package com.br.view;

import com.br.util.HttpClient;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnRegistrar; // Renomeado para mais clareza

    public LoginView() {
        // Configurações básicas da janela
        setTitle("Login - PDV Posto de Combustível");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centralizar na tela
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento

        // Componentes da tela
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Email:"), gbc); // CORRIGIDO

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUsuario = new JTextField(15);
        add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSenha = new JPasswordField(15);
        add(txtSenha, gbc);

        // Painel para os botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnEntrar = new JButton("Entrar");
        btnRegistrar = new JButton("Registrar-se");
        buttonPanel.add(btnEntrar);
        buttonPanel.add(btnRegistrar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Ação dos botões
        btnEntrar.addActionListener(e -> realizarLogin());

        btnRegistrar.addActionListener(e -> abrirTelaDeRegistro());
    }

    private void abrirTelaDeRegistro() {
        RegistroDialog dialog = new RegistroDialog(this);
        dialog.setVisible(true);
        // Opcional: se o registro for bem-sucedido, você pode, por exemplo,
        // preencher o campo de email na tela de login.
        if (dialog.isSaved()) {
            // Lógica opcional após o registro
        }
    }

    private void realizarLogin() {
        String usuario = txtUsuario.getText();
        String senha = new String(txtSenha.getPassword());

        btnEntrar.setEnabled(false);
        btnEntrar.setText("Autenticando...");
        btnRegistrar.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return HttpClient.getInstance().autenticar(usuario, senha);
            }

            @Override
            protected void done() {
                try {
                    boolean sucesso = get();
                    if (sucesso) {
                        new TelaPrincipal().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginView.this, "Email ou senha inválidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(LoginView.this, "Falha na comunicação com o servidor.", "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnEntrar.setEnabled(true);
                    btnEntrar.setText("Entrar");
                    btnRegistrar.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}