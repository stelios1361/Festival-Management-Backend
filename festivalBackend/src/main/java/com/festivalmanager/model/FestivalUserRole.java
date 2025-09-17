package com.festivalmanager.model;

import com.festivalmanager.enums.FestivalRoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing the role of a user within a specific festival.
 * <p>
 * Each user can have a specific role in a festival, such as ARTIST, ORGANIZER,
 * or STAFF. The combination of festival, user, and role must be unique.
 * </p>
 */
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

    /** Unique identifier of this user-role mapping. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The festival to which this role applies. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    /** The user assigned this role within the festival. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Role of the user in this festival (e.g., ARTIST, ORGANIZER, STAFF). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalRoleType role;
}
