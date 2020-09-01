package br.com.caelum.apigateway;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestauranteComDistanciaController {

	private DistanciaRestClient distanciaRestClient;
	private RestauranteRestClient restauranteRestClient;

	public RestauranteComDistanciaController(DistanciaRestClient distanciaRestClient,
			RestauranteRestClient restauranteRestClient) {
		this.distanciaRestClient = distanciaRestClient;
		this.restauranteRestClient = restauranteRestClient;
	}

	@GetMapping("/restaurantes-com-distancia/{cep}/restaurante/{restauranteId}")
	Map<String, Object> porCepEIdComDistancia(@PathVariable("cep") String cep, @PathVariable("restauranteId") Long restauranteId) {
		Map<String, Object> dadosDoRestaurante = restauranteRestClient.porId(restauranteId);
		Map<String, Object> dadosDeDistancia = distanciaRestClient.porCepEId(cep, restauranteId);
		dadosDoRestaurante.putAll(dadosDeDistancia);
		return dadosDoRestaurante;
	}
	
	
	
}
