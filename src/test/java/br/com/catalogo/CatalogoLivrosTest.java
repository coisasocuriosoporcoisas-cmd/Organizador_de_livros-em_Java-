package br.com.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CatalogoLivrosTest {
    @TempDir
    Path pastaTemporaria;

    @Test
    void cadastraLivroERecuperaDadosDoJson() throws Exception {
        Path arquivo = pastaTemporaria.resolve("livros.json");
        CatalogoLivros catalogo = new CatalogoLivros(arquivo);
        Livro livro = new Livro(12, "O nome do vento", "Fantasia", 4,
            Arrays.asList("Fantasia", "Aventura"),
            Arrays.asList("magia", "saga"), true);

        catalogo.cadastrar(livro);

        Livro recuperado = new CatalogoLivros(arquivo).listar().get(0);
        assertEquals(12, recuperado.getId());
        assertEquals("O nome do vento", recuperado.getTitulo());
        assertEquals("Fantasia", recuperado.getSessaoNome());
        assertEquals(4, recuperado.getSessaoNumero());
        assertEquals(Arrays.asList("Fantasia", "Aventura"), recuperado.getGeneros());
        assertEquals(Arrays.asList("magia", "saga"), recuperado.getTags());
        assertEquals(true, recuperado.isDisponivel());
    }

    @Test
    void rejeitaIdDuplicadoSemAdicionarLivroAoCatalogo() throws Exception {
        CatalogoLivros catalogo = new CatalogoLivros(pastaTemporaria.resolve("livros.json"));
        catalogo.cadastrar(new Livro(5, "Primeiro", "Ficção", 1,
            Arrays.asList("Ficção"), Arrays.asList("aventura"), true));

        assertThrows(IllegalArgumentException.class, () ->
            catalogo.cadastrar(new Livro(5, "Duplicado", "Ficção", 1,
                Arrays.asList("Ficção"), Arrays.asList("aventura"), true)));
        assertEquals(1, catalogo.listar().size());
    }

    @Test
    void divideTagsPorVirgulaRemoveVaziosEDuplicatas() {
        assertEquals(Arrays.asList("clássico", "ficção"),
            CatalogoLivros.separarLista(" clássico, , ficção, CLÁSSICO "));
    }
}
