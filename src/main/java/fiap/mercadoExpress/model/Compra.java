package fiap.mercadoExpress.model;

import fiap.mercadoExpress.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TDS_TB_Compras_Mercado")
@Getter
@Setter
@NoArgsConstructor
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroPedido;

    private String clienteNome;

    private String clienteEmail;

    @Column(nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    private LocalDateTime dataCompra = LocalDateTime.now();

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemCompra> itens = new ArrayList<>();

    public void adicionarItem(ItemCompra item) {
        item.setCompra(this);
        itens.add(item);
    }

    public String getDescricao() {
        StringBuilder sb = new StringBuilder();
        for (ItemCompra i : itens) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(i.getQuantidade()).append("x ").append(i.getProdutoNome());
        }
        return sb.toString();
    }
}
