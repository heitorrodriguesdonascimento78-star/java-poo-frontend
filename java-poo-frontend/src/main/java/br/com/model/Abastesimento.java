package com.br.pdvfrontend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Abastecimento {
    private Long id;
    private Long bombaId; // Apenas o ID para a requisição
    private Double litrosAbastecidos;
    private BigDecimal valorLitro;
    private BigDecimal valorTotal;
    private LocalDateTime dataHora;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBombaId() {
        return bombaId;
    }

    public void setBombaId(Long bombaId) {
        this.bombaId = bombaId;
    }

    public Double getLitrosAbastecidos() {
        return litrosAbastecidos;
    }

    public void setLitrosAbastecidos(Double litrosAbastecidos) {
        this.litrosAbastecidos = litrosAbastecidos;
    }

    public BigDecimal getValorLitro() {
        return valorLitro;
    }

    public void setValorLitro(BigDecimal valorLitro) {
        this.valorLitro = valorLitro;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}