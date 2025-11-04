package com.seu.restaurante;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

enum StatusConta {
    PAGA,
    ARQUIVADA
}

public class Conta {

    private int id;
    private int idMesa;
    private int idFuncionario;
    private BigDecimal valorTotal;
    private MetodoPagamento metodoPagamento;
    private LocalDateTime dataHora;
    private StatusConta status;

    public Conta(int idMesa, int idFuncionario, BigDecimal valorTotal, MetodoPagamento metodoPagamento, LocalDateTime dataHora) {
        this.idMesa = idMesa;
        this.idFuncionario = idFuncionario;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodoPagamento;
        this.dataHora = dataHora;
        this.status = StatusConta.PAGA;
    }

    public Conta(int id, int idMesa, int idFuncionario, BigDecimal valorTotal, MetodoPagamento metodoPagamento, LocalDateTime dataHora, StatusConta status) {
        this.id = id;
        this.idMesa = idMesa;
        this.idFuncionario = idFuncionario;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodoPagamento;
        this.dataHora = dataHora;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdMesa() { return idMesa; }
    public int getIdFuncionario() { return idFuncionario; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public LocalDateTime getDataHora() { return dataHora; }

    public StatusConta getStatus() { return status; }
    public void setStatus(StatusConta status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return id == conta.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}