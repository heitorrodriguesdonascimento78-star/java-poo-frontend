package com.br.util;

import com.br.enums.StatusBomba;
import com.br.enums.StatusCaixa;
import com.br.enums.TipoMovimento;
import com.br.model.Abastecimento;
import com.br.model.Bomba;
import com.br.model.Caixa;
import com.br.model.Cliente;
import com.br.model.MovimentoContaCliente;
import com.br.model.Produto;
import com.br.model.Tanque;
import com.br.model.Venda;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpClient {

    // --- Singleton ---
    private static HttpClient instance;
    public static synchronized HttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }
        return instance;
    }

    private static final String BASE_URL = "http://localhost:8080";
    private final java.net.http.HttpClient client;
    private final ObjectMapper objectMapper;

    private HttpClient() {
        this.client = java.net.http.HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public boolean autenticar(String usuario, String senha) throws IOException, InterruptedException {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", usuario);
        loginRequest.put("senha", senha);
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/login")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Login bem-sucedido: " + response.body());
            return true;
        }

        System.err.println("Erro de autenticação: " + response.statusCode() + " - " + response.body());
        return false;
    }

    public boolean registrarNovoUsuario(String nome, String email, String senha, String cpfCnpj, String dataNascimento, String tipoPessoa) throws IOException, InterruptedException {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("nomeCompleto", nome);
        requestBody.put("email", email);
        requestBody.put("senha", senha);
        requestBody.put("cpfCnpj", cpfCnpj);
        requestBody.put("dataNascimento", dataNascimento);
        requestBody.put("tipoPessoa", tipoPessoa);
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/funcionarios/registrar"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 201;
    }

    // --- Métodos de Produto ---
    public List<Produto> buscarProdutos() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/produtos")).header("Accept", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode contentNode = rootNode.get("content");
            if (contentNode != null) {
                return objectMapper.readValue(contentNode.traverse(), new TypeReference<List<Produto>>() {});
            } else {
                throw new IOException("Resposta do backend não contém o campo 'content'.");
            }
        } else {
            // CORRIGIDO: Adicionada aspa dupla final
            throw new IOException("Falha ao buscar produtos: " + response.statusCode() + " - " + response.body());
        }
    }

    public boolean criarProduto(Produto produto) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(produto);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/produtos")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 201;
    }

    public boolean atualizarProduto(Produto produto) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(produto);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/produtos/" + produto.getId())).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 200;
    }

    public boolean excluirProduto(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/produtos/" + id)).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 204;
    }

    // --- Métodos de Cliente ---
    public List<Cliente> buscarClientes() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/clientes")).header("Accept", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode contentNode = rootNode.get("content");
            if (contentNode != null) {
                return objectMapper.readValue(contentNode.traverse(), new TypeReference<List<Cliente>>() {});
            } else {
                return objectMapper.readValue(response.body(), new TypeReference<List<Cliente>>() {});
            }
        } else {
            throw new IOException("Falha ao buscar clientes: " + response.statusCode() + " - " + response.body());
        }
    }

    public boolean criarCliente(String nomeCompleto, String cpfCnpj, String tipoPessoa, String email, String telefone, LocalDate dataNascimento, BigDecimal limiteCredito) throws IOException, InterruptedException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("nomeCompleto", nomeCompleto);
        requestBody.put("cpfCnpj", cpfCnpj);
        requestBody.put("tipoPessoa", tipoPessoa);
        requestBody.put("email", email);
        requestBody.put("telefone", telefone);
        requestBody.put("dataNascimento", dataNascimento.toString());
        requestBody.put("limiteCredito", limiteCredito);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/clientes")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            System.err.println("Falha ao criar cliente: " + response.statusCode() + " - " + response.body());
        }
        return response.statusCode() == 201;
    }

    public boolean atualizarCliente(Long id, String nomeCompleto, String cpfCnpj, String tipoPessoa, String email, String telefone, LocalDate dataNascimento, BigDecimal limiteCredito) throws IOException, InterruptedException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("nomeCompleto", nomeCompleto);
        requestBody.put("cpfCnpj", cpfCnpj);
        requestBody.put("tipoPessoa", tipoPessoa);
        requestBody.put("email", email);
        requestBody.put("telefone", telefone);
        requestBody.put("dataNascimento", dataNascimento.toString());
        requestBody.put("limiteCredito", limiteCredito);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/clientes/" + id)).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Falha ao atualizar cliente: " + response.statusCode() + " - " + response.body());
        }
        return response.statusCode() == 200;
    }

    public boolean excluirCliente(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/clientes/" + id)).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 204;
    }

    // --- Métodos de Abastecimento ---
    public boolean criarAbastecimento(Long bombaId, Double litrosAbastecidos) throws IOException, InterruptedException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("bombaId", bombaId);
        requestBody.put("litrosAbastecidos", litrosAbastecidos);
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/abastecimentos")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            System.err.println("Falha ao criar abastecimento: " + response.statusCode() + " - " + response.body());
        }
        return response.statusCode() == 201;
    }

    // --- Métodos de Bomba ---
    public List<Bomba> buscarBombas() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/bombas")).header("Accept", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode contentNode = rootNode.get("content");
            if (contentNode != null) {
                return objectMapper.readValue(contentNode.traverse(), new TypeReference<List<Bomba>>() {});
            } else {
                return objectMapper.readValue(response.body(), new TypeReference<List<Bomba>>() {});
            }
        } else {
            throw new IOException("Falha ao buscar bombas: " + response.statusCode() + " - " + response.body());
        }
    }

    public boolean atualizarStatusBomba(Long id, StatusBomba status) throws IOException, InterruptedException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("status", status.name()); // Envia o nome do enum como String
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/bombas/" + id + "/status"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Falha ao atualizar status da bomba: " + response.statusCode() + " - " + response.body());
        }
        return response.statusCode() == 200;
    }

    public boolean selecionarBico(Integer numeroBombaFisica, Integer numeroBicoSelecionado) throws IOException, InterruptedException {
        List<Bomba> bombas = buscarBombas();
        boolean sucesso = true;

        for (Bomba bomba : bombas) {
            if (bomba.getNumeroBombaFisica().equals(numeroBombaFisica)) {
                if (bomba.getNumeroBico().equals(numeroBicoSelecionado)) {
                    // Ativa o bico selecionado (ou coloca em ABASTECENDO)
                    if (!atualizarStatusBomba(bomba.getId(), StatusBomba.ABASTECENDO)) {
                        sucesso = false;
                    }
                } else {
                    // Inativa os outros bicos da mesma bomba
                    if (!atualizarStatusBomba(bomba.getId(), StatusBomba.INATIVA)) {
                        sucesso = false;
                    }
                }
            }
        }
        return sucesso;
    }

    // --- Métodos de Venda ---
    public Venda criarVenda(Venda venda) throws IOException, InterruptedException {
        Map<String, Object> vendaRequest = new HashMap<>();
        vendaRequest.put("funcionarioId", venda.getFuncionarioId());
        vendaRequest.put("clienteId", venda.getClienteId());
        vendaRequest.put("formaPagamento", venda.getFormaPagamento());

        List<Map<String, Object>> itensRequest = venda.getItens().stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("produtoId", item.getProdutoId());
                    itemMap.put("quantidade", item.getQuantidade());
                    // Adicionar precoUnitario e subtotal se o backend precisar para criar a venda
                    itemMap.put("precoUnitario", item.getPrecoUnitario());
                    itemMap.put("subtotal", item.getSubtotal());
                    return itemMap;
                })
                .collect(Collectors.toList());

        vendaRequest.put("itens", itensRequest);

        String jsonBody = objectMapper.writeValueAsString(vendaRequest);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/vendas")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) { // 201 Created
            return objectMapper.readValue(response.body(), Venda.class);
        } else {
            System.err.println("Falha ao criar venda: " + response.statusCode() + " - " + response.body());
            throw new IOException("Falha ao criar venda: " + response.statusCode() + " - " + response.body());
        }
    }

    public List<Venda> buscarVendas() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/vendas"))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            if (rootNode.has("content")) { // Se for paginado
                JsonNode contentNode = rootNode.get("content");
                return objectMapper.readValue(contentNode.traverse(), new TypeReference<List<Venda>>() {});
            } else { // Se for uma lista direta
                return objectMapper.readValue(response.body(), new TypeReference<List<Venda>>() {});
            }
        } else {
            throw new IOException("Falha ao buscar vendas: " + response.statusCode() + " - " + response.body());
        }
    }

    public List<Venda> buscarVendasPorFuncionario(Long funcionarioId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/vendas/funcionario/" + funcionarioId))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // O backend pode retornar uma lista diretamente ou um objeto paginado com "content"
            JsonNode rootNode = objectMapper.readTree(response.body());
            if (rootNode.has("content")) { // Se for paginado
                JsonNode contentNode = rootNode.get("content");
                return objectMapper.readValue(contentNode.traverse(), new TypeReference<List<Venda>>() {});
            } else { // Se for uma lista direta
                return objectMapper.readValue(response.body(), new TypeReference<List<Venda>>() {});
            }
        } else {
            throw new IOException("Falha ao buscar vendas por funcionário: " + response.statusCode() + " - " + response.body());
        }
    }

    public String emitirCupomFiscal(Long vendaId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/vendas/" + vendaId + "/emitir-cupom"))
                .header("Accept", "text/plain")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Falha ao emitir cupom fiscal: " + response.statusCode() + " - " + response.body());
        }
    }

    public String reemitirCupomFiscal(Long vendaId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/vendas/" + vendaId + "/reemitir-cupom"))
                .header("Accept", "text/plain") // Espera um texto simples como resposta
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Falha ao reemitir cupom fiscal: " + response.statusCode() + " - " + response.body());
        }
    }

    // --- Métodos de Conta Cliente ---
    public boolean pagarConta(Long clienteId, BigDecimal valor) throws IOException, InterruptedException {
        Map<String, Object> pagamentoRequest = new HashMap<>();
        pagamentoRequest.put("valor", valor);
        String jsonBody = objectMapper.writeValueAsString(pagamentoRequest);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/clientes/" + clienteId + "/pagar")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Falha ao registrar pagamento: " + response.statusCode() + " - " + response.body());
        }
        return response.statusCode() == 200;
    }

    public List<MovimentoContaCliente> getHistoricoConta(Long clienteId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/clientes/" + clienteId + "/historico")).header("Accept", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<MovimentoContaCliente>>() {});
        } else {
            throw new IOException("Falha ao buscar histórico da conta: " + response.statusCode() + " - " + response.body());
        }
    }

    // --- Métodos de Tanque ---
    public List<Tanque> buscarTanques() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/tanques")).header("Accept", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode contentNode = rootNode.get("content");
            if (contentNode != null) {
                return objectMapper.readValue(contentNode.traverse(), new TypeReference<List<Tanque>>() {});
            } else {
                return objectMapper.readValue(response.body(), new TypeReference<List<Tanque>>() {});
            }
        } else {
            throw new IOException("Falha ao buscar tanques: " + response.statusCode() + " - " + response.body());
        }
    }

    public boolean criarTanque(Double capacidade, Double nivelAtual, Long combustivelId) throws IOException, InterruptedException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("capacidade", capacidade);
        requestBody.put("nivelAtual", nivelAtual);
        requestBody.put("combustivelId", combustivelId);
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/tanques")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            System.err.println("Falha ao criar tanque: " + response.statusCode() + " - " + response.body());
        }
        return response.statusCode() == 201;
    }

    public boolean atualizarTanque(Long id, Double capacidade, Double nivelAtual, Long combustivelId) throws IOException, InterruptedException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("capacidade", capacidade);
        requestBody.put("nivelAtual", nivelAtual);
        requestBody.put("combustivelId", combustivelId);
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/tanques/" + id)).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.err.println("Falha ao atualizar tanque: " + response.statusCode() + " - " + response.body());
        }
        return response.statusCode() == 200;
    }

    public boolean excluirTanque(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/api/tanques/" + id)).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode() == 204;
    }

    // --- Métodos para o Domínio de Caixa ---

    public Caixa abrirCaixa(com.br.dto.CaixaAberturaRequest requestBody) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/caixas/abrir"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) { // 201 Created
            return objectMapper.readValue(response.body(), Caixa.class);
        } else {
            System.err.println("Falha ao abrir caixa. Status: " + response.statusCode() + " | Resposta: " + response.body());
            throw new IOException("Falha ao abrir caixa: " + response.body());
        }
    }

    public Caixa fecharCaixa(Long caixaId, com.br.dto.CaixaFechamentoRequest requestBody) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/caixas/" + caixaId + "/fechar"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) { // 200 OK
            return objectMapper.readValue(response.body(), Caixa.class);
        } else {
            System.err.println("Falha ao fechar caixa. Status: " + response.statusCode() + " | Resposta: " + response.body());
            throw new IOException("Falha ao fechar caixa: " + response.body());
        }
    }

    public Caixa buscarCaixaAberto() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/caixas/aberto"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) { // 200 OK
            return objectMapper.readValue(response.body(), Caixa.class);
        } else if (response.statusCode() == 500 && response.body().contains("Nenhum caixa aberto encontrado")) {
            // Se não houver caixa aberto, o backend retorna 500 com essa mensagem.
            // Podemos tratar como um caso de "nenhum caixa" em vez de um erro fatal.
            return null;
        }
        else {
            System.err.println("Falha ao buscar caixa aberto. Status: " + response.statusCode() + " | Resposta: " + response.body());
            throw new IOException("Falha ao buscar caixa aberto: " + response.body());
        }
    }
}