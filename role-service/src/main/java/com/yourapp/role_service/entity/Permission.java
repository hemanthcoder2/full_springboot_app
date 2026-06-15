package com.yourapp.role_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String resource;

    @Builder.Default
    private boolean canCreate=false;

    @Builder.Default
    private boolean canRead=false;

    @Builder.Default
    private boolean canUpdate=false;

    @Builder.Default
    private boolean canDelete=false;


}
