package fiap.mercadoExpress.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "TDS_TB_Itens_Compra")
@Getter
@Setter
@NoArgsConstructor
public class ItemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "compra_id")
    private Compra compra;

    /** Pode ser nulo quando a compra vem direto da API /pagamentos. */
    private Long produtoId;

    @Column(nullable = false)
    private String produtoNome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private BigDecimal custoUnitario = BigDecimal.ZERO;
}
