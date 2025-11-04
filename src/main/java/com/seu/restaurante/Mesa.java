package com.seu.restaurante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

enum StatusMesa {
    LIVRE,
    OCUPADA,
    RESERVADA
}

public class Mesa {

    private int numero;
    private StatusMesa status;
    private List<Pedido> pedidosDaMesa;

    public Mesa(int numero, StatusMesa status) {
        this.numero = numero;
        this.status = status;
        this.pedidosDaMesa = new ArrayList<>();
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

    public void adicionarPedido(Pedido pedido) {
        this.pedidosDaMesa.add(pedido);
    }

    public List<Pedido> getPedidosDaMesa() {
        return pedidosDaMesa;
    }

    public List<Pedido> getPedidosAtivos() {
        return pedidosDaMesa.stream()
                .filter(p -> p.getStatus() != StatusPedido.PAGO && p.getStatus() != StatusPedido.ARQUIVADO)
                .collect(Collectors.toList());
    }

    public void limparPedidos() {
        this.pedidosDaMesa.clear();
    }

    public BigDecimal getValorTotalDaConta() {
        BigDecimal total = BigDecimal.ZERO;
        for (Pedido pedido : getPedidosAtivos()) {
            total = total.add(pedido.getValorTotal());
        }
        return total;
    }
}