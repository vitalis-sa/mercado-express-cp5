package fiap.mercadoExpress.service;

import fiap.mercadoExpress.config.RabbitConfig;
import fiap.mercadoExpress.dto.PagamentoDTO;
import fiap.mercadoExpress.enums.StatusPagamento;
import fiap.mercadoExpress.mapper.EmailMapper;
import fiap.mercadoExpress.mapper.PagamentoMapper;
import fiap.mercadoExpress.model.Compra;
import fiap.mercadoExpress.producer.PagamentoRequestProducer;
import fiap.mercadoExpress.repository.CompraRepository;
import fiap.mercadoExpress.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRequestProducer producer;
    private final CompraRepository compraRepository;
    private final ProdutoRepository produtoRepository;
    private final AmqpTemplate amqpTemplate;
    private final PagamentoMapper pagamentoMapper;
    private final EmailMapper emailMapper;
    private final JsonMapper jsonMapper;

    @Value("${app.mail.from}")
    private String emailFrom;

    public String solicitarPagamento(PagamentoDTO request) {
        obterOuCriarCompra(request);
        try {
            producer.integrar(request);
            return "Pagamento aguardando confirmacao...";
        } catch (Exception e) {
            log.error("Erro ao publicar solicitação de pagamento: {}", e.getMessage());
            return "Ocorreu um erro ao solicitar pagamento .. " + e.getMessage();
        }
    }

    @Transactional
    public void erroPagamento(String payload) {
        buscarOuCriarPendente(payload).ifPresent(compra -> {
            compra.setStatus(StatusPagamento.RECUSADO);
            compraRepository.save(compra);
            log.warn("Pedido {} foi recusado.", compra.getNumeroPedido());
        });
    }

    @Transactional
    public void sucessoPagamento(String payload) {
        buscarOuCriarPendente(payload).ifPresent(compra -> {
            compra.setStatus(StatusPagamento.APROVADO);
            baixarEstoque(compra);
            compraRepository.save(compra);
            log.info("Pedido {} foi aprovado.", compra.getNumeroPedido());
            enviarEmailConfirmacao(compra);
        });
    }

    private Compra obterOuCriarCompra(PagamentoDTO request) {
        return Optional.ofNullable(request.getNumeroPedido())
                .flatMap(compraRepository::findByNumeroPedido)
                .orElseGet(() -> {
                    Compra compra = pagamentoMapper.toCompra(request);
                    compra.adicionarItem(pagamentoMapper.toItemCompra(request));
                    return compraRepository.save(compra);
                });
    }

    private void baixarEstoque(Compra compra) {
        compra.getItens().forEach(item ->
                Optional.ofNullable(item.getProdutoId())
                        .flatMap(produtoRepository::findById)
                        .ifPresent(produto -> {
                            int estoqueAtual = Optional.ofNullable(produto.getEstoque()).orElse(0);
                            produto.setEstoque(Math.max(0, estoqueAtual - item.getQuantidade()));
                            produtoRepository.save(produto);
                        })
        );
    }

    private void enviarEmailConfirmacao(Compra compra) {
        Optional.ofNullable(compra.getClienteEmail())
                .filter(email -> !email.isBlank())
                .map(email -> emailMapper.toDto(emailMapper.toConfirmacaoEmail(compra, emailFrom)))
                .ifPresent(dto -> {
                    try {
                        amqpTemplate.convertAndSend(RabbitConfig.EMAIL_QUEUE, jsonMapper.writeValueAsString(dto));
                    } catch (Exception e) {
                        log.error("Erro ao enfileirar e-mail de confirmação: {}", e.getMessage());
                    }
                });
    }

    private Optional<Compra> buscarOuCriarPendente(String payload) {
        return Optional.ofNullable(jsonMapper.readValue(payload, PagamentoDTO.class))
                .filter(dto -> dto.getNumeroPedido() != null && !dto.getNumeroPedido().isBlank())
                .map(this::obterOuCriarCompra)
                .filter(compra -> compra.getStatus() == StatusPagamento.PENDENTE);
    }
}
