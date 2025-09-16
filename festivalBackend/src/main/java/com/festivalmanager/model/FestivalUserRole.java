package com.festivalmanager.model;

import com.festivalmanager.enums.FestivalRoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "festival_user_roles",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"festival_id", "user_id", "role"})
    }
)
public class FestivalUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Festival */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    
    /** User */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Role for this user in this festival */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalRoleType role;

}
