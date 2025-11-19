package com.br.pdvfrontend.view;

import com.br.pdvfrontend.enums.FormaPagamento;
import com.br.pdvfrontend.enums.StatusBomba;
import com.br.pdvfrontend.model.Bomba;
import com.br.pdvfrontend.model.Cliente;
import com.br.pdvfrontend.model.ItemVenda;
import com.br.pdvfrontend.model.Produto;
import com.br.pdvfrontend.model.Venda;
import com.br.pdvfrontend.util.HttpClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TelaAbastecimento extends JPanel {

    private JPanel bombasContainerPanel; // Painel que conterá os grupos de bombas físicas
    private JButton btnAtualizar;

    private JComboBox<Cliente> cmbClientes;
    private JComboBox<String> cmbFormaPagamento;

    private List<Cliente> clientesDisponiveis = new ArrayList<>();
    private List<Produto> produtosDisponiveis = new ArrayList<>();

    // Map para armazenar os painéis de bico por bomba física
    private Map<Integer, List<BombaPanel>> bicosPorBombaFisica = new HashMap<>();
    private BombaPanel bicoSelecionadoPanel = null; // O painel do bico atualmente selecionado

    public TelaAbastecimento() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- PAINEL SUPERIOR: Cliente, Pagamento e Atualizar ---
        JPanel topControlsPanel = new JPanel(new BorderLayout());

        JPanel clientPaymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientPaymentPanel.add(new JLabel("Cliente:"));
        cmbClientes = new JComboBox<>();
        clientPaymentPanel.add(cmbClientes);
        clientPaymentPanel.add(new JLabel("Forma de Pagamento:"));
        cmbFormaPagamento = new JComboBox<>(new String[]{"DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO", "PIX", "CONTA_CLIENTE"}); // Alterado de CREDITO_CLIENTE para CONTA_CLIENTE
        clientPaymentPanel.add(cmbFormaPagamento);
        topControlsPanel.add(clientPaymentPanel, BorderLayout.WEST);

        JPanel updateButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAtualizar = new JButton("Atualizar Status");
        updateButtonPanel.add(btnAtualizar);
        topControlsPanel.add(updateButtonPanel, BorderLayout.EAST);

        add(topControlsPanel, BorderLayout.NORTH);

        // --- Painel Central para as Bombas ---
        bombasContainerPanel = new JPanel(); // Este painel terá o layout para as bombas físicas
        bombasContainerPanel.setLayout(new BoxLayout(bombasContainerPanel, BoxLayout.Y_AXIS)); // Layout vertical para as bombas físicas
        JScrollPane scrollPane = new JScrollPane(bombasContainerPanel);
        add(scrollPane, BorderLayout.CENTER);

        // --- Listeners ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                carregarDadosIniciais();
            }
        });

        btnAtualizar.addActionListener(e -> carregarBombas());
    }

    private void carregarDadosIniciais() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                clientesDisponiveis = HttpClient.getInstance().buscarClientes();
                produtosDisponiveis = HttpClient.getInstance().buscarProdutos();
                System.out.println("Produtos carregados: " + produtosDisponiveis.size()); // Depuração
                return null;
            }

            @Override
            protected void done() {
                try {
                    // Popula combo de clientes
                    cmbClientes.removeAllItems();
                    cmbClientes.addItem(null); // Opção para "Nenhum cliente"
                    clientesDisponiveis.forEach(cmbClientes::addItem);
                    cmbClientes.setRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (value instanceof Cliente) {
                                setText(((Cliente) value).getNomeCompleto());
                            } else {
                                setText("Consumidor Final");
                            }
                            return this;
                        }
                    });
                    carregarBombas(); // Carrega as bombas APÓS carregar clientes e produtos
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaAbastecimento.this, "Falha ao carregar dados iniciais: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void carregarBombas() {
        btnAtualizar.setEnabled(false);
        btnAtualizar.setText("Atualizando...");

        SwingWorker<List<Bomba>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Bomba> doInBackground() throws Exception {
                return HttpClient.getInstance().buscarBombas();
            }

            @Override
            protected void done() {
                try {
                    List<Bomba> bombas = get();
                    bombasContainerPanel.removeAll();
                    bicosPorBombaFisica.clear(); // Limpa o mapa de bicos

                    // Agrupar bicos por bomba física
                    Map<Integer, List<Bomba>> bombasFisicas = bombas.stream()
                            .collect(Collectors.groupingBy(Bomba::getNumeroBombaFisica));

                    // Ordenar as bombas físicas pelo número
                    bombasFisicas.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> {
                                Integer numeroBombaFisica = entry.getKey();
                                List<Bomba> bicosDaBomba = entry.getValue();

                                // Painel para a bomba física (pool)
                                JPanel bombaFisicaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                                bombaFisicaPanel.setBorder(BorderFactory.createTitledBorder("Bomba " + numeroBombaFisica));
                                bombaFisicaPanel.setAlignmentX(LEFT_ALIGNMENT); // Alinha à esquerda no BoxLayout

                                // Ordenar bicos pelo número do bico
                                bicosDaBomba.stream()
                                        .sorted(Comparator.comparing(Bomba::getNumeroBico))
                                        .forEach(bico -> {
                                            Optional<Produto> combustivelProduto = produtosDisponiveis.stream()
                                                    .filter(p -> p.getId().equals(bico.getCombustivelId()))
                                                    .findFirst();

                                            BombaPanel bicoPanel = new BombaPanel(bico, combustivelProduto.orElse(null));
                                            bicoPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                                                @Override
                                                public void mouseClicked(java.awt.event.MouseEvent evt) {
                                                    onBicoClicked(bicoPanel, bico);
                                                }
                                            });
                                            bombaFisicaPanel.add(bicoPanel);

                                            // Adiciona o painel do bico ao mapa
                                            bicosPorBombaFisica.computeIfAbsent(numeroBombaFisica, k -> new ArrayList<>()).add(bicoPanel);
                                        });
                                bombasContainerPanel.add(bombaFisicaPanel);
                            });

                    bombasContainerPanel.revalidate();
                    bombasContainerPanel.repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaAbastecimento.this,
                            "Falha ao carregar status das bombas: " + e.getMessage(),
                            "Erro de Rede",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnAtualizar.setEnabled(true);
                    btnAtualizar.setText("Atualizar Status");
                }
            }
        };
        worker.execute();
    }

    private void onBicoClicked(BombaPanel clickedPanel, Bomba bico) {
        // Desseleciona o bico anteriormente selecionado, se houver
        if (bicoSelecionadoPanel != null && bicoSelecionadoPanel != clickedPanel) {
            bicoSelecionadoPanel.setBorder(BorderFactory.createEtchedBorder()); // Remove a borda de seleção
        }

        // Se o mesmo bico foi clicado novamente, desseleciona
        if (bicoSelecionadoPanel == clickedPanel) {
            bicoSelecionadoPanel.setBorder(BorderFactory.createEtchedBorder());
            bicoSelecionadoPanel = null;
            habilitarOutrosBicos(bico.getNumeroBombaFisica(), true); // Habilita todos os bicos da bomba física
            return;
        }

        // Seleciona o novo bico
        bicoSelecionadoPanel = clickedPanel;
        bicoSelecionadoPanel.setBorder(new LineBorder(Color.BLUE, 3)); // Adiciona borda de seleção

        // Desabilita os outros bicos da mesma bomba física
        habilitarOutrosBicos(bico.getNumeroBombaFisica(), false, clickedPanel);

        // Abre o diálogo de abastecimento para o bico selecionado
        abrirDialogoAbastecimento(bico);
    }

    private void habilitarOutrosBicos(Integer numeroBombaFisica, boolean enable, BombaPanel excludePanel) {
        List<BombaPanel> bicos = bicosPorBombaFisica.get(numeroBombaFisica);
        if (bicos != null) {
            for (BombaPanel bp : bicos) {
                if (bp != excludePanel) {
                    bp.setEnabled(enable);
                    // Opcional: mudar a cor ou aparência para indicar desabilitado
                    bp.setBackground(enable ? bp.getBackground() : Color.LIGHT_GRAY);
                }
            }
        }
    }

    private void habilitarOutrosBicos(Integer numeroBombaFisica, boolean enable) {
        habilitarOutrosBicos(numeroBombaFisica, enable, null);
    }

    private void abrirDialogoAbastecimento(Bomba bomba) {
        if (bomba.getStatus() != StatusBomba.ATIVA) { // <-- USANDO ENUM
            JOptionPane.showMessageDialog(this, "Bico " + bomba.getNumeroBico() + " da Bomba " + bomba.getNumeroBombaFisica() + " não está ativo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Produto> combustivelProduto = produtosDisponiveis.stream()
                .filter(p -> p.getId().equals(bomba.getCombustivelId()))
                .findFirst();

        if (combustivelProduto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Combustível do bico " + bomba.getNumeroBico() + " não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String litrosStr = JOptionPane.showInputDialog(this,
                "Bomba: " + bomba.getNumeroBombaFisica() + " - Bico: " + bomba.getNumeroBico() + " (" + bomba.getNomeCombustivel() + ")\n" +
                        "Preço/Litro: " + combustivelProduto.get().getPrecoVenda() + "\n" +
                        "Digite a quantidade de litros:",
                "Registrar Abastecimento",
                JOptionPane.PLAIN_MESSAGE);

        if (litrosStr != null && !litrosStr.isBlank()) {
            try {
                double litros = Double.parseDouble(litrosStr.replace(",", "."));
                if (litros > 0) {
                    registrarVendaAbastecimento(bomba, combustivelProduto.get(), litros);
                } else {
                    JOptionPane.showMessageDialog(this, "A quantidade de litros deve ser positiva.", "Valor Inválido", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor inválido para litros.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Após o diálogo, desseleciona o bico e reabilita os outros
        if (bicoSelecionadoPanel != null) {
            bicoSelecionadoPanel.setBorder(BorderFactory.createEtchedBorder());
            bicoSelecionadoPanel = null;
            habilitarOutrosBicos(bomba.getNumeroBombaFisica(), true);
        }
    }

    private void registrarVendaAbastecimento(Bomba bomba, Produto combustivelProduto, Double litros) {
        Cliente clienteSelecionado = (Cliente) cmbClientes.getSelectedItem();
        String formaPagamentoStr = (String) cmbFormaPagamento.getSelectedItem();

        if (formaPagamentoStr == null || formaPagamentoStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecione a forma de pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FormaPagamento formaPagamento = FormaPagamento.valueOf(formaPagamentoStr); // Convertendo String para Enum

        if (formaPagamento == FormaPagamento.CONTA_CLIENTE && clienteSelecionado == null) { // Corrigido de CREDITO_CLIENTE para CONTA_CLIENTE
            JOptionPane.showMessageDialog(this, "Para 'CONTA_CLIENTE', selecione um cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Venda novaVenda = new Venda();
        novaVenda.setFuncionarioId(1L); // TODO: Obter ID do funcionário logado

        if (clienteSelecionado != null) {
            novaVenda.setClienteId(clienteSelecionado.getId());
        }

        novaVenda.setFormaPagamento(formaPagamento);

        // Inicializa a lista de itens se for nula
        if (novaVenda.getItens() == null) {
            novaVenda.setItens(new ArrayList<>());
        }

        ItemVenda itemAbastecimento = new ItemVenda();
        itemAbastecimento.setProdutoId(combustivelProduto.getId());
        itemAbastecimento.setNomeProduto(combustivelProduto.getNome());
        itemAbastecimento.setQuantidade(litros); // Agora é Double
        itemAbastecimento.setPrecoUnitario(combustivelProduto.getPrecoVenda());
        itemAbastecimento.setSubtotal(combustivelProduto.getPrecoVenda().multiply(BigDecimal.valueOf(litros)));

        novaVenda.getItens().add(itemAbastecimento);

        SwingWorker<Venda, Void> worker = new SwingWorker<>() { // Alterado para retornar Venda
            @Override
            protected Venda doInBackground() throws Exception {
                return HttpClient.getInstance().criarVenda(novaVenda);
            }

            @Override
            protected void done() {
                try {
                    Venda vendaCriada = get(); // Obtém o objeto Venda retornado
                    if (vendaCriada != null && vendaCriada.getId() != null) {
                        JOptionPane.showMessageDialog(TelaAbastecimento.this, "Abastecimento (Venda) registrado com sucesso! ID: " + vendaCriada.getId(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                        // Emitir e exibir cupom fiscal da venda original
                        String cupomContent = HttpClient.getInstance().emitirCupomFiscal(vendaCriada.getId());
                        exibirCupomDialog(cupomContent, "CUPOM FISCAL");

                        carregarBombas();
                        cmbClientes.setSelectedIndex(0);
                        cmbFormaPagamento.setSelectedIndex(0);
                    } else {
                        JOptionPane.showMessageDialog(TelaAbastecimento.this, "Falha ao registrar o abastecimento (Venda).", "Erro no Servidor", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaAbastecimento.this, "Falha na comunicação com o servidor: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void exibirCupomDialog(String cupomContent, String titulo) {
        JDialog cupomDialog = new JDialog(SwingUtilities.getWindowAncestor(this), titulo, Dialog.ModalityType.APPLICATION_MODAL);
        cupomDialog.setLayout(new BorderLayout());
        cupomDialog.setSize(400, 500);
        cupomDialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea(cupomContent);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);

        cupomDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> cupomDialog.dispose());

        JButton printButton = new JButton("Imprimir");
        printButton.addActionListener(e -> {
            try {
                textArea.print();
            } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(cupomDialog, "Erro ao imprimir: " + ex.getMessage(), "Erro de Impressão", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        cupomDialog.add(buttonPanel, BorderLayout.SOUTH);

        cupomDialog.setVisible(true);
    }
}