package br.com.caelum.eats.distancia;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
public class RestaurantesController {

	private RestauranteRepository repo;
	
	@PostMapping("/restaurantes")
	ResponseEntity<Restaurante> adicionaNovoRestaurante(@RequestBody Restaurante restaurante,
			UriComponentsBuilder uriBuilder) {
		log.info("Inserindo novo restaurante: " + restaurante);
		Restaurante salvo = repo.insert(restaurante);
		URI location = uriBuilder.path("/restaurantes/{id}").buildAndExpand(salvo.getId()).toUri();
		return ResponseEntity.created(location).body(salvo);
	}
	
	@PutMapping("/restaurantes/{id}")
	Restaurante atualizaRestauranteExistente(@PathVariable("id") Long id, @RequestBody Restaurante restaurante) {
		if (!repo.existsById(id)) {
			throw new ResourceNotFoundException();
		}
		log.info("Atualizando restaurante: " + restaurante);
		return repo.save(restaurante);
	}
	
}
