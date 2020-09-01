package br.com.caelum.eats.pagamento;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import br.com.caelum.eats.pagamento.AmqpPagamentoConfig.PagamentoSource;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
class NotificadorDePagamentoConfirmado {

	private PagamentoSource source;
	
	void notifica(Pagamento pagamento) {
		Long pagamentoId = pagamento.getId();
		Long pedidoId = pagamento.getPedidoId();
		PagamentoConfirmado payload = new PagamentoConfirmado(pagamentoId, pedidoId);
		Message<PagamentoConfirmado> message = MessageBuilder.withPayload(payload).build();
		source.pagamentosConfirmados().send(message);
	}
	
	
}
