package fiap.mercadoExpress.producer;

import fiap.mercadoExpress.config.RabbitConfig;
import fiap.mercadoExpress.dto.PagamentoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class PagamentoRequestProducer {

    private final AmqpTemplate amqpTemplate;
    private final JsonMapper jsonMapper;

    public void integrar(PagamentoDTO pagamento) {
        amqpTemplate.convertAndSend(
                RabbitConfig.REQUEST_EXCHANGE,
                RabbitConfig.REQUEST_ROUTING_KEY,
                jsonMapper.writeValueAsString(pagamento));
    }
}
