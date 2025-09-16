package com.festivalmanager.dto.festival;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorManagementDTO {

    private Set<String> foodStalls;
    private Set<String> merchandiseBooths;
}
