package br.com.caelum.apigateway;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RestauranteRestClientFallback implements RestauranteRestClient {

	@Override
	public Map<String, Object> porId(Long id) {
		// busca do cache...
		return new HashMap<>();
	}

}
