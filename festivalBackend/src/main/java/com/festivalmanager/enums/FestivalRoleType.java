package com.festivalmanager.enums;

/**
 * Enumeration representing the role of a user within a specific festival.
 * <p>
 * These roles determine what actions a user can perform and which details
 * they can view for that festival.
 * </p>
 */
public enum FestivalRoleType {

    /** Organizer of the festival; can manage festival details and approve performances. */
    ORGANIZER,

    /** Staff member assigned to review or manage performances at the festival. */
    STAFF,

    /** Artist performing at the festival; can manage own performance and band members. */
    ARTIST
}
