package br.com.caelum.eats.pagamento;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient("monolito")
public interface PedidoRestClient {
	
	@PutMapping("/pedidos/{id}/pago")
	void avisaQueFoiPago(@PathVariable("id") Long pedidoId);

}
