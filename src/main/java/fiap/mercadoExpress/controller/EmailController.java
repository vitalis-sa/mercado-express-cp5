package fiap.mercadoExpress.controller;

import fiap.mercadoExpress.dto.EmailDto;
import fiap.mercadoExpress.mapper.EmailMapper;
import fiap.mercadoExpress.model.EmailModel;
import fiap.mercadoExpress.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final EmailMapper emailMapper;

    @PostMapping("/sending-email")
    public ResponseEntity<EmailModel> sendingEmail(@RequestBody @Valid EmailDto emailDto) {
        EmailModel emailModel = emailMapper.toModel(emailDto);
        emailService.sendEmail(emailModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(emailModel);
    }
}
