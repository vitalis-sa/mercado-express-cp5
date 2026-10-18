package fiap.mercadoExpress.service;

import fiap.mercadoExpress.dto.DashboardDTO;
import fiap.mercadoExpress.enums.StatusEmail;
import fiap.mercadoExpress.mapper.PagamentoMapper;
import fiap.mercadoExpress.model.Compra;
import fiap.mercadoExpress.model.EmailModel;
import fiap.mercadoExpress.model.ItemCompra;
import fiap.mercadoExpress.repository.CompraRepository;
import fiap.mercadoExpress.repository.EmailRepository;
import fiap.mercadoExpress.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CompraRepository compraRepository;
    private final ProdutoRepository produtoRepository;
    private final EmailRepository emailRepository;
    private final PagamentoMapper pagamentoMapper;

    public DashboardDTO gerar() {
        List<Compra> todas = compraRepository.findAll();
        int aprovadas = 0;
        int recusadas = 0;
        int pendentes = 0;
        BigDecimal receita = BigDecimal.ZERO;
        BigDecimal lucro = BigDecimal.ZERO;
        Map<String, BigDecimal> vendasPorDia = new LinkedHashMap<>();
        Map<String, Integer> produtosVendidos = new LinkedHashMap<>();

        for (Compra c : todas) {
            switch (c.getStatus()) {
                case PENDENTE -> pendentes++;
                case RECUSADO -> recusadas++;
                case APROVADO -> {
                    aprovadas++;
                    receita = receita.add(c.getValorTotal());
                    vendasPorDia.merge(c.getDataCompra().toLocalDate().toString(), c.getValorTotal(), BigDecimal::add);
                    for (ItemCompra i : c.getItens()) {
                        BigDecimal qtd = BigDecimal.valueOf(i.getQuantidade());
                        lucro = lucro.add(i.getPrecoUnitario().subtract(i.getCustoUnitario()).multiply(qtd));
                        produtosVendidos.merge(i.getProdutoNome(), i.getQuantidade(), Integer::sum);
                    }
                }
            }
        }

        List<Compra> ultimasCompras = todas.stream()
                .sorted(Comparator.comparing(Compra::getDataCompra).reversed())
                .limit(10)
                .toList();

        List<EmailModel> emails = emailRepository.findAll();
        long emailsEnviados = emails.stream().filter(e -> e.getStatusEmail() == StatusEmail.SENT).count();
        List<EmailModel> ultimosEmails = emails.stream()
                .sorted(Comparator.comparing(EmailModel::getSendDateEmail,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .toList();

        return pagamentoMapper.toDashboardDto(
                todas.size(),
                aprovadas,
                recusadas,
                pendentes,
                receita,
                lucro,
                vendasPorDia,
                produtosVendidos,
                produtoRepository.findAll(),
                ultimasCompras,
                emailsEnviados,
                ultimosEmails
        );
    }
}
