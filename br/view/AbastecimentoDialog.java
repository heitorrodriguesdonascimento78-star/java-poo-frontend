package com.br.view;

import com.br.model.Bomba;

import javax.swing.*;
import java.awt.*;

public class AbastecimentoDialog extends JDialog {

    private Bomba bomba;

    public AbastecimentoDialog(Frame owner, Bomba bomba) {
        super(owner, "Detalhes do Abastecimento - Bomba " + bomba.getNumeroBombaFisica() + " Bico " + bomba.getNumeroBico(), true);
        this.bomba = bomba;
        initComponents();
    }

    private void initComponents() {
        // Configurações básicas do diálogo
        setSize(400, 300);
        setLocationRelativeTo(getOwner()); // Centraliza em relação ao owner
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Exemplo de conteúdo (você pode expandir isso)
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Abastecimento para Bomba: " + bomba.getNumeroBombaFisica() + ", Bico: " + bomba.getNumeroBico());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(label, BorderLayout.CENTER);

        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel);
    }
}