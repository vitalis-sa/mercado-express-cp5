package fiap.mercadoExpress.mapper;

import fiap.mercadoExpress.dto.PagamentoDTO;
import fiap.mercadoExpress.model.Compra;
import fiap.mercadoExpress.model.ItemCompra;
import fiap.mercadoExpress.model.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {

    @Mapping(target = "valor", source = "valorTotal")
    @Mapping(target = "produto", expression = "java(compra.getDescricao())")
    @Mapping(target = "emailCliente", source = "clienteEmail")
    PagamentoDTO toPagamentoDto(Compra compra);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clienteNome", ignore = true)
    @Mapping(target = "clienteEmail", source = "emailCliente")
    @Mapping(target = "valorTotal", source = "valor", defaultValue = "0")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataCompra", ignore = true)
    @Mapping(target = "itens", ignore = true)
    Compra toCompra(PagamentoDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroPedido", source = "numeroPedido")
    @Mapping(target = "clienteNome", source = "clienteNome")
    @Mapping(target = "clienteEmail", source = "clienteEmail")
    @Mapping(target = "valorTotal", constant = "0")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataCompra", ignore = true)
    @Mapping(target = "itens", ignore = true)
    Compra toNovaCompra(String numeroPedido, String clienteNome, String clienteEmail);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compra", ignore = true)
    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoNome", source = "produto.nome")
    @Mapping(target = "quantidade", source = "quantidade")
    @Mapping(target = "precoUnitario", source = "produto.preco")
    @Mapping(target = "custoUnitario", source = "produto.custo", defaultValue = "0")
    ItemCompra toItemCompra(Produto produto, Integer quantidade);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compra", ignore = true)
    @Mapping(target = "produtoId", ignore = true)
    @Mapping(target = "produtoNome", source = "produto", defaultValue = "Pedido API")
    @Mapping(target = "quantidade", constant = "1")
    @Mapping(target = "precoUnitario", source = "valor", defaultValue = "0")
    @Mapping(target = "custoUnitario", constant = "0")
    ItemCompra toItemCompra(PagamentoDTO dto);
}
