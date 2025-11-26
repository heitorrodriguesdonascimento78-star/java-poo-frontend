package com.br.model;

import com.br.enums.StatusCaixa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Caixa {
    private Long id;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private Long usuarioAberturaId;
    private String usuarioAberturaNome;
    private Long usuarioFechamentoId;
    private String usuarioFechamentoNome;
    private StatusCaixa status;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Long getUsuarioAberturaId() {
        return usuarioAberturaId;
    }

    public void setUsuarioAberturaId(Long usuarioAberturaId) {
        this.usuarioAberturaId = usuarioAberturaId;
    }

    public String getUsuarioAberturaNome() {
        return usuarioAberturaNome;
    }

    public void setUsuarioAberturaNome(String usuarioAberturaNome) {
        this.usuarioAberturaNome = usuarioAberturaNome;
    }

    public Long getUsuarioFechamentoId() {
        return usuarioFechamentoId;
    }

    public void setUsuarioFechamentoId(Long usuarioFechamentoId) {
        this.usuarioFechamentoId = usuarioFechamentoId;
    }

    public String getUsuarioFechamentoNome() {
        return usuarioFechamentoNome;
    }

    public void setUsuarioFechamentoNome(String usuarioFechamentoNome) {
        this.usuarioFechamentoNome = usuarioFechamentoNome;
    }

    public StatusCaixa getStatus() {
        return status;
    }

    public void setStatus(StatusCaixa status) {
        this.status = status;
    }
}