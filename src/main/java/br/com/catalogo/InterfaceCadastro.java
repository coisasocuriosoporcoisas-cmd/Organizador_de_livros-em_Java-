package br.com.catalogo;

import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InterfaceCadastro extends Application {
    private final ObservableList<Livro> livros = FXCollections.observableArrayList();
    private CatalogoLivros catalogo;
    private TextField campoId;
    private TextField campoTitulo;
    private TextField campoSessaoNome;
    private TextField campoSessaoNumero;
    private TextField campoGeneros;
    private TextField campoTags;
    private CheckBox caixaDisponivel;
    private TableView<Livro> tabela;
    private Label contador;

    @Override
    public void start(Stage palco) {
        try {
            catalogo = new CatalogoLivros();
            livros.setAll(catalogo.listar());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao carregar catálogo",
                "Não foi possível abrir livros.json:\n" + e.getMessage());
            return;
        }

        BorderPane raiz = new BorderPane();
        raiz.getStyleClass().add("app");
        raiz.setTop(criarCabecalho());
        raiz.setCenter(criarConteudo());

        Scene cena = new Scene(raiz, 1220, 790);
        cena.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        palco.setTitle("Estante | Catálogo de livros");
        palco.setMinWidth(1000);
        palco.setMinHeight(700);
        palco.setScene(cena);
        palco.show();
    }

    private VBox criarCabecalho() {
        Label marca = new Label("ESTANTE");
        marca.getStyleClass().add("brand");
        Label titulo = new Label("Catálogo de livros");
        titulo.getStyleClass().add("page-title");
        Label descricao = new Label("Organize seu acervo por localização, gênero e tags.");
        descricao.getStyleClass().add("page-subtitle");

        VBox textos = new VBox(7, titulo, descricao);
        VBox cabecalho = new VBox(18, marca, textos);
        cabecalho.getStyleClass().add("header");
        return cabecalho;
    }

    private HBox criarConteudo() {
        VBox formulario = criarFormulario();
        VBox secaoCatalogo = criarTabela();
        HBox conteudo = new HBox(24, formulario, secaoCatalogo);
        conteudo.setAlignment(Pos.TOP_LEFT);
        conteudo.setPadding(new Insets(26, 34, 34, 34));
        HBox.setHgrow(secaoCatalogo, Priority.ALWAYS);
        formulario.setPrefWidth(350);
        formulario.setMinWidth(320);
        secaoCatalogo.setMaxWidth(Double.MAX_VALUE);
        return conteudo;
    }

    private VBox criarFormulario() {
        Label titulo = new Label("Novo livro");
        titulo.getStyleClass().add("section-title");
        Label descricao = new Label("Preencha os dados para adicionar ao acervo.");
        descricao.getStyleClass().add("muted");

        campoId = criarCampo("Ex.: 101");
        campoTitulo = criarCampo("Título do livro");
        campoSessaoNome = criarCampo("Ex.: Literatura brasileira");
        campoSessaoNumero = criarCampo("Ex.: 3");
        campoGeneros = criarCampo("Ex.: Romance, Ficção");
        campoTags = criarCampo("Ex.: clássico, emprestado");

        GridPane campos = new GridPane();
        campos.setHgap(12);
        campos.setVgap(13);
        adicionarCampo(campos, "ID do livro", campoId, 0);
        adicionarCampo(campos, "Título", campoTitulo, 1);
        adicionarCampo(campos, "Nome da sessão", campoSessaoNome, 2);
        adicionarCampo(campos, "Número da sessão", campoSessaoNumero, 3);
        adicionarCampo(campos, "Gêneros", campoGeneros, 4);
        adicionarCampo(campos, "Tags", campoTags, 5);

        Label dica = new Label("Separe gêneros e tags por vírgula.");
        dica.getStyleClass().add("field-hint");
        campos.add(dica, 0, 6);

        caixaDisponivel = new CheckBox("Livro disponível");
        caixaDisponivel.setSelected(true);
        caixaDisponivel.getStyleClass().add("availability-check");

        Button botaoCadastrar = new Button("Adicionar livro");
        botaoCadastrar.getStyleClass().add("primary-button");
        botaoCadastrar.setMaxWidth(Double.MAX_VALUE);
        botaoCadastrar.setOnAction(evento -> cadastrarLivro());

        Button botaoLimpar = new Button("Limpar campos");
        botaoLimpar.getStyleClass().add("secondary-button");
        botaoLimpar.setMaxWidth(Double.MAX_VALUE);
        botaoLimpar.setOnAction(evento -> limparFormulario());

        VBox cartao = new VBox(17, titulo, descricao, campos, caixaDisponivel,
            botaoCadastrar, botaoLimpar);
        cartao.getStyleClass().add("card");
        cartao.setPrefWidth(350);
        return cartao;
    }

    private VBox criarTabela() {
        Label titulo = new Label("Acervo");
        titulo.getStyleClass().add("section-title");
        contador = new Label();
        contador.getStyleClass().add("count-badge");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        HBox cabecalho = new HBox(12, titulo, espaco, contador);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        Label descricao = new Label("Seus livros cadastrados e suas localizações.");
        descricao.getStyleClass().add("muted");

        tabela = new TableView<>(livros);
        tabela.getStyleClass().add("book-table");
        tabela.setPlaceholder(new Label("Nenhum livro cadastrado. Adicione o primeiro pelo formulário."));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setMinHeight(450);

        tabela.getColumns().add(coluna("ID", livro -> Integer.toString(livro.getId()), 58));
        tabela.getColumns().add(coluna("Título", Livro::getTitulo, 150));
        tabela.getColumns().add(coluna("Sessão", Livro::getSessaoTexto, 145));
        tabela.getColumns().add(coluna("Gêneros", Livro::getGenerosTexto, 130));
        tabela.getColumns().add(coluna("Tags", Livro::getTagsTexto, 130));
        tabela.getColumns().add(coluna("Status",
            livro -> livro.isDisponivel() ? "Disponível" : "Indisponível", 100));
        atualizarContador();
        VBox.setVgrow(tabela, Priority.ALWAYS);

        VBox painel = new VBox(14, cabecalho, descricao, tabela);
        painel.getStyleClass().add("card");
        HBox.setHgrow(painel, Priority.ALWAYS);
        return painel;
    }

    private TableColumn<Livro, String> coluna(String nome,
            java.util.function.Function<Livro, String> valor, double largura) {
        TableColumn<Livro, String> coluna = new TableColumn<>(nome);
        coluna.setCellValueFactory(celula ->
            new ReadOnlyStringWrapper(valor.apply(celula.getValue())));
        coluna.setPrefWidth(largura);
        return coluna;
    }

    private TextField criarCampo(String dica) {
        TextField campo = new TextField();
        campo.setPromptText(dica);
        campo.getStyleClass().add("text-field");
        return campo;
    }

    private void adicionarCampo(GridPane grade, String rotulo, TextField campo, int linha) {
        Label label = new Label(rotulo);
        label.getStyleClass().add("field-label");
        grade.add(label, 0, linha * 2);
        grade.add(campo, 0, linha * 2 + 1);
        GridPane.setHgrow(campo, Priority.ALWAYS);
        campo.setMaxWidth(Double.MAX_VALUE);
    }

    private void cadastrarLivro() {
        final int id;
        final int numeroSessao;
        try {
            id = Integer.parseInt(campoId.getText().trim());
            numeroSessao = Integer.parseInt(campoSessaoNumero.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Verifique os números",
                "O ID e o número da sessão devem ser números inteiros.");
            return;
        }

        String titulo = campoTitulo.getText().trim();
        String sessaoNome = campoSessaoNome.getText().trim();
        List<String> generos = CatalogoLivros.separarLista(campoGeneros.getText());
        List<String> tags = CatalogoLivros.separarLista(campoTags.getText());

        if (id <= 0 || numeroSessao <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Número inválido",
                "O ID e o número da sessão devem ser maiores que zero.");
            return;
        }
        if (titulo.isEmpty() || sessaoNome.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos obrigatórios",
                "Informe o título do livro e o nome da sessão.");
            return;
        }
        if (generos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Gênero obrigatório",
                "Informe ao menos um gênero para o livro.");
            return;
        }

        Livro livro = new Livro(id, titulo, sessaoNome, numeroSessao,
            generos, tags, caixaDisponivel.isSelected());
        try {
            catalogo.cadastrar(livro);
            livros.setAll(catalogo.listar());
            atualizarContador();
            limparFormulario();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Livro não cadastrado", e.getMessage());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao salvar o livro",
                "Não foi possível gravar livros.json:\n" + e.getMessage());
        }
    }

    private void atualizarContador() {
        int total = livros.size();
        contador.setText(total + (total == 1 ? " livro" : " livros"));
    }

    private void limparFormulario() {
        campoId.clear();
        campoTitulo.clear();
        campoSessaoNome.clear();
        campoSessaoNumero.clear();
        campoGeneros.clear();
        campoTags.clear();
        caixaDisponivel.setSelected(true);
        campoId.requestFocus();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
