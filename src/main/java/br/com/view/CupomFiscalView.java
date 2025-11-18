package com.br.pdvfrontend.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CupomFiscalView extends JDialog {

    private JTextArea txtCupom;
    private JButton btnImprimir;
    private JButton btnFechar;

    public CupomFiscalView() {
        setTitle("Emissão de Cupom Fiscal");
        setSize(400, 550);
        setLocationRelativeTo(null); // Centraliza em relação à janela pai
        setLayout(new BorderLayout(10, 10));
        setModal(true); // Bloqueia a janela principal enquanto esta estiver aberta

        // --- ÁREA DE TEXTO DO CUPOM ---
        txtCupom = new JTextArea();
        txtCupom.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtCupom.setEditable(false);
        txtCupom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gerarConteudoCupom(); // Preenche com dados de exemplo
        add(new JScrollPane(txtCupom), BorderLayout.CENTER);

        // --- PAINEL DE BOTÕES ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnImprimir = new JButton("Imprimir");
        btnFechar = new JButton("Fechar");
        bottomPanel.add(btnImprimir);
        bottomPanel.add(btnFechar);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- AÇÕES DOS BOTÕES ---
        btnImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica de impressão (aqui apenas simulada)
                JOptionPane.showMessageDialog(CupomFiscalView.this,
                        "Cupom enviado para a impressora!",
                        "Impressão",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a janela de diálogo
            }
        });
    }

    private void gerarConteudoCupom() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime agora = LocalDateTime.now();

        sb.append("----------------------------------------\n");
        sb.append("         POSTO DE COMBUSTÍVEL XYZ         \n");
        sb.append("      Avenida Brasil, 1234 - Centro     \n");
        sb.append("         CNPJ: 12.345.678/0001-99         \n");
        sb.append("----------------------------------------\n");
        sb.append("             CUPOM FISCAL               \n");
        sb.append("----------------------------------------\n");
        sb.append("DATA: ").append(dtf.format(agora)).append("\n");
        sb.append("COO: 012345              CCF: 009876\n");
        sb.append("----------------------------------------\n");
        sb.append("ITEM  DESCRIÇÃO      QTD   VL.UNIT   TOTAL\n");
        sb.append("----------------------------------------\n");
        sb.append("001   Gasolina Comum 30.5L R$ 5,89  R$ 179,65\n");
        sb.append("\n");
        sb.append("----------------------------------------\n");
        sb.append("TOTAL R$                           179,65\n");
        sb.append("Dinheiro                           200,00\n");
        sb.append("Troco R$                            20,35\n");
        sb.append("----------------------------------------\n");
        sb.append("Obrigado pela preferência!\n");
        sb.append("Volte Sempre!\n");

        txtCupom.setText(sb.toString());
    }

    public static void main(String[] args) {
        // Apenas para testar a aparência desta janela isoladamente
        SwingUtilities.invokeLater(() -> {
            new CupomFiscalView().setVisible(true);
        });
    }
}