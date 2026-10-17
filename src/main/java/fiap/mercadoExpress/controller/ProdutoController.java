package fiap.mercadoExpress.controller;

import fiap.mercadoExpress.dto.ProdutoDTO;
import fiap.mercadoExpress.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private static final String REDIRECT_PRODUTOS = "redirect:/produtos";
    private static final String ATTR_PRODUTO = "produto";
    private static final String ATTR_MENSAGEM = "mensagem";

    private final ProdutoService service;

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("produtos", service.listarDto(busca));
        model.addAttribute("busca", busca);
        return "produto/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute(ATTR_PRODUTO, new ProdutoDTO());
        return "produto/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute(ATTR_PRODUTO) ProdutoDTO produtoDto, RedirectAttributes ra) {
        service.salvarDto(produtoDto);
        ra.addFlashAttribute(ATTR_MENSAGEM, "Produto salvo com sucesso!");
        return REDIRECT_PRODUTOS;
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute(ATTR_PRODUTO, service.buscarDtoPorIdOuFalhar(id));
        return "produto/form";
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         @ModelAttribute(ATTR_PRODUTO) ProdutoDTO produtoDto,
                         RedirectAttributes ra) {
        produtoDto.setId(id);
        service.salvarDto(produtoDto);
        ra.addFlashAttribute(ATTR_MENSAGEM, "Produto atualizado com sucesso!");
        return REDIRECT_PRODUTOS;
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes ra) {
        service.deletar(id);
        ra.addFlashAttribute(ATTR_MENSAGEM, "Produto excluído.");
        return REDIRECT_PRODUTOS;
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute(ATTR_PRODUTO, service.buscarDtoPorIdOuFalhar(id));
        return "produto/detalhe";
    }
}
