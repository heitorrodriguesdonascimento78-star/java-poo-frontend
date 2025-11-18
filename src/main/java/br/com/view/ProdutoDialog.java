package com.br.pdvfrontend.view;

import com.br.pdvfrontend.util.HttpClient;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RegistroDialog extends JDialog {

    private JTextField txtNomeCompleto;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JComboBox<String> cmbTipoPessoa;
    private JFormattedTextField txtCpfCnpj;
    private JFormattedTextField txtDataNascimento;

    private JButton btnRegistrar;
    private JButton btnCancelar;

    private boolean saved = false;
    private MaskFormatter cpfMask;
    private MaskFormatter cnpjMask;
    private MaskFormatter dataMask;

    public RegistroDialog(Frame owner) {
        super(owner, "Registrar Novo Usuário", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        try {
            cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            cnpjMask = new MaskFormatter("##.###.###/####-##");
            cnpjMask.setPlaceholderCharacter('_');
            dataMask = new MaskFormatter("##/##/####");
            dataMask.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; txtNomeCompleto = new JTextField(); formPanel.add(txtNomeCompleto, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; txtEmail = new JTextField(); formPanel.add(txtEmail, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; txtSenha = new JPasswordField(); formPanel.add(txtSenha, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Tipo Pessoa:"), gbc);
        gbc.gridx = 1; cmbTipoPessoa = new JComboBox<>(new String[]{"FISICA", "JURIDICA"}); formPanel.add(cmbTipoPessoa, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("CPF/CNPJ:"), gbc);
        gbc.gridx = 1; txtCpfCnpj = new JFormattedTextField(); formPanel.add(txtCpfCnpj, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Data Nascimento:"), gbc);
        gbc.gridx = 1;
        txtDataNascimento = new JFormattedTextField(new DefaultFormatterFactory(dataMask));
        formPanel.add(txtDataNascimento, gbc);
        row++;

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnRegistrar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        cmbTipoPessoa.addActionListener(e -> aplicarMascaraCpfCnpj());
        aplicarMascaraCpfCnpj();

        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void aplicarMascaraCpfCnpj() {
        String tipo = (String) cmbTipoPessoa.getSelectedItem();
        MaskFormatter mask = "FISICA".equals(tipo) ? cpfMask : cnpjMask;
        txtCpfCnpj.setFormatterFactory(new DefaultFormatterFactory(mask));
        txtCpfCnpj.setText("");
    }

    private void registrar() {
        // Validação da Data de Nascimento
        LocalDate dataNascimento;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            dataNascimento = LocalDate.parse(txtDataNascimento.getText(), dateFormatter);
            LocalDate hoje = LocalDate.now();
            LocalDate dataMinima = hoje.minusYears(130);

            if (dataNascimento.isAfter(hoje)) {
                JOptionPane.showMessageDialog(this, "A data de nascimento não pode ser no futuro.", "Data Inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dataNascimento.isBefore(dataMinima)) {
                JOptionPane.showMessageDialog(this, "A idade não pode ser superior a 130 anos.", "Data Inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy.", "Data Inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nome = txtNomeCompleto.getText();
        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());
        String tipoPessoa = (String) cmbTipoPessoa.getSelectedItem();
        String cpfCnpj = txtCpfCnpj.getText().replaceAll("[^0-9]", "");
        String dataNascimentoStr = dataNascimento.format(DateTimeFormatter.ISO_LOCAL_DATE); // Formato YYYY-MM-DD

        if (nome.isBlank() || email.isBlank() || senha.isBlank() || cpfCnpj.isBlank()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("Registrando...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return HttpClient.getInstance().registrarNovoUsuario(nome, email, senha, cpfCnpj, dataNascimentoStr, tipoPessoa);
            }

            @Override
            protected void done() {
                try {
                    boolean sucesso = get();
                    if (sucesso) {
                        JOptionPane.showMessageDialog(RegistroDialog.this, "Usuário registrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        saved = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegistroDialog.this, "Falha ao registrar. Verifique se o email ou CPF/CNPJ já existem.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(RegistroDialog.this, "Erro de comunicação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnRegistrar.setEnabled(true);
                    btnRegistrar.setText("Registrar");
                }
            }
        };
        worker.execute();
    }

    public boolean isSaved() {
        return saved;
    }
}