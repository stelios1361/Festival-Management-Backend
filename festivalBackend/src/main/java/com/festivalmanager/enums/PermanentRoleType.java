package com.festivalmanager.enums;

/**
 * Enumeration representing the permanent role assigned to a user in the system.
 * <p>
 * These roles are global but only affect user-related permissions.
 * </p>
 */
public enum PermanentRoleType {

    /** User manager role; can manage other users but not festivals or performances. */
    ADMIN,

    /** Regular user with standard access; permissions may depend on festival-specific roles. */
    USER
}
