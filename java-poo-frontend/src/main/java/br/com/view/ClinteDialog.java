package com.br.pdvfrontend.view;

import com.br.pdvfrontend.model.Cliente;
import com.br.pdvfrontend.util.HttpClient;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public class ClienteDialog extends JDialog {

    private JTextField txtId;
    private JTextField txtNomeCompleto;
    private JFormattedTextField txtCpfCnpj;
    private JComboBox<String> cmbTipoPessoa;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JSpinner spnDataNascimento;
    private JTextField txtLimiteCredito;

    private JButton btnSalvar;
    private JButton btnCancelar;

    private Cliente cliente;
    private boolean saved = false;

    private MaskFormatter cpfMask;
    private MaskFormatter cnpjMask;

    public ClienteDialog(Frame owner, Cliente cliente) {
        super(owner, cliente == null ? "Novo Cliente" : "Editar Cliente", true);
        this.cliente = cliente;

        setSize(500, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        try {
            cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            cnpjMask = new MaskFormatter("##.###.###/####-##");
            cnpjMask.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao criar máscaras de CPF/CNPJ.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; txtId = new JTextField(); txtId.setEditable(false); formPanel.add(txtId, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; formPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; txtNomeCompleto = new JTextField(); formPanel.add(txtNomeCompleto, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; formPanel.add(new JLabel("Tipo Pessoa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; cmbTipoPessoa = new JComboBox<>(new String[]{"FISICA", "JURIDICA"});
        formPanel.add(cmbTipoPessoa, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; formPanel.add(new JLabel("CPF/CNPJ:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCpfCnpj = new JFormattedTextField();
        formPanel.add(txtCpfCnpj, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; txtEmail = new JTextField(); formPanel.add(txtEmail, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; formPanel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; txtTelefone = new JTextField(); formPanel.add(txtTelefone, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; formPanel.add(new JLabel("Data Nascimento:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnDataNascimento = new JSpinner(dateModel);
        spnDataNascimento.setEditor(new JSpinner.DateEditor(spnDataNascimento, "dd/MM/yyyy"));
        formPanel.add(spnDataNascimento, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; formPanel.add(new JLabel("Limite Crédito:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; txtLimiteCredito = new JTextField(); formPanel.add(txtLimiteCredito, gbc);
        row++;

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        cmbTipoPessoa.addActionListener(e -> aplicarMascaraCpfCnpj());

        if (this.cliente != null) {
            preencherFormulario(this.cliente);
        } else {
            aplicarMascaraCpfCnpj();
        }

        btnSalvar.addActionListener(e -> salvarCliente());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void aplicarMascaraCpfCnpj() {
        String tipoPessoa = (String) cmbTipoPessoa.getSelectedItem();
        try {
            if ("FISICA".equals(tipoPessoa)) {
                txtCpfCnpj.setFormatterFactory(new DefaultFormatterFactory(cpfMask));
                txtCpfCnpj.setToolTipText("Formato: XXX.XXX.XXX-XX");
            } else if ("JURIDICA".equals(tipoPessoa)) {
                txtCpfCnpj.setFormatterFactory(new DefaultFormatterFactory(cnpjMask));
                txtCpfCnpj.setToolTipText("Formato: XX.XXX.XXX/XXXX-XX");
            }
            txtCpfCnpj.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao aplicar máscara: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormulario(Cliente c) {
        txtId.setText(String.valueOf(c.getId()));
        txtNomeCompleto.setText(c.getNomeCompleto());

        cmbTipoPessoa.setSelectedItem(c.getTipoPessoa());

        if (c.getCpfCnpj() != null && !c.getCpfCnpj().isEmpty()) {
            txtCpfCnpj.setText(c.getCpfCnpj());
        } else {
            txtCpfCnpj.setText("");
        }

        txtEmail.setText(c.getEmail());
        txtTelefone.setText(c.getTelefone());
        Optional.ofNullable(c.getDataNascimento()).ifPresent(data ->
                spnDataNascimento.setValue(Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant()))
        );
        txtLimiteCredito.setText(Optional.ofNullable(c.getLimiteCredito()).map(BigDecimal::toString).orElse(""));
    }

    private void salvarCliente() {
        String cpfCnpjRaw = txtCpfCnpj.getText().replaceAll("[^0-9]", "");

        if (txtNomeCompleto.getText().isBlank() || cpfCnpjRaw.isBlank()) {
            JOptionPane.showMessageDialog(this, "Nome Completo e CPF/CNPJ são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipoPessoa = (String) cmbTipoPessoa.getSelectedItem();
        if ("FISICA".equals(tipoPessoa) && cpfCnpjRaw.length() != 11) {
            JOptionPane.showMessageDialog(this, "CPF inválido. Deve conter 11 dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if ("JURIDICA".equals(tipoPessoa) && cpfCnpjRaw.length() != 14) {
            JOptionPane.showMessageDialog(this, "CNPJ inválido. Deve conter 14 dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            final String nomeCompleto = txtNomeCompleto.getText();
            final String cpfCnpj = cpfCnpjRaw;
            final String email = txtEmail.getText();
            final String telefone = txtTelefone.getText();

            final Date dataSelecionada = (Date) spnDataNascimento.getValue();
            final LocalDate dataNascimento = dataSelecionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            BigDecimal tempLimiteCredito = BigDecimal.ZERO;
            if (!txtLimiteCredito.getText().isBlank()) {
                tempLimiteCredito = new BigDecimal(txtLimiteCredito.getText().replace(",", "."));
            }
            final BigDecimal limiteCredito = tempLimiteCredito;

            final boolean isNew = (this.cliente == null);

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (isNew) {
                        return HttpClient.getInstance().criarCliente(nomeCompleto, cpfCnpj, tipoPessoa, email, telefone, dataNascimento, limiteCredito);
                    } else {
                        return HttpClient.getInstance().atualizarCliente(cliente.getId(), nomeCompleto, cpfCnpj, tipoPessoa, email, telefone, dataNascimento, limiteCredito);
                    }
                }

                @Override
                protected void done() {
                    try {
                        boolean sucesso = get();
                        if (sucesso) {
                            JOptionPane.showMessageDialog(ClienteDialog.this, "Cliente salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            saved = true;
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(ClienteDialog.this, "Falha ao salvar o cliente. Verifique os dados ou o log do servidor.", "Erro no Servidor", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ClienteDialog.this, "Falha na comunicação com o servidor: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Dados inválidos. Verifique o formato dos campos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}