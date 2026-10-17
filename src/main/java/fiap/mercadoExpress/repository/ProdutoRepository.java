package fiap.mercadoExpress.repository;

import fiap.mercadoExpress.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCaseOrTipoContainingIgnoreCaseOrSetorContainingIgnoreCase(
            String nome, String tipo, String setor);
}
