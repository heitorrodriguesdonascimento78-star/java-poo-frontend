package com.br.view;

import com.br.model.Cliente;
import com.br.model.MovimentoContaCliente;
import com.br.util.HttpClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel; // <-- IMPORT ADICIONADO
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TelaContaCliente extends JPanel {

    private JComboBox<Cliente> cmbClientes;
    private JLabel lblSaldoAtual;
    private JTextField txtValorMovimento;
    private JButton btnRegistrarPagamento;
    private JButton btnRegistrarDivida; // Para registrar nova dívida
    private JTable tblHistorico;
    private DefaultTableModel tableModel;
    private JLabel lblBalançoRodape; // <-- NOVO LABEL PARA O RODAPÉ

    private List<Cliente> clientesDisponiveis = new ArrayList<>();
    private Cliente clienteSelecionado;

    public TelaContaCliente() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Usar EmptyBorder para padding

        // --- PAINEL SUPERIOR: Seleção de Cliente e Saldo ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Cliente:"));
        cmbClientes = new JComboBox<>();
        topPanel.add(cmbClientes);
        lblSaldoAtual = new JLabel("Saldo Atual: R$ 0,00");
        lblSaldoAtual.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topPanel.add(Box.createHorizontalStrut(20)); // Espaçamento
        topPanel.add(lblSaldoAtual);
        add(topPanel, BorderLayout.NORTH);

        // --- PAINEL CENTRAL: Histórico de Transações ---
        String[] colunas = {"ID", "Data/Hora", "Tipo", "Valor", "Descrição"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblHistorico = new JTable(tableModel);
        JScrollPane centerScrollPane = new JScrollPane(tblHistorico);
        centerScrollPane.setBorder(BorderFactory.createTitledBorder("Histórico de Transações"));
        add(centerScrollPane, BorderLayout.CENTER);

        // --- PAINEL INFERIOR: Registrar Movimento e Rodapé ---
        JPanel southPanel = new JPanel(new BorderLayout()); // Painel para agrupar

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(new JLabel("Valor:"));
        txtValorMovimento = new JTextField(10);
        bottomPanel.add(txtValorMovimento);
        btnRegistrarPagamento = new JButton("Registrar Pagamento");
        bottomPanel.add(btnRegistrarPagamento);
        // btnRegistrarDivida = new JButton("Registrar Dívida"); // Futuramente, se houver endpoint
        // bottomPanel.add(btnRegistrarDivida);
        southPanel.add(bottomPanel, BorderLayout.NORTH); // Adiciona o painel de botões ao NORTE do southPanel

        lblBalançoRodape = new JLabel("Balanço Total: R$ 0,00"); // <-- LABEL DO RODAPÉ
        lblBalançoRodape.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBalançoRodape.setHorizontalAlignment(SwingConstants.RIGHT);
        lblBalançoRodape.setBorder(new EmptyBorder(5, 0, 0, 10)); // Padding inferior
        southPanel.add(lblBalançoRodape, BorderLayout.SOUTH); // Adiciona o southPanel ao SUL da TelaContaCliente

        add(southPanel, BorderLayout.SOUTH); // Adiciona o southPanel ao SUL da TelaContaCliente

        // --- Listeners ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                carregarClientes();
            }
        });

        cmbClientes.addActionListener(e -> {
            clienteSelecionado = (Cliente) cmbClientes.getSelectedItem();
            if (clienteSelecionado != null && clienteSelecionado.getId() != null) {
                atualizarDetalhesCliente();
            } else {
                lblSaldoAtual.setText("Saldo Atual: R$ 0,00");
                lblBalançoRodape.setText("Balanço Total: R$ 0,00"); // Reseta o rodapé
                tableModel.setRowCount(0);
            }
        });

        btnRegistrarPagamento.addActionListener(e -> registrarPagamento());
    }

    private void carregarClientes() {
        SwingWorker<List<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                return HttpClient.getInstance().buscarClientes();
            }

            @Override
            protected void done() {
                try {
                    clientesDisponiveis = get();
                    cmbClientes.removeAllItems();
                    cmbClientes.addItem(null); // Opção para "Selecione um cliente"
                    clientesDisponiveis.forEach(cmbClientes::addItem);
                    cmbClientes.setRenderer(new DefaultListCellRenderer() {
                        @Override
                        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            if (value instanceof Cliente) {
                                setText(((Cliente) value).getNomeCompleto() + " (CPF/CNPJ: " + ((Cliente) value).getCpfCnpj() + ")");
                            } else {
                                setText("Selecione um Cliente");
                            }
                            return this;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaContaCliente.this, "Falha ao carregar clientes: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void atualizarDetalhesCliente() {
        if (clienteSelecionado == null || clienteSelecionado.getId() == null) {
            return;
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        lblSaldoAtual.setText("Saldo Atual: " + currencyFormatter.format(clienteSelecionado.getSaldoAtual()));

        SwingWorker<List<MovimentoContaCliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MovimentoContaCliente> doInBackground() throws Exception {
                return HttpClient.getInstance().getHistoricoConta(clienteSelecionado.getId());
            }

            @Override
            protected void done() {
                try {
                    List<MovimentoContaCliente> historico = get();
                    tableModel.setRowCount(0); // Limpa a tabela
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    BigDecimal balancoTotal = BigDecimal.ZERO; // <-- Variável para o balanço

                    for (MovimentoContaCliente movimento : historico) {
                        tableModel.addRow(new Object[]{
                                movimento.getId(),
                                movimento.getDataHora().format(dateTimeFormatter),
                                movimento.getTipo().name(),
                                currencyFormatter.format(movimento.getValor()),
                                movimento.getDescricao()
                        });
                        // Calcula o balanço
                        if (movimento.getTipo() != null) {
                            switch (movimento.getTipo()) {
                                case CREDITO:
                                    balancoTotal = balancoTotal.add(movimento.getValor());
                                    break;
                                case DEBITO:
                                    balancoTotal = balancoTotal.subtract(movimento.getValor());
                                    break;
                            }
                        }
                    }
                    // Atualiza o rodapé com o balanço total
                    lblBalançoRodape.setText("Balanço Total: " + currencyFormatter.format(balancoTotal));

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaContaCliente.this, "Falha ao carregar histórico: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void registrarPagamento() {
        if (clienteSelecionado == null || clienteSelecionado.getId() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para registrar o pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtValorMovimento.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o valor do pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal valor = new BigDecimal(txtValorMovimento.getText().replace(",", "."));
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "O valor do pagamento deve ser positivo.", "Valor Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnRegistrarPagamento.setEnabled(false);
            btnRegistrarPagamento.setText("Registrando...");

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return HttpClient.getInstance().pagarConta(clienteSelecionado.getId(), valor);
                }

                @Override
                protected void done() {
                    try {
                        boolean sucesso = get();
                        if (sucesso) {
                            JOptionPane.showMessageDialog(TelaContaCliente.this, "Pagamento registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            txtValorMovimento.setText("");
                            // Recarrega os detalhes do cliente para atualizar saldo e histórico
                            atualizarDetalhesCliente();
                        } else {
                            JOptionPane.showMessageDialog(TelaContaCliente.this, "Falha ao registrar pagamento.", "Erro no Servidor", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(TelaContaCliente.this, "Falha na comunicação com o servidor: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        btnRegistrarPagamento.setEnabled(true);
                        btnRegistrarPagamento.setText("Registrar Pagamento");
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido para pagamento.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}