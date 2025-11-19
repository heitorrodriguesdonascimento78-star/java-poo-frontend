package com.br.pdvfrontend.enums;

public enum TipoMovimento {
    CREDITO, // Pagamento recebido, aumenta o saldo do cliente (diminui a dívida)
    DEBITO   // Nova dívida, diminui o saldo do cliente (aumenta a dívida)
}