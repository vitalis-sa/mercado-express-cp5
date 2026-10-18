package fiap.mercadoExpress.service;

import fiap.mercadoExpress.mapper.PagamentoMapper;
import fiap.mercadoExpress.model.Compra;
import fiap.mercadoExpress.model.ItemCompra;
import fiap.mercadoExpress.model.Produto;
import fiap.mercadoExpress.model.Usuario;
import fiap.mercadoExpress.repository.CompraRepository;
import fiap.mercadoExpress.repository.ProdutoRepository;
import fiap.mercadoExpress.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagamentoMapper pagamentoMapper;

    @Transactional
    public Compra criarPedido(Map<Long, Integer> itensCarrinho, String usuarioLogado) {
        Map<Long, Integer> itensValidos = Optional.ofNullable(itensCarrinho)
                .filter(map -> !map.isEmpty())
                .orElseThrow(() -> new IllegalStateException("Carrinho vazio."));

        String numeroPedido = "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Optional<Usuario> usuarioOpt = buscarUsuario(usuarioLogado);
        Compra compra = pagamentoMapper.toNovaCompra(
                numeroPedido,
                usuarioOpt.map(Usuario::getNome).orElse(usuarioLogado),
                usuarioOpt.map(Usuario::getEmail).orElse(usuarioLogado)
        );

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> e : itensValidos.entrySet()) {
            Produto p = produtoRepository.findById(e.getKey())
                    .filter(prod -> Optional.ofNullable(prod.getEstoque()).orElse(0) >= e.getValue())
                    .orElseThrow(() -> new IllegalStateException("Produto indisponível ou estoque insuficiente."));
            ItemCompra item = pagamentoMapper.toItemCompra(p, e.getValue());
            compra.adicionarItem(item);
            total = total.add(item.getPrecoUnitario().multiply(BigDecimal.valueOf(e.getValue())));
        }
        compra.setValorTotal(total);
        return compraRepository.save(compra);
    }

    public List<Compra> listarDoCliente(String usuarioLogado) {
        return buscarUsuario(usuarioLogado)
                .map(Usuario::getEmail)
                .or(() -> Optional.ofNullable(usuarioLogado))
                .map(compraRepository::findByClienteEmailOrderByDataCompraDesc)
                .orElseGet(List::of);
    }

    private Optional<Usuario> buscarUsuario(String identificador) {
        return Optional.ofNullable(identificador)
                .flatMap(id -> usuarioRepository.findByNome(id)
                        .or(() -> usuarioRepository.findByEmail(id)));
    }
}
