package br.com.caelum.apigateway;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component
public class DistanciaRestClient {
	
	private String distanciaServiceUrl;
	private RestTemplate restTemplate;
	
	public DistanciaRestClient(@Value("${configuracao.distancia.service.url}") String distanciaServiceUrl,
			RestTemplate restTemplate) {
		this.distanciaServiceUrl = distanciaServiceUrl;
		this.restTemplate = restTemplate;
	}
	
	@HystrixCommand(fallbackMethod = "restauranteSemDistancia")
	Map<String, Object> porCepEId(String cep, Long restauranteId) {
		String url = distanciaServiceUrl + "/restaurantes/" + cep + "/restaurante/" + restauranteId;
		return restTemplate.getForObject(url, Map.class);
	}
	
	Map<String, Object> restauranteSemDistancia(String cep, Long restauranteId) {
		//busco do cache...
		return new HashMap<>();
	}
}








