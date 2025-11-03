package com.seu.restaurante;

enum StatusMesa {
    LIVRE,
    OCUPADA,
    RESERVADA
}

public class Mesa {

    private int numero;
    private StatusMesa status;
    private Pedido pedidoAtual;

    public Mesa(int numero, StatusMesa status) {
        this.numero = numero;
        this.status = status;
        this.pedidoAtual = null;
    }

    public int getNumero() {
        return numero;
    }

    public StatusMesa getStatus() {
        return status;
    }

    public void setStatus(StatusMesa status) {
        this.status = status;
    }

    public Pedido getPedidoAtual() {
        return pedidoAtual;
    }

    public void setPedidoAtual(Pedido pedidoAtual) {
        this.pedidoAtual = pedidoAtual;
    }
}