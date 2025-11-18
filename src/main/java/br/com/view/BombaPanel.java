package com.br.pdvfrontend.view;

import com.br.pdvfrontend.enums.StatusBomba; // Importar o enum
import com.br.pdvfrontend.model.Bomba;
import com.br.pdvfrontend.model.Produto;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class BombaPanel extends JPanel {

    private JLabel lblCombustivel;
    private JLabel lblStatus;
    private JLabel lblTanqueId;
    private JLabel lblBicoInfo;

    public BombaPanel(Bomba bomba, Produto combustivelProduto) {
        // Layout com 3 linhas: Bico/Combustível, Tanque, Status
        setLayout(new GridLayout(3, 1));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Bomba " + String.format("%02d", bomba.getNumeroBombaFisica()), // <-- USANDO numeroBombaFisica
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        setPreferredSize(new Dimension(180, 120)); // Ajusta a altura

        lblBicoInfo = new JLabel("Bico " + bomba.getNumeroBico() + ": " + bomba.getNomeCombustivel()); // <-- NOVO LABEL
        lblBicoInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBicoInfo.setHorizontalAlignment(SwingConstants.CENTER);

        lblTanqueId = new JLabel("Tanque: " + (bomba.getTanqueId() != null ? bomba.getTanqueId() : "N/A"));
        lblTanqueId.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTanqueId.setHorizontalAlignment(SwingConstants.CENTER);

        lblStatus = new JLabel(bomba.getStatus().name());
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

        add(lblBicoInfo);
        add(lblTanqueId);
        add(lblStatus);

        updateStatusColor(bomba.getStatus());
    }

    private void updateStatusColor(StatusBomba status) {
        Color backgroundColor;

        switch (status) {
            case ATIVA:
                backgroundColor = new Color(204, 255, 204); // Verde claro
                break;
            case ABASTECENDO:
                backgroundColor = new Color(255, 255, 204); // Amarelo claro
                break;
            case INATIVA:
            case MANUTENCAO:
                backgroundColor = new Color(255, 204, 204); // Vermelho claro
                break;
            default:
                backgroundColor = Color.LIGHT_GRAY;
                break;
        }
        setBackground(backgroundColor);
        for (Component c : getComponents()) {
            c.setBackground(backgroundColor);
        }
    }
}