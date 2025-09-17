package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetDTO {

    private Double tracking;
    private Double costs;
    private Double logistics;
    private Double expectedRevenue;
}
