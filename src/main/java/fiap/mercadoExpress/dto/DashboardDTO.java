package fiap.mercadoExpress.dto;

import fiap.mercadoExpress.model.Compra;
import fiap.mercadoExpress.model.EmailModel;
import fiap.mercadoExpress.model.Produto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class DashboardDTO {
    private long totalCompras;
    private long aprovadas;
    private long recusadas;
    private long pendentes;
    private long emailsEnviados;
    private BigDecimal receita = BigDecimal.ZERO;
    private BigDecimal lucro = BigDecimal.ZERO;
    private Map<String, BigDecimal> vendasPorDia = new TreeMap<>();
    private Map<String, Integer> produtosVendidos = new TreeMap<>();
    private List<Produto> estoque = new ArrayList<>();
    private List<Compra> ultimasCompras = new ArrayList<>();
    private List<EmailModel> ultimosEmails = new ArrayList<>();
}
