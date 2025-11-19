package com.br.pdvfrontend.view;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    public JButton btnAbastecimento;
    public JButton btnVendas;
    public JButton btnClientes;
    public JButton btnContas;
    public JButton btnProdutos;
    public JButton btnRelatorios;
    public JButton btnCaixa; // <-- NOVO BOTÃO
    public JButton btnSair;

    public MenuPanel() {
        setLayout(new GridLayout(11, 1, 5, 5)); // Aumenta o número de linhas para acomodar o novo botão
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnAbastecimento = new JButton("Abastecimento");
        btnVendas = new JButton("Vendas");
        btnClientes = new JButton("Clientes");
        btnContas = new JButton("Contas");
        btnProdutos = new JButton("Produtos");
        btnRelatorios = new JButton("Relatórios");
        btnCaixa = new JButton("Caixa"); // <-- INSTANCIA O NOVO BOTÃO
        btnSair = new JButton("Sair");

        add(new JLabel("MENU", SwingConstants.CENTER));
        add(new JSeparator());
        add(btnAbastecimento);
        add(btnVendas);
        add(btnClientes);
        add(btnContas);
        add(btnProdutos);
        add(btnRelatorios);
        add(btnCaixa); // <-- ADICIONA O NOVO BOTÃO
        add(Box.createVerticalGlue());
        add(btnSair);
    }
}