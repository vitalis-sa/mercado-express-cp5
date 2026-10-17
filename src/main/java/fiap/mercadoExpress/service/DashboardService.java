package fiap.mercadoExpress.service;

import fiap.mercadoExpress.dto.DashboardDTO;
import fiap.mercadoExpress.enums.StatusEmail;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CompraRepository compraRepository;
    private final ProdutoRepository produtoRepository;
    private final EmailRepository emailRepository;

    public DashboardDTO gerar() {
        DashboardDTO d = new DashboardDTO();
        List<Compra> todas = compraRepository.findAll();
        d.setTotalCompras(todas.size());

        for (Compra c : todas) {
            switch (c.getStatus()) {
                case PENDENTE -> d.setPendentes(d.getPendentes() + 1);
                case RECUSADO -> d.setRecusadas(d.getRecusadas() + 1);
                case APROVADO -> {
                    d.setAprovadas(d.getAprovadas() + 1);
                    d.setReceita(d.getReceita().add(c.getValorTotal()));
                    d.getVendasPorDia().merge(c.getDataCompra().toLocalDate().toString(), c.getValorTotal(), BigDecimal::add);
                    for (ItemCompra i : c.getItens()) {
                        BigDecimal qtd = BigDecimal.valueOf(i.getQuantidade());
                        d.setLucro(d.getLucro().add(i.getPrecoUnitario().subtract(i.getCustoUnitario()).multiply(qtd)));
                        d.getProdutosVendidos().merge(i.getProdutoNome(), i.getQuantidade(), Integer::sum);
                    }
                }
            }
        }
        d.setEstoque(produtoRepository.findAll());
        d.setUltimasCompras(todas.stream()
                .sorted(Comparator.comparing(Compra::getDataCompra).reversed())
                .limit(10).toList());

        List<EmailModel> emails = emailRepository.findAll();
        d.setEmailsEnviados(emails.stream().filter(e -> e.getStatusEmail() == StatusEmail.SENT).count());
        d.setUltimosEmails(emails.stream()
                .sorted(Comparator.comparing(EmailModel::getSendDateEmail,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10).toList());
        return d;
    }
}
