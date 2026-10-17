package fiap.mercadoExpress.controller;

import fiap.mercadoExpress.mapper.PagamentoMapper;
import fiap.mercadoExpress.model.Compra;
import fiap.mercadoExpress.model.Produto;
import fiap.mercadoExpress.service.CarrinhoService;
import fiap.mercadoExpress.service.CompraService;
import fiap.mercadoExpress.service.PagamentoService;
import fiap.mercadoExpress.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CarrinhoController {

    private final CarrinhoService carrinho;
    private final ProdutoService produtoService;
    private final CompraService compraService;
    private final PagamentoService pagamentoService;
    private final PagamentoMapper pagamentoMapper;

    public record LinhaCarrinho(Produto produto, int quantidade, double subtotal) {}

    @GetMapping("/carrinho")
    public String ver(Model model) {
        List<LinhaCarrinho> linhas = montarLinhasCarrinho();
        double total = linhas.stream().mapToDouble(LinhaCarrinho::subtotal).sum();
        model.addAttribute("linhas", linhas);
        model.addAttribute("total", total);
        return "carrinho/carrinho";
    }

    @PostMapping("/carrinho/adicionar/{id}")
    public String adicionar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Produto produto = produtoService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalStateException("Produto não encontrado."));
            carrinho.adicionar(produto);
            ra.addFlashAttribute("mensagem", produto.getNome() + " adicionado ao carrinho.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/produtos";
    }

    @PostMapping("/carrinho/remover/{id}")
    public String remover(@PathVariable Long id) {
        carrinho.remover(id);
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/finalizar")
    public String finalizar(Authentication auth, RedirectAttributes ra) {
        try {
            Compra compra = compraService.criarPedido(carrinho.getItens(), auth.getName());
            carrinho.limpar();
            pagamentoService.solicitarPagamento(pagamentoMapper.toPagamentoDto(compra));
            ra.addFlashAttribute("mensagem",
                    "Pedido " + compra.getNumeroPedido() + " enviado ao Banco Tranquilo. Aguardando confirmação...");
            return "redirect:/compras";
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/carrinho";
        }
    }

    @GetMapping("/compras")
    public String minhasCompras(Authentication auth, Model model) {
        model.addAttribute("compras", compraService.listarDoCliente(auth.getName()));
        return "carrinho/compras";
    }

    private List<LinhaCarrinho> montarLinhasCarrinho() {
        List<LinhaCarrinho> linhas = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : carrinho.getItens().entrySet()) {
            produtoService.buscarPorId(entry.getKey()).ifPresent(produto -> {
                double subtotal = produto.getPreco() * entry.getValue();
                linhas.add(new LinhaCarrinho(produto, entry.getValue(), subtotal));
            });
        }
        return linhas;
    }
}
