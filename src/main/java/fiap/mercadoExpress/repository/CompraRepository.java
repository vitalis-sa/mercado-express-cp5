package fiap.mercadoExpress.repository;

import fiap.mercadoExpress.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    Optional<Compra> findByNumeroPedido(String numeroPedido);
    List<Compra> findByClienteEmailOrderByDataCompraDesc(String clienteEmail);
}
