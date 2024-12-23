package com.mitkov.weatherappclient.WeatherAppClient.services;

import com.mitkov.weatherappcommon.AuthenticatedMeasurementDTO;
import com.mitkov.weatherappcommon.SensorAuthRequestDTO;
import com.mitkov.weatherappcommon.MeasurementDTO;
import com.mitkov.weatherappcommon.MeasurementUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SensorClientService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(SensorClientService.class);

    @Value("${spring.kafka.topic.measurements}")
    private String measurementsTopic;

    private final RestTemplate restTemplate;

    @Value("${sensor.api.url}")
    private String apiUrl;

    @Value("${sensor.username}")
    private String sensorUsername;

    @Value("${sensor.password}")
    private String sensorPassword;

    private String jwtToken;

    public SensorClientService(RestTemplate restTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    public void authenticate() {
        SensorAuthRequestDTO sensorAuthRequestDTO = new SensorAuthRequestDTO();
        sensorAuthRequestDTO.setUsername(sensorUsername);
        sensorAuthRequestDTO.setPassword(sensorPassword);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl + "/auth/login-sensor", sensorAuthRequestDTO, String.class);

        if (response.getBody() != null) {
            this.jwtToken = response.getBody();
        } else {
            throw new RuntimeException("Failed to authenticate sensor.");
        }
    }

//    public void sendMeasurements() {
//        for (int j = 0; j < 5; j++) {
//            MeasurementDTO measurementDTO = new MeasurementDTO();
//            measurementDTO.setMeasurementUnit(MeasurementUnit.DEGREES_CELSIUS);
//            measurementDTO.setMeasurementValue(generateRandomValue());
//            measurementDTO.setRaining(generateRandomBoolean());
//            measurementDTO.setSensorId(3L);
//
//            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(measurementsTopic, measurementDTO);
//
//            future.whenComplete((result, ex) -> {
//                if (ex != null) {
//                    logger.error("Message failed: {}", ex.getMessage());
//                } else {
//                    logger.info("Message sent: {}", result.getProducerRecord().value());
//                }
//            });
//        }
//    }


    public void sendMeasurements() {

        if (jwtToken == null) {
            authenticate();
        }

        for (int j = 0; j < 2; j++) {
            MeasurementDTO measurementDTO = new MeasurementDTO();
            measurementDTO.setMeasurementUnit(MeasurementUnit.DEGREES_CELSIUS);
//            measurementDTO.setMeasurementUnit("DEGREES_CELSIUS");
            measurementDTO.setMeasurementValue(generateRandomValue());
            measurementDTO.setRaining(generateRandomBoolean());
            measurementDTO.setSensorId(3L);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);
            HttpEntity<MeasurementDTO> requestEntity = new HttpEntity<>(measurementDTO, headers);

//            restTemplate.postForEntity(apiUrl + "/measurements/add", requestEntity, Void.class);

            AuthenticatedMeasurementDTO authenticatedMeasurementDTO = new AuthenticatedMeasurementDTO();
            authenticatedMeasurementDTO.setMeasurementDTO(measurementDTO);
            authenticatedMeasurementDTO.setJwtToken(jwtToken);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(measurementsTopic, authenticatedMeasurementDTO);

//            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(measurementsTopic, measurementDTO);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    logger.error("Message failed: {}", ex.getMessage());
                } else {
                    logger.info("Message sent: {}", result.getProducerRecord().value());
                }
            });
        }
    }

    private double generateRandomValue() {
        return ThreadLocalRandom.current().nextDouble(-100, 100);
    }

    private boolean generateRandomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
