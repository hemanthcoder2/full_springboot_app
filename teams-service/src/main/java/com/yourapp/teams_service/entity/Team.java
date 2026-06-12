package com.yourapp.teams_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="teams")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="team_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String teamName;
    private String teamDescription;
    @Column(nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Team parent;

    @OneToMany(mappedBy = "parent", cascade=CascadeType.ALL)
    @Builder.Default
    private List<Team> children = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TeamStatus status = TeamStatus.ACTIVE;

    @ElementCollection
    @CollectionTable(name="team_members", joinColumns = @JoinColumn(name="team_id"))
    @Column(name="employee_id")
    @Builder.Default
    private List<Long> memberIds = new ArrayList<>();

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;



    public enum TeamStatus{
        ACTIVE,
        INACTIVE
    }



}
