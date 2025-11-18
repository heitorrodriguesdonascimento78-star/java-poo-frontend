package com.br.pdvfrontend.view;

import com.br.pdvfrontend.enums.FormaPagamento;
import com.br.pdvfrontend.model.Cliente;
import com.br.pdvfrontend.model.ItemVenda;
import com.br.pdvfrontend.model.Produto;
import com.br.pdvfrontend.model.Venda;
import com.br.pdvfrontend.util.HttpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TelaVenda extends JPanel {

    private JComboBox<Cliente> cmbClientes;
    private JComboBox<String> cmbFormaPagamento;
    private JTable tblItensVenda;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JButton btnConcluirVenda;

    private JList<Produto> listProdutosDisponiveis;
    private DefaultListModel<Produto> listModel;
    private JButton btnAdicionarProduto;

    private List<ItemVenda> itensVendaAtual = new ArrayList<>();
    private BigDecimal totalVenda = BigDecimal.ZERO;

    // Variáveis de instância para armazenar os dados carregados
    private List<Cliente> clientesDisponiveis = new ArrayList<>();
    private List<Produto> produtosDisponiveis = new ArrayList<>();

    public TelaVenda() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PAINEL SUPERIOR: Cliente e Pagamento ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Cliente:"));
        cmbClientes = new JComboBox<>();
        topPanel.add(cmbClientes);
        topPanel.add(new JLabel("Forma de Pagamento:"));
        cmbFormaPagamento = new JComboBox<>(new String[]{"DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO", "PIX", "CONTA_CLIENTE"});
        topPanel.add(cmbFormaPagamento);
        add(topPanel, BorderLayout.NORTH);

        // --- PAINEL CENTRAL: Itens da Venda ---
        String[] colunas = {"Produto", "Qtd.", "Preço Unit.", "Subtotal"};
        tableModel = new DefaultTableModel(colunas, 0);
        tblItensVenda = new JTable(tableModel);
        JScrollPane centerScrollPane = new JScrollPane(tblItensVenda);
        centerScrollPane.setBorder(BorderFactory.createTitledBorder("Itens da Venda"));
        add(centerScrollPane, BorderLayout.CENTER);

        // --- PAINEL DIREITO: Adicionar Produtos ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Adicionar Produto"));
        listModel = new DefaultListModel<>();
        listProdutosDisponiveis = new JList<>(listModel);
        rightPanel.add(new JScrollPane(listProdutosDisponiveis), BorderLayout.CENTER);
        btnAdicionarProduto = new JButton("Adicionar à Venda");
        rightPanel.add(btnAdicionarProduto, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        // --- PAINEL INFERIOR: Total e Concluir ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total: R$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        bottomPanel.add(lblTotal, BorderLayout.WEST);
        btnConcluirVenda = new JButton("Concluir Venda");
        btnConcluirVenda.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bottomPanel.add(btnConcluirVenda, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Listeners ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                carregarDadosIniciais();
            }
        });

        btnAdicionarProduto.addActionListener(e -> adicionarProdutoSelecionado());
        btnConcluirVenda.addActionListener(e -> concluirVenda());
    }

    // Classe auxiliar para retornar múltiplos valores do doInBackground
    static class InitialData {
        List<Cliente> clientes;
        List<Produto> produtos;

        InitialData(List<Cliente> clientes, List<Produto> produtos) {
            this.clientes = clientes;
            this.produtos = produtos;
        }
    }

    private void carregarDadosIniciais() {
        SwingWorker<InitialData, Void> worker = new SwingWorker<>() {
            @Override
            protected InitialData doInBackground() throws Exception {
                List<Cliente> fetchedClients = HttpClient.getInstance().buscarClientes();
                List<Produto> fetchedProducts = HttpClient.getInstance().buscarProdutos();

                // Filtra apenas produtos do tipo "CONVENIENCIA"
                List<Produto> filteredProducts = fetchedProducts.stream()
                        .filter(p -> "CONVENIENCIA".equalsIgnoreCase(p.getTipo()))
                        .collect(Collectors.toList());

                return new InitialData(fetchedClients, filteredProducts);
            }

            @Override
            protected void done() {
                try {
                    InitialData data = get();
                    clientesDisponiveis = data.clientes;
                    produtosDisponiveis = data.produtos; // Atribui à variável de instância

                    // Popula lista de produtos
                    listModel.clear();
                    produtosDisponiveis.forEach(listModel::addElement);
                    listProdutosDisponiveis.setCellRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (value instanceof Produto) {
                                setText(((Produto) value).getNome() + " (R$ " + ((Produto) value).getPrecoVenda() + ")");
                            }
                            return this;
                        }
                    });

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
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaVenda.this, "Falha ao carregar dados iniciais: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void adicionarProdutoSelecionado() {
        Produto produtoSelecionado = listProdutosDisponiveis.getSelectedValue();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para adicionar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qtdStr = JOptionPane.showInputDialog(this, "Digite a quantidade:", "Adicionar " + produtoSelecionado.getNome(), JOptionPane.PLAIN_MESSAGE);
        if (qtdStr != null && !qtdStr.isBlank()) {
            try {
                double quantidade = Double.parseDouble(qtdStr.replace(",", "."));
                if (quantidade <= 0) {
                    JOptionPane.showMessageDialog(this, "A quantidade deve ser positiva.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ItemVenda novoItem = new ItemVenda();
                novoItem.setProdutoId(produtoSelecionado.getId());
                novoItem.setNomeProduto(produtoSelecionado.getNome());
                novoItem.setQuantidade(quantidade);
                novoItem.setPrecoUnitario(produtoSelecionado.getPrecoVenda());
                novoItem.setSubtotal(produtoSelecionado.getPrecoVenda().multiply(BigDecimal.valueOf(quantidade)));

                itensVendaAtual.add(novoItem);
                atualizarTabelaEtotal();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void atualizarTabelaEtotal() {
        tableModel.setRowCount(0);
        totalVenda = BigDecimal.ZERO;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        for (ItemVenda item : itensVendaAtual) {
            tableModel.addRow(new Object[]{
                    item.getNomeProduto(),
                    item.getQuantidade(),
                    currencyFormatter.format(item.getPrecoUnitario()),
                    currencyFormatter.format(item.getSubtotal())
            });
            totalVenda = totalVenda.add(item.getSubtotal());
        }
        lblTotal.setText("Total: " + currencyFormatter.format(totalVenda));
    }

    private void concluirVenda() {
        if (itensVendaAtual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um item à venda.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Venda novaVenda = new Venda();
        novaVenda.setFuncionarioId(1L); // TODO: Obter ID do funcionário logado

        Cliente clienteSelecionado = (Cliente) cmbClientes.getSelectedItem();
        if (clienteSelecionado != null) {
            novaVenda.setClienteId(clienteSelecionado.getId());
        }

        novaVenda.setFormaPagamento(FormaPagamento.valueOf((String) cmbFormaPagamento.getSelectedItem()));
        novaVenda.setItens(itensVendaAtual);

        SwingWorker<Venda, Void> worker = new SwingWorker<>() {
            @Override
            protected Venda doInBackground() throws Exception {
                return HttpClient.getInstance().criarVenda(novaVenda);
            }

            @Override
            protected void done() {
                try {
                    Venda vendaCriada = get();
                    if (vendaCriada != null && vendaCriada.getId() != null) {
                        JOptionPane.showMessageDialog(TelaVenda.this, "Venda concluída com sucesso! ID: " + vendaCriada.getId(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        // Limpa a tela para uma nova venda
                        itensVendaAtual.clear();
                        atualizarTabelaEtotal();
                        cmbClientes.setSelectedIndex(0);
                        cmbFormaPagamento.setSelectedIndex(0);
                    } else {
                        JOptionPane.showMessageDialog(TelaVenda.this, "Falha ao concluir a venda.", "Erro no Servidor", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaVenda.this, "Falha na comunicação: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}