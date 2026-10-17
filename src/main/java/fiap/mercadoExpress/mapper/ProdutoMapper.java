package fiap.mercadoExpress.mapper;

import fiap.mercadoExpress.dto.ProdutoDTO;
import fiap.mercadoExpress.model.Produto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoDTO toDto(Produto produto);

    Produto toEntity(ProdutoDTO dto);

    List<ProdutoDTO> toDtoList(List<Produto> produtos);
}
