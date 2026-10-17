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
        if (busca == null || busca.isBlank()) {
            return produtoMapper.toDtoList(repository.findAll());
        }
        String termo = busca.trim();
        return produtoMapper.toDtoList(
                repository.findByNomeContainingIgnoreCaseOrTipoContainingIgnoreCaseOrSetorContainingIgnoreCase(
                        termo, termo, termo));
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public ProdutoDTO buscarDtoPorIdOuFalhar(Long id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        return produtoMapper.toDto(produto);
    }

    public ProdutoDTO salvarDto(ProdutoDTO dto) {
        Produto salvo = repository.save(produtoMapper.toEntity(dto));
        return produtoMapper.toDto(salvo);
    }

    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
