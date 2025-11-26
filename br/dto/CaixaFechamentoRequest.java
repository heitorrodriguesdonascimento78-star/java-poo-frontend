package com.br.dto;

import java.math.BigDecimal;

public class CaixaFechamentoRequest {
    private Long usuarioFechamentoId;
    private BigDecimal valorFinal;

    public CaixaFechamentoRequest(Long usuarioFechamentoId, BigDecimal valorFinal) {
        this.usuarioFechamentoId = usuarioFechamentoId;
        this.valorFinal = valorFinal;
    }

    public Long getUsuarioFechamentoId() {
        return usuarioFechamentoId;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }
}