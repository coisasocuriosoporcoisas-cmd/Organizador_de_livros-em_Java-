package br.com.catalogo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Livro {
    private int id;
    private String titulo;
    private String sessaoNome;
    private int sessaoNumero;
    private List<String> generos;
    private List<String> tags;
    private boolean disponivel;

    public Livro() {
        this.generos = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public Livro(int id, String titulo, String sessaoNome, int sessaoNumero,
                 List<String> generos, List<String> tags, boolean disponivel) {
        this.id = id;
        this.titulo = titulo;
        this.sessaoNome = sessaoNome;
        this.sessaoNumero = sessaoNumero;
        this.generos = new ArrayList<>(generos);
        this.tags = new ArrayList<>(tags);
        this.disponivel = disponivel;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSessaoNome() {
        return sessaoNome;
    }

    public int getSessaoNumero() {
        return sessaoNumero;
    }

    public List<String> getGeneros() {
        return Collections.unmodifiableList(generos);
    }

    public void setGeneros(List<String> generos) {
        this.generos = new ArrayList<>(generos);
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getGenerosTexto() {
        return String.join(", ", generos);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getTagsTexto() {
        return String.join(", ", tags);
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getSessaoTexto() {
        return sessaoNome + " · " + sessaoNumero;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setSessaoNome(String sessaoNome) {
        this.sessaoNome = sessaoNome;
    }

    public void setSessaoNumero(int sessaoNumero) {
        this.sessaoNumero = sessaoNumero;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}
