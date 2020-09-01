package br.com.caelum.eats.restaurante;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DistanciaRestClient {
	
	private String distanciaServiceUrl;
	private RestTemplate restTemplate;
	
	public DistanciaRestClient(RestTemplate restTemplate,
			@Value("${configuracao.distancia.service.url}") String distanciaServiceUrl) {
		this.restTemplate = restTemplate;
		this.distanciaServiceUrl = distanciaServiceUrl;
	}
	
	void novoRestauranteAprovado(Restaurante restaurante) {
		RestauranteParaDistancia restauranteParaDistancia = new RestauranteParaDistancia(restaurante);
		String url = distanciaServiceUrl + "/restaurantes";
		ResponseEntity<RestauranteParaDistancia> responseEntity = 
				restTemplate.postForEntity(url, restauranteParaDistancia, RestauranteParaDistancia.class);
		HttpStatus statusCode = responseEntity.getStatusCode();
		if (!HttpStatus.CREATED.equals(statusCode)) {
			throw new RuntimeException("Status diferente do esperado: " + statusCode);
		}
	}
	
	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
	void restauranteAtualizado(Restaurante restaurante) {
		RestauranteParaDistancia restauranteParaDistancia = new RestauranteParaDistancia(restaurante);
		String url = distanciaServiceUrl + "/restaurantes/" + restaurante.getId();
		log.info("Invocando serviço de distância na URL {}. Atualizando o restaurante {}", url, restauranteParaDistancia);
		restTemplate.put(url, restauranteParaDistancia, RestauranteParaDistancia.class);
	}

}
