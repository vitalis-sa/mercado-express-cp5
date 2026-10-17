package fiap.mercadoExpress.controller;

import fiap.mercadoExpress.dto.PagamentoDTO;
import fiap.mercadoExpress.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping
    public String processar(@RequestBody PagamentoDTO request) {
        return pagamentoService.solicitarPagamento(request);
    }
}
