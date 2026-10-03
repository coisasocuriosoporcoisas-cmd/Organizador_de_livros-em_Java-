package br.com.catalogo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatalogoLivros {
    private final Path arquivo;
    private final ObjectMapper mapper;
    private final List<Livro> livros;

    public CatalogoLivros() throws IOException {
        this(Paths.get("livros.json"));
    }

    CatalogoLivros(Path arquivo) throws IOException {
        this.arquivo = arquivo;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        if (Files.exists(arquivo)) {
            this.livros = mapper.readValue(arquivo.toFile(), new TypeReference<List<Livro>>() { });
        } else {
            this.livros = new ArrayList<>();
        }
    }

    public List<Livro> listar() {
        return Collections.unmodifiableList(new ArrayList<>(livros));
    }

    public void cadastrar(Livro livro) throws IOException {
        for (Livro existente : livros) {
            if (existente.getId() == livro.getId()) {
                throw new IllegalArgumentException("Já existe um livro cadastrado com esse ID.");
            }
        }

        livros.add(livro);
        try {
            salvar();
        } catch (IOException e) {
            livros.remove(livro);
            throw e;
        }
    }

    private void salvar() throws IOException {
        Path pasta = arquivo.toAbsolutePath().getParent();
        if (pasta != null) {
            Files.createDirectories(pasta);
        }
        mapper.writeValue(arquivo.toFile(), livros);
    }

    public static List<String> separarLista(String texto) {
        Set<String> valores = new HashSet<>();
        List<String> resultado = new ArrayList<>();
        for (String item : texto.split(",")) {
            String valor = item.trim();
            if (!valor.isEmpty() && valores.add(valor.toLowerCase(java.util.Locale.ROOT))) {
                resultado.add(valor);
            }
        }
        return resultado;
    }
}
