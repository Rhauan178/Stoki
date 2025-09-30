package com.seu.restaurante;

// (O enum StatusMesa continua aqui, sem alterações)
enum StatusMesa {
    LIVRE,
    OCUPADA,
    RESERVADA
}

public class Mesa {

    private int numero;
    private StatusMesa status;
    private Pedido pedidoAtual; // NOVA LINHA: A mesa agora tem um pedido

    public Mesa(int numero, StatusMesa status) {
        this.numero = numero;
        this.status = status;
        this.pedidoAtual = null; // Uma mesa começa sem nenhum pedido ativo
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

    // --- NOVOS MÉTODOS para gerir o pedido da mesa ---
    public Pedido getPedidoAtual() {
        return pedidoAtual;
    }

    public void setPedidoAtual(Pedido pedidoAtual) {
        this.pedidoAtual = pedidoAtual;
    }
}