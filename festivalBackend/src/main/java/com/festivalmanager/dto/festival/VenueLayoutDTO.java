package com.festivalmanager.dto.festival;


import java.util.Set;
import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    public  class VenueLayoutDTO {

        private Set<String> stages;
        private Set<String> vendorAreas;
        private Set<String> facilities; // New field
    }