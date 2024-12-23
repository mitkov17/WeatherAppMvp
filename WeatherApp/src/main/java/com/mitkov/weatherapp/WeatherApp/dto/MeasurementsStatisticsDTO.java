package com.mitkov.weatherapp.WeatherApp.dto;

import com.mitkov.weatherappcommon.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementsStatisticsDTO {

    private MeasurementUnit measurementUnit;

    private Double avgValue;

    private Double minValue;

    private Double maxValue;

    private Long count;
}
