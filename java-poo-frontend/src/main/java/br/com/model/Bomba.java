package com.br.pdvfrontend.model;

import com.br.pdvfrontend.enums.StatusBomba; // Importar o enum

public class Bomba {
    private Long id;
    private Integer numeroBombaFisica; // Novo campo
    private Integer numeroBico;        // Renomeado de 'numero'
    private Long tanqueId;
    private Long combustivelId;
    private String nomeCombustivel;
    private StatusBomba status;        // Usando o enum StatusBomba

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroBombaFisica() {
        return numeroBombaFisica;
    }

    public void setNumeroBombaFisica(Integer numeroBombaFisica) {
        this.numeroBombaFisica = numeroBombaFisica;
    }

    public Integer getNumeroBico() {
        return numeroBico;
    }

    public void setNumeroBico(Integer numeroBico) {
        this.numeroBico = numeroBico;
    }

    public Long getTanqueId() {
        return tanqueId;
    }

    public void setTanqueId(Long tanqueId) {
        this.tanqueId = tanqueId;
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

    public StatusBomba getStatus() {
        return status;
    }

    public void setStatus(StatusBomba status) {
        this.status = status;
    }
