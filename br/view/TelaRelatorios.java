package com.br.view;

import com.br.model.Venda;
import com.br.util.HttpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TelaRelatorios extends JPanel {

    private JTable tblVendas;
    private DefaultTableModel tableModel;
    private JButton btnAtualizarVendas;
    private JButton btnReemitirCupom;

    private List<Venda> vendasDisponiveis;

    public TelaRelatorios() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Painel de Controles Superiores ---
        JPanel topControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAtualizarVendas = new JButton("Atualizar Vendas");
        btnReemitirCupom = new JButton("Reemitir Cupom Fiscal");
        topControlsPanel.add(btnAtualizarVendas);
        topControlsPanel.add(btnReemitirCupom);
        add(topControlsPanel, BorderLayout.NORTH);

        // --- Tabela de Vendas ---
        String[] colunas = {"ID da Venda", "Data/Hora", "Cliente", "Forma Pagamento", "Valor Total", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna todas as células não editáveis
            }
        };
        tblVendas = new JTable(tableModel);
        tblVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha
        JScrollPane scrollPane = new JScrollPane(tblVendas);
        add(scrollPane, BorderLayout.CENTER);

        // --- Listeners ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                carregarVendas();
            }
        });

        btnAtualizarVendas.addActionListener(e -> carregarVendas());
        btnReemitirCupom.addActionListener(e -> reemitirCupomFiscal());
    }

    private void carregarVendas() {
        btnAtualizarVendas.setEnabled(false);
        btnAtualizarVendas.setText("Carregando...");

        SwingWorker<List<Venda>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Venda> doInBackground() throws Exception {
                return HttpClient.getInstance().buscarVendas();
            }

            @Override
            protected void done() {
                try {
                    vendasDisponiveis = get();
                    tableModel.setRowCount(0); // Limpa a tabela

                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                    if (vendasDisponiveis != null) {
                        for (Venda venda : vendasDisponiveis) {
                            tableModel.addRow(new Object[]{
                                    venda.getId(),
                                    venda.getDataHora() != null ? venda.getDataHora().format(dateFormatter) : "N/A",
                                    venda.getNomeCliente() != null ? venda.getNomeCliente() : "Consumidor Final",
                                    venda.getFormaPagamento(),
                                    currencyFormatter.format(venda.getValorTotal()),
                                    venda.getStatus()
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaRelatorios.this, "Falha ao carregar vendas: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnAtualizarVendas.setEnabled(true);
                    btnAtualizarVendas.setText("Atualizar Vendas");
                }
            }
        };
        worker.execute();
    }

    private void reemitirCupomFiscal() {
        int selectedRow = tblVendas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para reemitir o cupom.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long vendaId = (Long) tableModel.getValueAt(selectedRow, 0); // O ID da venda está na primeira coluna

        btnReemitirCupom.setEnabled(false);
        btnReemitirCupom.setText("Reemitindo...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return HttpClient.getInstance().reemitirCupomFiscal(vendaId);
            }

            @Override
            protected void done() {
                try {
                    String cupomContent = get();
                    exibirCupomDialog(cupomContent);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaRelatorios.this, "Falha ao reemitir cupom: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnReemitirCupom.setEnabled(true);
                    btnReemitirCupom.setText("Reemitir Cupom Fiscal");
                }
            }
        };
        worker.execute();
    }

    private void exibirCupomDialog(String cupomContent) {
        JDialog cupomDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Cupom Fiscal Reemitido", Dialog.ModalityType.APPLICATION_MODAL);
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

        // NOVO: Botão de Impressão
        JButton printButton = new JButton("Imprimir");
        printButton.addActionListener(e -> {
            try {
                textArea.print(); // Imprime o conteúdo do JTextArea
            } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(cupomDialog, "Erro ao imprimir: " + ex.getMessage(), "Erro de Impressão", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(printButton); // Adiciona o botão de impressão
        buttonPanel.add(closeButton);
        cupomDialog.add(buttonPanel, BorderLayout.SOUTH);

        cupomDialog.setVisible(true);
    }
}