package com.mitkov.weatherapp.WeatherApp.config;

import com.mitkov.weatherapp.WeatherApp.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final RegistrationService registrationService;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner initDefaultAdmin() {
        return args -> registrationService.createDefaultAdminIfNotExists();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
