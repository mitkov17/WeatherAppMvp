package com.mitkov.weatherappcommon;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasurementDTO {

    private Boolean raining;

    private MeasurementUnit measurementUnit;

    private Double measurementValue;

    private Long sensorId;
}
