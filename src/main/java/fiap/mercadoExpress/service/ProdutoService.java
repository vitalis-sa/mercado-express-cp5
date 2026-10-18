package fiap.mercadoExpress.service;

import fiap.mercadoExpress.dto.ProdutoDTO;
import fiap.mercadoExpress.mapper.ProdutoMapper;
import fiap.mercadoExpress.model.Produto;
import fiap.mercadoExpress.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final ProdutoMapper produtoMapper;

    public List<ProdutoDTO> listarDto(String busca) {
        List<Produto> produtos = Optional.ofNullable(busca)
                .map(String::trim)
                .filter(termo -> !termo.isEmpty())
                .map(termo -> repository.findByNomeContainingIgnoreCaseOrTipoContainingIgnoreCaseOrSetorContainingIgnoreCase(
                        termo, termo, termo))
                .orElseGet(repository::findAll);
        return produtoMapper.toDtoList(produtos);
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public ProdutoDTO buscarDtoPorIdOuFalhar(Long id) {
        return repository.findById(id)
                .map(produtoMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    public ProdutoDTO salvarDto(ProdutoDTO dto) {
        return Optional.ofNullable(dto)
                .map(produtoMapper::toEntity)
                .map(repository::save)
                .map(produtoMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Dados do produto inválidos."));
    }

    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
