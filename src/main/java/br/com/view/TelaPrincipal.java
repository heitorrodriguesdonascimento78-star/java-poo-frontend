package com.br.pdvfrontend.view;

import com.br.pdvfrontend.util.HttpClient;
import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private MenuPanel menuPanel;

    public static final String ABASTECIMENTO = "Abastecimento";
    public static final String VENDAS = "Vendas";
    public static final String CLIENTES = "Clientes";
    public static final String CONTAS = "Contas";
    public static final String PRODUTOS = "Produtos";
    public static final String RELATORIOS = "Relatórios";
    public static final String CAIXA = "Caixa"; // <-- NOVO

    public TelaPrincipal() {
        setTitle("PDV - Posto de Combustível");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        menuPanel = new MenuPanel();
        add(menuPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new TelaAbastecimento(), ABASTECIMENTO);
        contentPanel.add(new TelaVenda(), VENDAS);
        contentPanel.add(new TelaCliente(), CLIENTES);
        contentPanel.add(new TelaContaCliente(), CONTAS);
        contentPanel.add(new TelaProduto(), PRODUTOS);
        contentPanel.add(new TelaCaixa(), CAIXA); // <-- NOVO
        contentPanel.add(new TelaRelatorios(), RELATORIOS); // Substituído o placeholder pela TelaRelatorios

        add(contentPanel, BorderLayout.CENTER);

        setupMenuActions();

        cardLayout.show(contentPanel, ABASTECIMENTO);
    }

    private void setupMenuActions() {
        menuPanel.btnAbastecimento.addActionListener(e -> cardLayout.show(contentPanel, ABASTECIMENTO));
        menuPanel.btnVendas.addActionListener(e -> cardLayout.show(contentPanel, VENDAS));
        menuPanel.btnClientes.addActionListener(e -> cardLayout.show(contentPanel, CLIENTES));
        menuPanel.btnContas.addActionListener(e -> cardLayout.show(contentPanel, CONTAS));
        menuPanel.btnProdutos.addActionListener(e -> cardLayout.show(contentPanel, PRODUTOS));
        menuPanel.btnRelatorios.addActionListener(e -> cardLayout.show(contentPanel, RELATORIOS));

        // NOVO BOTÃO PARA CAIXA
        menuPanel.btnCaixa.addActionListener(e -> cardLayout.show(contentPanel, CAIXA));

        menuPanel.btnSair.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?", "Sair", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginView().setVisible(true);
                dispose();
            }
        });
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("Tela de " + text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(label);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}