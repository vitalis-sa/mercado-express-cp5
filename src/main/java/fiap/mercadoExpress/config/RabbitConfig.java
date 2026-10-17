package fiap.mercadoExpress.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara exchanges (direct, durable), queues (durable) e bindings do Mercado Express.
 * Mapa conforme CONVENCOES_E_DEPENDENCIAS.md (seção 3.2).
 */
@EnableRabbit
@Configuration
public class RabbitConfig {

    public static final String REQUEST_EXCHANGE = "pagamento-request-exchange";
    public static final String REQUEST_ROUTING_KEY = "pagamento-request-rout-key";
    public static final String REQUEST_QUEUE = "pagamento-request-queue";

    public static final String SUCESSO_EXCHANGE = "pagamento-response-sucesso-exchange";
    public static final String SUCESSO_ROUTING_KEY = "pagamento-response-sucesso-rout-key";
    public static final String SUCESSO_QUEUE = "pagamento-response-sucesso-queue";

    public static final String ERRO_EXCHANGE = "pagamento-response-erro-exchange";
    public static final String ERRO_ROUTING_KEY = "pagamento-response-erro-rout-key";
    public static final String ERRO_QUEUE = "pagamento-response-erro-queue";

    public static final String EMAIL_QUEUE = "ms.email";

    // --- Solicitação de pagamento (API -> Worker)
    @Bean DirectExchange requestExchange() { return new DirectExchange(REQUEST_EXCHANGE, true, false); }
    @Bean Queue requestQueue() { return new Queue(REQUEST_QUEUE, true); }
    @Bean Binding requestBinding() {
        return BindingBuilder.bind(requestQueue()).to(requestExchange()).with(REQUEST_ROUTING_KEY);
    }

    // --- Resposta de sucesso (Worker -> API)
    @Bean DirectExchange sucessoExchange() { return new DirectExchange(SUCESSO_EXCHANGE, true, false); }
    @Bean Queue sucessoQueue() { return new Queue(SUCESSO_QUEUE, true); }
    @Bean Binding sucessoBinding() {
        return BindingBuilder.bind(sucessoQueue()).to(sucessoExchange()).with(SUCESSO_ROUTING_KEY);
    }

    // --- Resposta de erro (Worker -> API)
    @Bean DirectExchange erroExchange() { return new DirectExchange(ERRO_EXCHANGE, true, false); }
    @Bean Queue erroQueue() { return new Queue(ERRO_QUEUE, true); }
    @Bean Binding erroBinding() {
        return BindingBuilder.bind(erroQueue()).to(erroExchange()).with(ERRO_ROUTING_KEY);
    }

    // --- Envio de e-mail (Default Exchange, fila direta)
    @Bean Queue emailQueue() { return new Queue(EMAIL_QUEUE, true); }
}
