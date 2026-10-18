package fiap.mercadoExpress.service;

import fiap.mercadoExpress.model.Produto;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
@SessionScope
public class CarrinhoService {

    @Getter
    private final Map<Long, Integer> itens = new LinkedHashMap<>();

    public void adicionar(Produto produto) {
        int proximo = itens.getOrDefault(produto.getId(), 0) + 1;
        Optional.ofNullable(produto.getEstoque())
                .filter(estoque -> estoque >= proximo)
                .orElseThrow(() -> new IllegalStateException("Estoque insuficiente para " + produto.getNome()));
        itens.put(produto.getId(), proximo);
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
