package com.seu.restaurante;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Conta {

    private int id;
    private int idMesa;
    private int idFuncionario;
    private BigDecimal valorTotal;
    private MetodoPagamento metodoPagamento;
    private LocalDateTime dataHora;

    public Conta(int idMesa, int idFuncionario, BigDecimal valorTotal, MetodoPagamento metodoPagamento, LocalDateTime dataHora) {
        this.idMesa = idMesa;
        this.idFuncionario = idFuncionario;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodoPagamento;
        this.dataHora = dataHora;
    }

    public Conta(int id, int idMesa, int idFuncionario, BigDecimal valorTotal, MetodoPagamento metodoPagamento, LocalDateTime dataHora) {
        this.id = id;
        this.idMesa = idMesa;
        this.idFuncionario = idFuncionario;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodoPagamento;
        this.dataHora = dataHora;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdMesa() { return idMesa; }
    public int getIdFuncionario() { return idFuncionario; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public LocalDateTime getDataHora() { return dataHora; }
}