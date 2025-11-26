package com.br.model;

import com.br.enums.FormaPagamento;
import com.br.enums.StatusVenda;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Venda {
    private Long id;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonProperty("data")
    private LocalDateTime dataHora;
    private Long funcionarioId;
    private String nomeFuncionario; // Adicionado para exibir na tela
    private Long clienteId;
    private String nomeCliente; // Adicionado para exibir na tela
    private FormaPagamento formaPagamento;
    private BigDecimal valorTotal;
    private StatusVenda status;
    private List<ItemVenda> itens;

    // Construtor vazio
    public Venda() {
    }

    // Construtor com todos os campos
    public Venda(Long id, LocalDateTime dataHora, Long funcionarioId, String nomeFuncionario, Long clienteId, String nomeCliente, FormaPagamento formaPagamento, BigDecimal valorTotal, StatusVenda status, List<ItemVenda> itens) {
        this.id = id;
        this.dataHora = dataHora;
        this.funcionarioId = funcionarioId;
        this.nomeFuncionario = nomeFuncionario;
        this.clienteId = clienteId;
        this.nomeCliente = nomeCliente;
        this.formaPagamento = formaPagamento;
        this.valorTotal = valorTotal;
        this.status = status;
        this.itens = itens;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void setStatus(StatusVenda status) {
        this.status = status;
    }


    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }
}