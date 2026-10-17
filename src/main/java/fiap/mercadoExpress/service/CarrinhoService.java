package fiap.mercadoExpress.service;

import fiap.mercadoExpress.model.Produto;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.LinkedHashMap;
import java.util.Map;

/** Carrinho de compras mantido na sessão HTTP do usuário (produtoId -> quantidade). */
@Component
@SessionScope
public class CarrinhoService {

    @Getter
    private final Map<Long, Integer> itens = new LinkedHashMap<>();

    public void adicionar(Produto produto) {
        int atual = itens.getOrDefault(produto.getId(), 0);
        int estoque = produto.getEstoque() == null ? 0 : produto.getEstoque();
        if (atual + 1 > estoque) {
            throw new IllegalStateException("Estoque insuficiente para " + produto.getNome());
        }
        itens.put(produto.getId(), atual + 1);
    }

    public void remover(Long produtoId) {
        itens.remove(produtoId);
    }

    public void limpar() {
        itens.clear();
    }

    public boolean isVazio() {
        return itens.isEmpty();
    }
}
