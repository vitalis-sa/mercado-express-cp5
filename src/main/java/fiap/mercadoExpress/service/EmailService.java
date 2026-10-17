package fiap.mercadoExpress.service;

import fiap.mercadoExpress.enums.StatusEmail;
import fiap.mercadoExpress.model.EmailModel;
import fiap.mercadoExpress.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender emailSender;

    public EmailModel sendEmail(EmailModel emailModel) {
        emailModel.setSendDateEmail(LocalDateTime.now());
        try {
            emailSender.send(buildMailMessage(emailModel));
            emailModel.setStatusEmail(StatusEmail.SENT);
            log.info("E-mail enviado com sucesso (Status: {}).", emailModel.getStatusEmail());
        } catch (MailException e) {
            log.error("Falha ao enviar e-mail: {}", e.getMessage());
            emailModel.setStatusEmail(StatusEmail.ERROR);
        } finally {
            emailRepository.save(emailModel);
        }
        return emailModel;
    }

    private SimpleMailMessage buildMailMessage(EmailModel emailModel) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailModel.getEmailFrom());
        message.setTo(emailModel.getEmailTo());
        message.setSubject(emailModel.getSubject());
        message.setText(emailModel.getText());
        return message;
    }
}
