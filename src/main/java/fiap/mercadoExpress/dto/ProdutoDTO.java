package fiap.mercadoExpress.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {

    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    @NotBlank(message = "O tipo do produto é obrigatório")
    private String tipo;

    @NotBlank(message = "O setor do produto é obrigatório")
    private String setor;

    @NotBlank(message = "O tamanho do produto é obrigatório")
    private String tamanho;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    private Double preco;

    @Min(value = 0, message = "O custo não pode ser negativo")
    private Double custo = 0.0;

    @Min(value = 0, message = "O estoque não pode ser negativo")
    private Integer estoque = 0;
}
