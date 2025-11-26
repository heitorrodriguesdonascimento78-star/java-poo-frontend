package com.br.view;

import com.br.model.Produto;
import com.br.util.HttpClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TelaProduto extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private List<Produto> produtosAtuais = new ArrayList<>();

    public TelaProduto() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Painel de Botões Superior ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNovo = new JButton("Novo Produto");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        topPanel.add(btnNovo);
        topPanel.add(btnEditar);
        topPanel.add(btnExcluir);

        add(topPanel, BorderLayout.NORTH);

        // --- Tabela de Produtos ---
        String[] colunas = {"ID", "Nome", "Descrição", "Cód. Barras", "Preço Venda", "Custo", "Tipo", "Estoque Atual"};
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
                carregarProdutos();
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean isRowSelected = table.getSelectedRow() != -1;
            btnEditar.setEnabled(isRowSelected);
            btnExcluir.setEnabled(isRowSelected);
        });

        btnNovo.addActionListener(e -> abrirFormularioProduto(null));
        btnEditar.addActionListener(e -> editarProdutoSelecionado());
        btnExcluir.addActionListener(e -> excluirProdutoSelecionado());
    }

    private void carregarProdutos() {
        tableModel.setRowCount(0);
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);

        SwingWorker<List<Produto>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Produto> doInBackground() throws Exception {
                return HttpClient.getInstance().buscarProdutos();
            }

            @Override
            protected void done() {
                try {
                    produtosAtuais = get(); // Armazena a lista de produtos
                    tableModel.setRowCount(0); // Limpa a tabela antes de adicionar
                    NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                    for (Produto produto : produtosAtuais) {
                        String precoVendaFormatado = Optional.ofNullable(produto.getPrecoVenda()).map(formatadorMoeda::format).orElse("N/A");
                        String custoFormatado = Optional.ofNullable(produto.getCusto()).map(formatadorMoeda::format).orElse("N/A");
                        String estoqueAtualFormatado = Optional.ofNullable(produto.getEstoqueAtual()).map(Object::toString).orElse("N/A");

                        tableModel.addRow(new Object[]{
                                produto.getId(),
                                produto.getNome(),
                                produto.getDescricao(),
                                produto.getCodigoBarras(),
                                precoVendaFormatado,
                                custoFormatado,
                                produto.getTipo(),
                                estoqueAtualFormatado
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaProduto.this, "Falha ao carregar produtos: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void abrirFormularioProduto(Produto produto) {
        Window window = SwingUtilities.getWindowAncestor(this);
        Frame ownerFrame = (window instanceof Frame) ? (Frame) window : null;

        ProdutoDialog dialog = new ProdutoDialog(ownerFrame, produto);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            carregarProdutos();
        }
    }

    private void editarProdutoSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Produto produtoSelecionado = produtosAtuais.get(selectedRow);
            abrirFormularioProduto(produtoSelecionado);
        }
    }

    private void excluirProdutoSelecionado() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Produto produtoSelecionado = produtosAtuais.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja excluir o produto: " + produtoSelecionado.getNome() + "?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return HttpClient.getInstance().excluirProduto(produtoSelecionado.getId());
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean sucesso = get();
                            if (sucesso) {
                                JOptionPane.showMessageDialog(TelaProduto.this, "Produto excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                carregarProdutos();
                            } else {
                                JOptionPane.showMessageDialog(TelaProduto.this, "Falha ao excluir o produto.", "Erro no Servidor", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(TelaProduto.this, "Falha na comunicação com o servidor: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
            }
        }
    }
}