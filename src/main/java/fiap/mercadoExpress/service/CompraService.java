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
        if (itensCarrinho.isEmpty()) {
            throw new IllegalStateException("Carrinho vazio.");
        }
        String numeroPedido = "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Optional<Usuario> usuarioOpt = buscarUsuario(usuarioLogado);
        Compra compra = pagamentoMapper.toNovaCompra(
                numeroPedido,
                usuarioOpt.map(Usuario::getNome).orElse(usuarioLogado),
                usuarioOpt.map(Usuario::getEmail).orElse(usuarioLogado)
        );

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> e : itensCarrinho.entrySet()) {
            Produto p = produtoRepository.findById(e.getKey())
                    .orElseThrow(() -> new IllegalStateException("Produto não encontrado: " + e.getKey()));
            int estoque = p.getEstoque() == null ? 0 : p.getEstoque();
            if (e.getValue() > estoque) {
                throw new IllegalStateException("Estoque insuficiente para " + p.getNome());
            }
            ItemCompra item = pagamentoMapper.toItemCompra(p, e.getValue());
            compra.adicionarItem(item);
            total = total.add(item.getPrecoUnitario().multiply(BigDecimal.valueOf(e.getValue())));
        }
        compra.setValorTotal(total);
        return compraRepository.save(compra);
    }

    public List<Compra> listarDoCliente(String usuarioLogado) {
        String email = buscarUsuario(usuarioLogado).map(Usuario::getEmail).orElse(usuarioLogado);
        return compraRepository.findByClienteEmailOrderByDataCompraDesc(email);
    }

    private Optional<Usuario> buscarUsuario(String identificador) {
        return usuarioRepository.findByNome(identificador)
                .or(() -> usuarioRepository.findByEmail(identificador));
    }
}
