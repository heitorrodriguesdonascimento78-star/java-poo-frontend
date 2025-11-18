package com.br.pdvfrontend.ui;

import com.br.pdvfrontend.model.Venda;
import com.br.pdvfrontend.util.HttpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

public class TelaRelatoriosVendas extends JFrame {

    private JTextField txtFuncionarioId;
    private JButton btnBuscarVendas;
    private JTable tableVendas;
    private DefaultTableModel tableModel;
    private JButton btnReemitirCupom;

    private HttpClient httpClient;

    public TelaRelatoriosVendas() {
        super("Relatórios de Vendas");
        httpClient = HttpClient.getInstance();
        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        txtFuncionarioId = new JTextField(10);
        btnBuscarVendas = new JButton("Buscar Vendas");
        btnReemitirCupom = new JButton("Reemitir Cupom");

        // Configuração da tabela
        tableModel = new DefaultTableModel(new Object[]{"ID Venda", "Data/Hora", "Funcionário", "Cliente", "Forma Pagamento", "Valor Total", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células não editáveis
            }
        };
        tableVendas = new JTable(tableModel);
        tableVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Apenas uma linha pode ser selecionada
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10)); // Adiciona espaçamento

        // Painel superior para entrada do ID do funcionário e botão de busca
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("ID do Funcionário:"));
        topPanel.add(txtFuncionarioId);
        topPanel.add(btnBuscarVendas);
        add(topPanel, BorderLayout.NORTH);

        // Tabela de vendas no centro
        JScrollPane scrollPane = new JScrollPane(tableVendas);
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior para o botão de reemissão
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.add(btnReemitirCupom);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela
    }

    private void setupListeners() {
        btnBuscarVendas.addActionListener(e -> buscarVendas());
        btnReemitirCupom.addActionListener(e -> reemitirCupom());
    }

    private void buscarVendas() {
        String idText = txtFuncionarioId.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira o ID do funcionário.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Long funcionarioId = Long.parseLong(idText);
            List<Venda> vendas = httpClient.buscarVendasPorFuncionario(funcionarioId);

            tableModel.setRowCount(0); // Limpa a tabela
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            if (vendas != null && !vendas.isEmpty()) {
                for (Venda venda : vendas) {
                    tableModel.addRow(new Object[]{
                            venda.getId(),
                            venda.getDataHora().format(formatter),
                            venda.getNomeFuncionario() != null ? venda.getNomeFuncionario() : "N/A",
                            venda.getNomeCliente() != null ? venda.getNomeCliente() : "N/A",
                            venda.getFormaPagamento(),
                            venda.getValorTotal(),
                            venda.getStatus()
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Nenhuma venda encontrada para o funcionário ID: " + funcionarioId, "Informação", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID do Funcionário inválido. Por favor, insira um número.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IOException | InterruptedException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar vendas: " + ex.getMessage(), "Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void reemitirCupom() {
        int selectedRow = tableVendas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma venda na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long vendaId = (Long) tableModel.getValueAt(selectedRow, 0); // Pega o ID da venda da primeira coluna

        try {
            String cupomContent = httpClient.reemitirCupomFiscal(vendaId);
            exibirCupomDialog(cupomContent);
        } catch (IOException | InterruptedException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao reemitir cupom: " + ex.getMessage(), "Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exibirCupomDialog(String cupomContent) {
        JDialog cupomDialog = new JDialog(this, "Cupom Fiscal Reemitido", true); // true para modal
        cupomDialog.setLayout(new BorderLayout());
        cupomDialog.setSize(400, 300);
        cupomDialog.setLocationRelativeTo(this); // Centraliza em relação à tela principal

        JTextArea textArea = new JTextArea(cupomContent);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Fonte monoespaçada para melhor formatação de cupom
        JScrollPane scrollPane = new JScrollPane(textArea);

        cupomDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> cupomDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        cupomDialog.add(buttonPanel, BorderLayout.SOUTH);

        cupomDialog.setVisible(true);
    }

    public static void main(String[] args) {
        // Exemplo de como iniciar a tela
        SwingUtilities.invokeLater(() -> {
            new TelaRelatoriosVendas().setVisible(true);
        });
    }
}