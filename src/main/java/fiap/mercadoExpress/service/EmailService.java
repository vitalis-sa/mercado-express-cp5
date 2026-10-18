package fiap.mercadoExpress.service;

import fiap.mercadoExpress.enums.StatusEmail;
import fiap.mercadoExpress.mapper.EmailMapper;
import fiap.mercadoExpress.model.EmailModel;
import fiap.mercadoExpress.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender emailSender;
    private final EmailMapper emailMapper;
    private final JsonMapper jsonMapper;

    @Value("${app.mail.brevo-api-key:}")
    private String brevoApiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public EmailModel sendEmail(EmailModel emailModel) {
        emailModel.setSendDateEmail(LocalDateTime.now());
        try {
            Optional<String> chaveBrevo = Optional.ofNullable(brevoApiKey)
                    .map(String::trim)
                    .filter(key -> !key.isEmpty());

            if (chaveBrevo.isPresent()) {
                enviarViaBrevoHttp(emailModel, chaveBrevo.get());
            } else {
                emailSender.send(emailMapper.toMailMessage(emailModel));
            }
            emailModel.setStatusEmail(StatusEmail.SENT);
            log.info("E-mail enviado com sucesso para {} (Status: {}).", emailModel.getEmailTo(), emailModel.getStatusEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail: {}", e.getMessage());
            emailModel.setStatusEmail(StatusEmail.ERROR);
        } finally {
            emailRepository.save(emailModel);
        }
        return emailModel;
    }

    private void enviarViaBrevoHttp(EmailModel emailModel, String apiKey) throws Exception {
        Map<String, Object> body = Map.of(
                "sender", Map.of("name", "Mercado Express", "email", emailModel.getEmailFrom()),
                "to", List.of(Map.of("email", emailModel.getEmailTo())),
                "subject", emailModel.getSubject(),
                "textContent", emailModel.getText()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                .timeout(Duration.ofSeconds(10))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Optional.of(response.statusCode())
                .filter(code -> code >= 200 && code < 300)
                .orElseThrow(() -> new IllegalStateException("Erro Brevo HTTP " + response.statusCode() + ": " + response.body()));
    }
}
