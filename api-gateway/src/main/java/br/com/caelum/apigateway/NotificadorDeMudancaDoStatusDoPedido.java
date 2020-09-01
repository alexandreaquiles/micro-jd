package br.com.caelum.apigateway;

import java.util.Map;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import br.com.caelum.apigateway.AmqpApiGatewayConfig.PedidoAtualizadoSink;

@Component
public class NotificadorDeMudancaDoStatusDoPedido {

	private final SimpMessagingTemplate websocket;

	public NotificadorDeMudancaDoStatusDoPedido(SimpMessagingTemplate websocket) {
		this.websocket = websocket;
	}
	
	@StreamListener(PedidoAtualizadoSink.PEDIDO_COM_STATUS_ATUALIZADO)
	void pedidoAtualizado(Map<String, Object> pedido) {
		websocket.convertAndSend("/pedidos/"+pedido.get("id")+"/status", pedido);
		
		if ("PAGO".equals(pedido.get("status"))) {
			Map<String, Object> restaurante = (Map<String, Object>) pedido.get("restaurante");
			websocket.convertAndSend("/parceiros/restaurantes/"+restaurante.get("id")+"/pedidos/pendentes", pedido);
		}
		
	}
	
	
	
}
