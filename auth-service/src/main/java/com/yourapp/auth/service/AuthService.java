package com.yourapp.auth.service;

import com.yourapp.auth.dto.AuthDto;
import com.yourapp.auth.entity.RefreshToken;
import com.yourapp.auth.entity.User;
import com.yourapp.auth.repository.RefreshTokenRepository;
import com.yourapp.auth.repository.UserRepository;
import com.yourapp.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiry;

    // ── Login ─────────────────────────────────────────────────────

    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // Revoke existing refresh tokens
        refreshTokenRepository.revokeAllUserTokens(user);

        String accessToken = jwtUtil.generateAccessToken(userDetails, user.getCompanyId(), user.getRole().name());
        String refreshToken = createRefreshToken(user);

        log.info("User logged in: {}", user.getEmail());

        return AuthDto.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiry / 1000)
                .user(AuthDto.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .companyId(user.getCompanyId())
                        .role(user.getRole().name())
                        .build())
                .build();
    }

    // ── Refresh token ─────────────────────────────────────────────

    @Transactional
    public AuthDto.TokenResponse refreshToken(AuthDto.RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (storedToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (storedToken.isExpired()) {
            throw new RuntimeException("Refresh token has expired. Please login again");
        }

        User user = storedToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // Rotate refresh token
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        String newAccessToken = jwtUtil.generateAccessToken(userDetails, user.getCompanyId(), user.getRole().name());
        String newRefreshToken = createRefreshToken(user);

        log.info("Token refreshed for user: {}", user.getEmail());

        return AuthDto.TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiry / 1000)
                .build();
    }

    // ── Logout ────────────────────────────────────────────────────

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("User logged out: {}", email);
    }

    // ── Register (called internally by Company service) ───────────

    @Transactional
    public AuthDto.UserInfo register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .companyId(request.getCompanyId())
                .role(request.getRole() != null ? request.getRole() : User.Role.EMPLOYEE)
                .status(User.UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {} for company: {}", user.getEmail(), user.getCompanyId());

        return AuthDto.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .companyId(user.getCompanyId())
                .role(user.getRole().name())
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────

    private String createRefreshToken(User user) {
        String tokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiry / 1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }
}
