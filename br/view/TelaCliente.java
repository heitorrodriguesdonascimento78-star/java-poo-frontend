package com.br.view;

import com.br.model.Cliente;
import com.br.util.HttpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TelaCliente extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private List<Cliente> clientesAtuais = new ArrayList<>();

    public TelaCliente() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Painel de Botões Superior ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNovo = new JButton("Novo Cliente");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        topPanel.add(btnNovo);
        topPanel.add(btnEditar);
        topPanel.add(btnExcluir);

        add(topPanel, BorderLayout.NORTH);

        // --- Tabela de Clientes (COLUNAS RE-ADICIONADAS) ---
        String[] colunas = {"ID", "Nome Completo", "CPF/CNPJ", "Tipo", "Email", "Telefone", "Data Nasc.", "Limite Crédito", "Saldo Atual"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Listeners ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                carregarClientes();
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean isRowSelected = table.getSelectedRow() != -1;
            btnEditar.setEnabled(isRowSelected);
            btnExcluir.setEnabled(isRowSelected);
        });

        btnNovo.addActionListener(e -> abrirFormularioCliente(null));
        btnEditar.addActionListener(e -> editarClienteSelecionado());
        btnExcluir.addActionListener(e -> excluirClienteSelecionado());
    }

    private void carregarClientes() {
        tableModel.setRowCount(0);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);

        SwingWorker<List<Cliente>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                return HttpClient.getInstance().buscarClientes();
            }

            @Override
            protected void done() {
                try {
                    clientesAtuais = get();
                    tableModel.setRowCount(0);
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                    for (Cliente cliente : clientesAtuais) {
                        String dataNascFormatada = Optional.ofNullable(cliente.getDataNascimento())
                                .map(data -> data.format(dateFormatter))
                                .orElse("N/A");
                        String limiteCreditoFormatado = Optional.ofNullable(cliente.getLimiteCredito())
                                .map(currencyFormatter::format)
                                .orElse("N/A");
                        String saldoAtualFormatado = Optional.ofNullable(cliente.getSaldoAtual())
                                .map(currencyFormatter::format)
                                .orElse("N/A");

                        // DADOS RE-ADICIONADOS PARA EXIBIÇÃO
                        tableModel.addRow(new Object[]{
                                cliente.getId(),
                                cliente.getNomeCompleto(),
                                cliente.getTipoPessoa(),
                                cliente.getCpfCnpj(),
                                cliente.getEmail(),
                                cliente.getTelefone(),
                                dataNascFormatada,
                                limiteCreditoFormatado,
                                saldoAtualFormatado
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaCliente.this, "Falha ao carregar clientes: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void abrirFormularioCliente(Cliente cliente) {
        Window window = SwingUtilities.getWindowAncestor(this);
        Frame ownerFrame = (window instanceof Frame) ? (Frame) window : null;

        ClienteDialog dialog = new ClienteDialog(ownerFrame, cliente);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            carregarClientes();
        }
    }

    private void editarClienteSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Cliente clienteSelecionado = clientesAtuais.get(selectedRow);
            abrirFormularioCliente(clienteSelecionado);
        }
    }

    private void excluirClienteSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Cliente clienteSelecionado = clientesAtuais.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja excluir o cliente: " + clienteSelecionado.getNomeCompleto() + "?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return HttpClient.getInstance().excluirCliente(clienteSelecionado.getId());
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean sucesso = get();
                            if (sucesso) {
                                JOptionPane.showMessageDialog(TelaCliente.this, "Cliente excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                carregarClientes();
                            } else {
                                JOptionPane.showMessageDialog(TelaCliente.this, "Falha ao excluir o cliente.", "Erro no Servidor", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(TelaCliente.this, "Falha na comunicação com o servidor: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
            }
        }
    }
}