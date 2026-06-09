package com.yourapp.auth.controller;

import com.yourapp.auth.dto.AuthDto;
import com.yourapp.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<AuthDto.ApiResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(AuthDto.ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<AuthDto.ApiResponse> refresh(@Valid @RequestBody AuthDto.RefreshTokenRequest request) {
        AuthDto.TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(AuthDto.ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke refresh tokens", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<AuthDto.ApiResponse> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(AuthDto.ApiResponse.success("Logged out successfully", null));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user (internal use by Company service)")
    public ResponseEntity<AuthDto.ApiResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        AuthDto.UserInfo userInfo = authService.register(request);
        return ResponseEntity.ok(AuthDto.ApiResponse.success("User registered successfully", userInfo));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current logged-in user info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<AuthDto.ApiResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(AuthDto.ApiResponse.success("User info", userDetails.getUsername()));
    }
}
