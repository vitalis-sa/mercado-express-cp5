package fiap.mercadoExpress.consumer;

import fiap.mercadoExpress.config.RabbitConfig;
import fiap.mercadoExpress.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PagamentoResponseErroConsumidor {

    private final PagamentoService pagamentoService;

    @RabbitListener(queues = {RabbitConfig.ERRO_QUEUE})
    public void receive(@Payload String payload) {
        pagamentoService.erroPagamento(payload);
    }
}
