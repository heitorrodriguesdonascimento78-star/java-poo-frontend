package com.br.model;

import java.math.BigDecimal;
import java.util.List;

public class ContaCliente {
    private Long id;
    private Long clienteId; // Apenas o ID do cliente
    private BigDecimal saldo;
    private List<MovimentoContaCliente> historicoTransacoes; // Pode ser útil para desserialização

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public List<MovimentoContaCliente> getHistoricoTransacoes() {
        return historicoTransacoes;
    }

    public void setHistoricoTransacoes(List<MovimentoContaCliente> historicoTransacoes) {
        this.historicoTransacoes = historicoTransacoes;
    }
}