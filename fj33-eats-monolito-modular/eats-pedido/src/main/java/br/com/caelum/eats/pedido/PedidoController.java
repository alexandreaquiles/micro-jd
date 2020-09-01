package br.com.caelum.eats.pedido;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.caelum.eats.AmqpPedidoConfig.PedidoAtualizadoSource;
import br.com.caelum.eats.pedido.Pedido.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
class PedidoController {

	private PedidoRepository repo;
	private PedidoAtualizadoSource pedidoAtualizado;

	@GetMapping("/pedidos")
	List<PedidoDto> lista() {
		return repo.findAll().stream()
				.map(pedido -> new PedidoDto(pedido)).collect(Collectors.toList());
	}


	@GetMapping("/pedidos/{id}")
	PedidoDto porId(@PathVariable("id") Long id) {
		Pedido pedido = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException());
		return new PedidoDto(pedido);
	}

	@PostMapping("/pedidos")
	PedidoDto adiciona(@RequestBody Pedido pedido) {
		pedido.setDataHora(LocalDateTime.now());
		pedido.setStatus(Pedido.Status.REALIZADO);
		pedido.getItens().forEach(item -> item.setPedido(pedido));
		pedido.getEntrega().setPedido(pedido);
		Pedido salvo = repo.save(pedido);
		return new PedidoDto(salvo);
	}

	@PutMapping("/pedidos/{id}/status")
	PedidoDto atualizaStatus(@RequestBody Pedido pedido) {
		repo.atualizaStatus(pedido.getStatus(), pedido);
		PedidoDto pedidoDto = new PedidoDto(pedido);
		
		Message<PedidoDto> message = MessageBuilder.withPayload(pedidoDto).build();
		pedidoAtualizado.pedidoComStatusAtualizado().send(message);
		
		return pedidoDto;
	}

	@GetMapping("/parceiros/restaurantes/{restauranteId}/pedidos/pendentes")
	List<PedidoDto> pendentes(@PathVariable("restauranteId") Long restauranteId) {
		return repo.doRestauranteSemOsStatus(restauranteId, Arrays.asList(Pedido.Status.REALIZADO, Pedido.Status.ENTREGUE)).stream()
				.map(pedido -> new PedidoDto(pedido)).collect(Collectors.toList());
	}

	@PutMapping("/pedidos/{id}/pago")
	void pago(@PathVariable("id") Long id) {
		Pedido pedido = repo.porIdComItens(id);
		if (pedido == null) {
			throw new ResourceNotFoundException();
		}
		pedido.setStatus(Status.PAGO);
		repo.atualizaStatus(Status.PAGO, pedido);
		
		log.info("Pedido atualizado como PAGO: " + pedido.getId());

		PedidoDto pedidoDto = new PedidoDto(pedido);
		Message<PedidoDto> message = MessageBuilder.withPayload(pedidoDto ).build();
		pedidoAtualizado.pedidoComStatusAtualizado().send(message);

	}
	
}
