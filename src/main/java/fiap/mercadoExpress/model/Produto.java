package fiap.mercadoExpress.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "TDS_Sec_MVC_TB_Mercado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String setor;

    @Column(nullable = false)
    private String tamanho;

    @Column(nullable = false)
    private Double preco;

    private Double custo = 0.0;

    private Integer estoque = 0;
}
