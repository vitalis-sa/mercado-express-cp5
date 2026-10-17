package fiap.mercadoExpress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoDTO {
    private String numeroPedido;
    private BigDecimal valor;
    private String produto;
    /** Cliente que receberá o e-mail de confirmação (opcional na API direta). */
    private String emailCliente;
}
