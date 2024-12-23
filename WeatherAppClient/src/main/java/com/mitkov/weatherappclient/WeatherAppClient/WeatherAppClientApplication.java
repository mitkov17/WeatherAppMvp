package com.mitkov.weatherappclient.WeatherAppClient;

import com.mitkov.weatherappclient.WeatherAppClient.services.SensorClientService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WeatherAppClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherAppClientApplication.class, args)
				.getBean(SensorClientService.class)
				.sendMeasurements();
//		SpringApplication.run(WeatherAppClientApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
