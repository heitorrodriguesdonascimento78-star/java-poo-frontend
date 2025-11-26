package com.br.dto;

import java.math.BigDecimal;

public class CaixaAberturaRequest {
    private Long usuarioAberturaId;
    private BigDecimal valorInicial;

    public CaixaAberturaRequest(Long usuarioAberturaId, BigDecimal valorInicial) {
        this.usuarioAberturaId = usuarioAberturaId;
        this.valorInicial = valorInicial;
    }

    public Long getUsuarioAberturaId() {
        return usuarioAberturaId;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }
}