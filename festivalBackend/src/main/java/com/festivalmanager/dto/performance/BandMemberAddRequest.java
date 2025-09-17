package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to add a new band member to an existing performance.
 * <p>
 * This object is sent by the main artist or authorized user to request
 * that a new member be added to the band associated with a performance.
 * </p>
 */
@Getter
@Setter
public class BandMemberAddRequest {

    /** 
     * The username of the requester performing the addition. 
     * Typically the main artist or another authorized user. 
     */
    private String requesterUsername;

    /** 
     * Authentication token of the requester to validate the operation. 
     */
    private String token;

    /** 
     * The ID of the performance to which the new member will be added. 
     */
    private Long performanceId;

    /** 
     * The username of the user to be added as a new band member. 
     */
    private String newMemberUsername;
}
