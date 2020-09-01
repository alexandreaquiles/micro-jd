package br.com.caelum.eats.pagamento;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/pagamentos")
@AllArgsConstructor
@Slf4j
class PagamentoController {

	private PagamentoRepository pagamentoRepo;
	private PedidoRestClient pedidoRestClient;
	private NotificadorDePagamentoConfirmado notificadorDePagamentoConfirmado;
	
	@GetMapping("/{id}")
	PagamentoDto detalha(@PathVariable("id") Long id) {
		Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException());
		return new PagamentoDto(pagamento);
	}

	@PostMapping
	ResponseEntity<PagamentoDto> cria(@RequestBody Pagamento pagamento, UriComponentsBuilder uriBuilder) {
		pagamento.setStatus(Pagamento.Status.CRIADO);
		Pagamento salvo = pagamentoRepo.save(pagamento);
		URI path = uriBuilder.path("/pagamentos/{id}").buildAndExpand(salvo.getId()).toUri();
		return ResponseEntity.created(path).body(new PagamentoDto(salvo));
	}

	@PutMapping("/{id}")
	PagamentoDto confirma(@PathVariable("id") Long id) {
		Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException());
		pagamento.setStatus(Pagamento.Status.CONFIRMADO);
		pagamentoRepo.save(pagamento);
		
		Long pedidoId = pagamento.getPedidoId();
		log.info("Serviço de pagamentos vai chamar o monólito para avisar que o pedido foi pago: " + pedidoId);
		pedidoRestClient.avisaQueFoiPago(pedidoId );
		
		//produzir uma mensagem
		notificadorDePagamentoConfirmado.notifica(pagamento);
		
		return new PagamentoDto(pagamento);
	}

	@DeleteMapping("/{id}")
	PagamentoDto cancela(@PathVariable("id") Long id) {
		Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException());
		pagamento.setStatus(Pagamento.Status.CANCELADO);
		pagamentoRepo.save(pagamento);
		return new PagamentoDto(pagamento);
	}

}