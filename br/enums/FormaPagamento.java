package com.br.enums;

public enum FormaPagamento {
    DINHEIRO,
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    PIX,
    CHEQUE, // Manter CHEQUE se for usado em algum lugar, caso contrário, remover.
    CONTA_CLIENTE // Alterado de CREDITO_CLIENTE para CONTA_CLIENTE
}