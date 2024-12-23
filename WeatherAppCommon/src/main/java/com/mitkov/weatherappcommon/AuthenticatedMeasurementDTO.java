package com.mitkov.weatherappcommon;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticatedMeasurementDTO {
    private MeasurementDTO measurementDTO;
    private String jwtToken;
}
