package com.br.view;

import com.br.dto.CaixaAberturaRequest;
import com.br.dto.CaixaFechamentoRequest;
import com.br.enums.StatusCaixa;
import com.br.model.Caixa;
import com.br.util.HttpClient;
import com.br.util.HttpClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TelaCaixa extends JPanel {

    private JLabel lblStatusCaixa;
    private JLabel lblIdCaixa;
    private JLabel lblValorInicial;
    private JLabel lblDataAbertura;
    private JLabel lblUsuarioAbertura;
    private JLabel lblValorFinal;
    private JLabel lblDataFechamento;
    private JLabel lblUsuarioFechamento;

    private JTextField txtValorInicial;
    private JTextField txtUsuarioAberturaId;
    private JButton btnAbrirCaixa;

    private JTextField txtValorFinal;
    private JTextField txtUsuarioFechamentoId;
    private JButton btnFecharCaixa;

    private Caixa caixaAtual; // Armazena o estado do caixa

    private com.br.util.HttpClient HttpClient;

    public TelaCaixa() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- PAINEL DE STATUS DO CAIXA ---
        JPanel statusPanel = new JPanel(new GridBagLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status do Caixa"));
        GridBagConstraints gbcStatus = new GridBagConstraints();
        gbcStatus.insets = new Insets(5, 5, 5, 5);
        gbcStatus.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbcStatus.gridx = 0; gbcStatus.gridy = row; statusPanel.add(new JLabel("Status:"), gbcStatus);
        gbcStatus.gridx = 1; lblStatusCaixa = new JLabel("FECHADO"); lblStatusCaixa.setFont(new Font("Segoe UI", Font.BOLD, 14)); statusPanel.add(lblStatusCaixa, gbcStatus);
        row++;

        gbcStatus.gridx = 0; gbcStatus.gridy = row; statusPanel.add(new JLabel("ID do Caixa:"), gbcStatus);
        gbcStatus.gridx = 1; lblIdCaixa = new JLabel("N/A"); statusPanel.add(lblIdCaixa, gbcStatus);
        row++;

        gbcStatus.gridx = 0; gbcStatus.gridy = row; statusPanel.add(new JLabel("Valor Inicial:"), gbcStatus);
        gbcStatus.gridx = 1; lblValorInicial = new JLabel("R$ 0,00"); statusPanel.add(lblValorInicial, gbcStatus);
        row++;

        gbcStatus.gridx = 0; gbcStatus.gridy = row; statusPanel.add(new JLabel("Data Abertura:"), gbcStatus);
        gbcStatus.gridx = 1; lblDataAbertura = new JLabel("N/A"); statusPanel.add(lblDataAbertura, gbcStatus);
        row++;

        gbcStatus.gridx = 0; gbcStatus.gridy = row; statusPanel.add(new JLabel("Usuário Abertura:"), gbcStatus);
        gbcStatus.gridx = 1; lblUsuarioAbertura = new JLabel("N/A"); statusPanel.add(lblUsuarioAbertura, gbcStatus);
        row++;

        gbcStatus.gridx = 0; gbcStatus.gridy = row; statusPanel.add(new JLabel("Valor Final:"), gbcStatus);
        gbcStatus.gridx = 1; lblValorFinal = new JLabel("N/A"); statusPanel.add(lblValorFinal, gbcStatus);
        row++;

        gbcStatus.gridx = 0; gbcStatus.gridy = row; statusPanel.add(new JLabel("Data Fechamento:"), gbcStatus);
        gbcStatus.gridx = 1; lblDataFechamento = new JLabel("N/A"); statusPanel.add(lblDataFechamento, gbcStatus);
        row++;

        gbcStatus.gridx = 0; gbcStatus.gridy = row; statusPanel.add(new JLabel("Usuário Fechamento:"), gbcStatus);
        gbcStatus.gridx = 1; lblUsuarioFechamento = new JLabel("N/A"); statusPanel.add(lblUsuarioFechamento, gbcStatus);
        row++;

        add(statusPanel, BorderLayout.NORTH);

        // --- PAINEL DE AÇÕES (ABRIR/FECHAR) ---
        JPanel actionsPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // 1 linha, 2 colunas
        actionsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // PAINEL ABRIR CAIXA
        JPanel abrirCaixaPanel = new JPanel(new GridBagLayout());
        abrirCaixaPanel.setBorder(BorderFactory.createTitledBorder("Abrir Caixa"));
        GridBagConstraints gbcAbrir = new GridBagConstraints();
        gbcAbrir.insets = new Insets(5, 5, 5, 5);
        gbcAbrir.anchor = GridBagConstraints.WEST;
        gbcAbrir.fill = GridBagConstraints.HORIZONTAL;

        row = 0;
        gbcAbrir.gridx = 0; gbcAbrir.gridy = row; abrirCaixaPanel.add(new JLabel("Valor Inicial:"), gbcAbrir);
        gbcAbrir.gridx = 1; gbcAbrir.weightx = 1.0; txtValorInicial = new JTextField(10); abrirCaixaPanel.add(txtValorInicial, gbcAbrir);
        row++;

        gbcAbrir.gridx = 0; gbcAbrir.gridy = row; abrirCaixaPanel.add(new JLabel("ID Funcionário:"), gbcAbrir);
        gbcAbrir.gridx = 1; gbcAbrir.weightx = 1.0; txtUsuarioAberturaId = new JTextField(10); abrirCaixaPanel.add(txtUsuarioAberturaId, gbcAbrir);
        row++;

        gbcAbrir.gridx = 0; gbcAbrir.gridy = row; gbcAbrir.gridwidth = 2; gbcAbrir.anchor = GridBagConstraints.CENTER;
        btnAbrirCaixa = new JButton("Abrir Caixa");
        abrirCaixaPanel.add(btnAbrirCaixa, gbcAbrir);
        actionsPanel.add(abrirCaixaPanel);

        // PAINEL FECHAR CAIXA
        JPanel fecharCaixaPanel = new JPanel(new GridBagLayout());
        fecharCaixaPanel.setBorder(BorderFactory.createTitledBorder("Fechar Caixa"));
        GridBagConstraints gbcFechar = new GridBagConstraints();
        gbcFechar.insets = new Insets(5, 5, 5, 5);
        gbcFechar.anchor = GridBagConstraints.WEST;
        gbcFechar.fill = GridBagConstraints.HORIZONTAL;

        row = 0;
        gbcFechar.gridx = 0; gbcFechar.gridy = row; fecharCaixaPanel.add(new JLabel("Valor Final:"), gbcFechar);
        gbcFechar.gridx = 1; gbcFechar.weightx = 1.0; txtValorFinal = new JTextField(10); fecharCaixaPanel.add(txtValorFinal, gbcFechar);
        row++;

        gbcFechar.gridx = 0; gbcFechar.gridy = row; fecharCaixaPanel.add(new JLabel("ID Funcionário:"), gbcFechar);
        gbcFechar.gridx = 1; gbcFechar.weightx = 1.0; txtUsuarioFechamentoId = new JTextField(10); fecharCaixaPanel.add(txtUsuarioFechamentoId, gbcFechar);
        row++;

        gbcFechar.gridx = 0; gbcFechar.gridy = row; gbcFechar.gridwidth = 2; gbcFechar.anchor = GridBagConstraints.CENTER;
        btnFecharCaixa = new JButton("Fechar Caixa");
        fecharCaixaPanel.add(btnFecharCaixa, gbcFechar);
        actionsPanel.add(fecharCaixaPanel);

        add(actionsPanel, BorderLayout.CENTER);

        // --- LISTENERS ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                buscarCaixaAberto();
            }
        });

        btnAbrirCaixa.addActionListener(e -> abrirCaixa());
        btnFecharCaixa.addActionListener(e -> fecharCaixa());
    }

    private void buscarCaixaAberto() {
        SwingWorker<Caixa, Void> worker = new SwingWorker<>() {
            @Override
            protected Caixa doInBackground() throws Exception {
                return HttpClient.getInstance().buscarCaixaAberto();
            }

            @Override
            protected void done() {
                try {
                    caixaAtual = get();
                    atualizarUI();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TelaCaixa.this, "Falha ao buscar caixa aberto: " + e.getMessage(), "Erro de Rede", JOptionPane.ERROR_MESSAGE);
                    caixaAtual = null; // Garante que o estado seja nulo em caso de erro
                    atualizarUI();
                }
            }
        };
        worker.execute();
    }

    private void atualizarUI() {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        if (caixaAtual != null && caixaAtual.getStatus() == StatusCaixa.ABERTO) {
            lblStatusCaixa.setText("ABERTO");
            lblStatusCaixa.setForeground(new Color(0, 150, 0)); // Verde
            lblIdCaixa.setText(String.valueOf(caixaAtual.getId()));
            lblValorInicial.setText(currencyFormatter.format(caixaAtual.getValorInicial()));
            lblDataAbertura.setText(caixaAtual.getDataAbertura().format(dateTimeFormatter));
            lblUsuarioAbertura.setText(caixaAtual.getUsuarioAberturaNome());
            lblValorFinal.setText("N/A");
            lblDataFechamento.setText("N/A");
            lblUsuarioFechamento.setText("N/A");

            btnAbrirCaixa.setEnabled(false);
            txtValorInicial.setEditable(false);
            txtUsuarioAberturaId.setEditable(false);

            btnFecharCaixa.setEnabled(true);
            txtValorFinal.setEditable(true);
            txtUsuarioFechamentoId.setEditable(true);
        } else {
            lblStatusCaixa.setText("FECHADO");
            lblStatusCaixa.setForeground(new Color(150, 0, 0)); // Vermelho
            lblIdCaixa.setText("N/A");
            lblValorInicial.setText("R$ 0,00");
            lblDataAbertura.setText("N/A");
            lblUsuarioAbertura.setText("N/A");
            lblValorFinal.setText("N/A");
            lblDataFechamento.setText("N/A");
            lblUsuarioFechamento.setText("N/A");

            btnAbrirCaixa.setEnabled(true);
            txtValorInicial.setEditable(true);
            txtUsuarioAberturaId.setEditable(true);

            btnFecharCaixa.setEnabled(false);
            txtValorFinal.setEditable(false);
            txtUsuarioFechamentoId.setEditable(false);
        }
    }

    private void abrirCaixa() {
        try {
            Long usuarioId = Long.parseLong(txtUsuarioAberturaId.getText());
            BigDecimal valorInicial = new BigDecimal(txtValorInicial.getText().replace(",", "."));

            if (valorInicial.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Valor inicial não pode ser negativo.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnAbrirCaixa.setEnabled(false);
            btnAbrirCaixa.setText("Abrindo...");

            SwingWorker<Caixa, Void> worker = new SwingWorker<>() {

                @Override
                protected Caixa doInBackground() throws Exception {
                    return HttpClient.getInstance().abrirCaixa(new CaixaAberturaRequest(usuarioId, valorInicial));
                }

                @Override
                protected void done() {
                    try {
                        caixaAtual = get();
                        if (caixaAtual != null) {
                            JOptionPane.showMessageDialog(TelaCaixa.this, "Caixa aberto com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            txtValorInicial.setText("");
                            txtUsuarioAberturaId.setText("");
                        } else {
                            JOptionPane.showMessageDialog(TelaCaixa.this, "Falha ao abrir caixa.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(TelaCaixa.this, "Erro ao abrir caixa: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        atualizarUI();
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID do funcionário ou valor inicial inválidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fecharCaixa() {
        if (caixaAtual == null || caixaAtual.getId() == null) {
            JOptionPane.showMessageDialog(this, "Nenhum caixa aberto para fechar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Long usuarioId = Long.parseLong(txtUsuarioFechamentoId.getText());
            BigDecimal valorFinal = new BigDecimal(txtValorFinal.getText().replace(",", "."));

            btnFecharCaixa.setEnabled(false);
            btnFecharCaixa.setText("Fechando...");

            SwingWorker<Caixa, Void> worker = new SwingWorker<>() {
                @Override
                protected Caixa doInBackground() throws Exception {
                    return HttpClient.getInstance().fecharCaixa(caixaAtual.getId(), new CaixaFechamentoRequest(usuarioId, valorFinal));
                }

                @Override
                protected void done() {
                    try {
                        caixaAtual = get();
                        if (caixaAtual != null && caixaAtual.getStatus() == StatusCaixa.FECHADO) {
                            JOptionPane.showMessageDialog(TelaCaixa.this, "Caixa fechado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            txtValorFinal.setText("");
                            txtUsuarioFechamentoId.setText("");
                        } else {
                            JOptionPane.showMessageDialog(TelaCaixa.this, "Falha ao fechar caixa.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(TelaCaixa.this, "Erro ao fechar caixa: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        atualizarUI();
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID do funcionário ou valor final inválidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}