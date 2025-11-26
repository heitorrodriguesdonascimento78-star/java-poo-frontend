package com.br.model;

import java.math.BigDecimal;

public class ItemVenda {
    private Long id;
    private Long produtoId;
    private String nomeProduto; // Adicionado para exibir na tela
    private Double quantidade; // Alterado de Integer para Double
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    // Construtor vazio
    public ItemVenda() {
    }

    // Construtor com todos os campos
    public ItemVenda(Long id, Long produtoId, String nomeProduto, Double quantidade, BigDecimal precoUnitario, BigDecimal subtotal) {
        this.id = id;
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Double getQuantidade() { // Alterado de Integer para Double
        return quantidade;
    }

    public void setQuantidade(Double quantidade) { // Alterado de Integer para Double
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}