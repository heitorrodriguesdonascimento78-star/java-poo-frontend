package com.br.model;

import java.time.LocalDateTime;

public class Tanque {
    private Long id;
    private Double capacidade;
    private Double nivelAtual;
    private Long combustivelId;      // <-- NOVO CAMPO ADICIONADO
    private String nomeCombustivel;  // <-- NOVO CAMPO ADICIONADO
    private LocalDateTime ultimaLeitura;
    private LocalDateTime dataUltimaEntrega;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Double capacidade) {
        this.capacidade = capacidade;
    }

    public Double getNivelAtual() {
        return nivelAtual;
    }

    public void setNivelAtual(Double nivelAtual) {
        this.nivelAtual = nivelAtual;
    }

    public Long getCombustivelId() {
        return combustivelId;
    }

    public void setCombustivelId(Long combustivelId) {
        this.combustivelId = combustivelId;
    }

    public String getNomeCombustivel() {
        return nomeCombustivel;
    }

    public void setNomeCombustivel(String nomeCombustivel) {
        this.nomeCombustivel = nomeCombustivel;
    }

    public LocalDateTime getUltimaLeitura() {
        return ultimaLeitura;
    }

    public void setUltimaLeitura(LocalDateTime ultimaLeitura) {
        this.ultimaLeitura = ultimaLeitura;
    }

    public LocalDateTime getDataUltimaEntrega() {
        return dataUltimaEntrega;
    }

    public void setDataUltimaEntrega(LocalDateTime dataUltimaEntrega) {
        this.dataUltimaEntrega = dataUltimaEntrega;
    }
}