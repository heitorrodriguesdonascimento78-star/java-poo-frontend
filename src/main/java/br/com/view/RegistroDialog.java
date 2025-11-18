package com.br.pdvfrontend.view;

import com.br.pdvfrontend.model.Produto;
import com.br.pdvfrontend.util.HttpClient;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Optional;

public class ProdutoDialog extends JDialog {

    private JTextField txtId;
    private JTextField txtNome;
    private JTextArea txtDescricao;
    private JTextField txtCodigoBarras;
    private JTextField txtPrecoVenda;
    private JTextField txtCusto;
    private JTextField txtTipo;
    private JSpinner spnEstoqueAtual;

    private JButton btnSalvar;
    private JButton btnCancelar;

    private Produto produto; // Produto que está sendo editado/criado
    private boolean saved = false; // Indica se o produto foi salvo com sucesso

    public ProdutoDialog(Frame owner, Produto produto) {
        super(owner, produto == null ? "Novo Produto" : "Editar Produto", true);
        this.produto = produto;

        setSize(500, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- PAINEL DO FORMULÁRIO ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID (apenas para visualização em edição, não editável)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtId = new JTextField(15);
        txtId.setEditable(false);
        formPanel.add(txtId, gbc);
        row++;

        // Nome
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        formPanel.add(txtNome, gbc);
        row++;

        // Descrição
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDescricao = new JTextArea(3, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtDescricao), gbc);
        row++;

        // Código de Barras
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Cód. Barras:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCodigoBarras = new JTextField(15);
        formPanel.add(txtCodigoBarras, gbc);
        row++;

        // Preço Venda
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Preço Venda:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPrecoVenda = new JTextField();
        formPanel.add(txtPrecoVenda, gbc);
        row++;

        // Custo
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Custo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCusto = new JTextField();
        formPanel.add(txtCusto, gbc);
        row++;

        // Tipo
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtTipo = new JTextField();
        formPanel.add(txtTipo, gbc);
        row++;

        // Estoque Atual
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Estoque Atual:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        spnEstoqueAtual = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 99999.0, 0.1));
        formPanel.add(spnEstoqueAtual, gbc);
        row++;

        add(formPanel, BorderLayout.CENTER);

        // --- PAINEL DE BOTÕES ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- PREENCHER CAMPOS SE FOR EDIÇÃO ---
        if (this.produto != null) {
            preencherFormulario(this.produto);
        }

        // --- AÇÕES ---
        btnSalvar.addActionListener(e -> salvarProduto());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void preencherFormulario(Produto p) {
        txtId.setText(String.valueOf(p.getId()));
        txtNome.setText(p.getNome());
        txtDescricao.setText(p.getDescricao());
        txtCodigoBarras.setText(p.getCodigoBarras());
        txtPrecoVenda.setText(Optional.ofNullable(p.getPrecoVenda()).map(BigDecimal::toString).orElse(""));
        txtCusto.setText(Optional.ofNullable(p.getCusto()).map(BigDecimal::toString).orElse(""));
        txtTipo.setText(p.getTipo());
        spnEstoqueAtual.setValue(Optional.ofNullable(p.getEstoqueAtual()).orElse(0.0));
    }

    private void salvarProduto() {
        if (txtNome.getText().isBlank() || txtPrecoVenda.getText().isBlank() || txtCusto.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Nome, Preço de Venda e Custo são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean isNew = (this.produto == null);
            Produto produtoASalvar = isNew ? new Produto() : this.produto;

            produtoASalvar.setNome(txtNome.getText());
            produtoASalvar.setDescricao(txtDescricao.getText());
            produtoASalvar.setCodigoBarras(txtCodigoBarras.getText());
            produtoASalvar.setPrecoVenda(new BigDecimal(txtPrecoVenda.getText().replace(",", ".")));
            produtoASalvar.setCusto(new BigDecimal(txtCusto.getText().replace(",", ".")));
            produtoASalvar.setTipo(txtTipo.getText());
            produtoASalvar.setEstoqueAtual((Double) spnEstoqueAtual.getValue());

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (isNew) {
                        return HttpClient.getInstance().criarProduto(produtoASalvar);
                    } else {
                        return HttpClient.getInstance().atualizarProduto(produtoASalvar);
                    }
                }

                @Override
                protected void done() {
                    try {
                        boolean sucesso = get();
                        if (sucesso) {
                            JOptionPane.showMessageDialog(ProdutoDialog.this, "Produto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            saved = true;
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(ProdutoDialog.this, "Falha ao salvar o produto.", "Erro no Servidor", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ProdutoDialog.this, "Falha na comunicação com o servidor: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores numéricos inválidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}