package com.mitkov.weatherapp.WeatherApp.kafka;

import com.mitkov.weatherapp.WeatherApp.converters.MeasurementConverter;
import com.mitkov.weatherapp.WeatherApp.entities.Role;
import com.mitkov.weatherapp.WeatherApp.entities.UserClaims;
import com.mitkov.weatherapp.WeatherApp.security.JWTUtil;
import com.mitkov.weatherapp.WeatherApp.services.MeasurementService;
import com.mitkov.weatherapp.WeatherApp.services.SensorDetailsService;
import com.mitkov.weatherappcommon.AuthenticatedMeasurementDTO;
import com.mitkov.weatherappcommon.MeasurementDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@KafkaListener(topics = "weather-sensor-measurements", groupId = "weather-sensor-group")
@RequiredArgsConstructor
public class MeasurementEventHandler {

    private final Logger logger = LoggerFactory.getLogger(MeasurementEventHandler.class);
    private final JWTUtil jwtUtil;

    private final MeasurementService measurementService;

    private final MeasurementConverter measurementConverter;

    private final SensorDetailsService sensorDetailsService;

    @KafkaHandler
    public void handle(AuthenticatedMeasurementDTO authenticatedMeasurementDTO) {
        String jwtToken = authenticatedMeasurementDTO.getJwtToken();
        MeasurementDTO measurementDTO = authenticatedMeasurementDTO.getMeasurementDTO();

        try {
            UserClaims userClaims = jwtUtil.validateTokenAndRetrieveClaims(jwtToken);
            UserDetails userDetails;
            if (userClaims.getRole().equals(Role.ROLE_SENSOR.name())) {
                userDetails = sensorDetailsService.loadUserByUsername(userClaims.getUsername());
            } else {
                throw new SecurityException("Unauthorized role");
            }

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userClaims.getRole());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            measurementService.addMeasurement(measurementConverter.convertToMeasurement(measurementDTO));
        } catch (Exception e) {
            logger.error("Error processing measurement: {}", e.getMessage(), e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object unknownMessage) {
        logger.warn("Received unknown message: {}", unknownMessage);
    }
}
