package fiap.mercadoExpress.consumer;

import fiap.mercadoExpress.dto.EmailDto;
import fiap.mercadoExpress.mapper.EmailMapper;
import fiap.mercadoExpress.model.EmailModel;
import fiap.mercadoExpress.service.EmailService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;
    private final EmailMapper emailMapper;
    private final JsonMapper jsonMapper;
    private final Validator validator;

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void listen(@Payload String payload) {
        EmailDto dto = parsePayload(payload);
        if (dto == null || !isValid(dto)) {
            return;
        }
        EmailModel model = emailMapper.toModel(dto);
        emailService.sendEmail(model);
    }

    private EmailDto parsePayload(String payload) {
        try {
            return jsonMapper.readValue(payload, EmailDto.class);
        } catch (Exception e) {
            log.error("Mensagem inválida na fila ms.email: {}", e.getMessage());
            return null;
        }
    }

    private boolean isValid(EmailDto dto) {
        Set<ConstraintViolation<EmailDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            log.warn("EmailDto inválido na fila ms.email: {}", violations);
            return false;
        }
        return true;
    }
}
