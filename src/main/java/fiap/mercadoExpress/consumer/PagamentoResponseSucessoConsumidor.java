package fiap.mercadoExpress.consumer;

import fiap.mercadoExpress.config.RabbitConfig;
import fiap.mercadoExpress.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PagamentoResponseSucessoConsumidor {

    private final PagamentoService pagamentoService;

    @RabbitListener(queues = {RabbitConfig.SUCESSO_QUEUE})
    public void receive(@Payload String payload) {
        pagamentoService.sucessoPagamento(payload);
    }
}
