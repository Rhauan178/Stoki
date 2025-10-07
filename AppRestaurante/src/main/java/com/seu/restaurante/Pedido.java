package com.seu.restaurante;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

enum StatusPedido {
    ABERTO,
    ENVIADO,
    PRONTO,
    PAGO,
    ARQUIVADO
}

public class Pedido {

    private int id;
    private int idMesa;
    private StatusPedido status;
    private LocalDateTime dataHora;
    private Map<ItemCardapio, Integer> itens;
    private String observacao;

    public Pedido(int id, int idMesa) {
        this.id = id;
        this.idMesa = idMesa;
        this.status = StatusPedido.ABERTO;
        this.dataHora = LocalDateTime.now();
        this.itens = new HashMap<>();
        this.observacao = "";
    }

    public void adicionarItem(ItemCardapio item) {
        this.itens.put(item, this.itens.getOrDefault(item, 0) + 1);
    }

    public void removerItem(ItemCardapio item) {
        if (this.itens.containsKey(item)) {
            int quantidadeAtual = this.itens.get(item);
            if (quantidadeAtual > 1) {
                this.itens.put(item, quantidadeAtual - 1);
            } else {
                this.itens.remove(item);
            }
        }
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<ItemCardapio, Integer> entry : itens.entrySet()) {
            ItemCardapio item = entry.getKey();
            Integer quantidade = entry.getValue();
            total = total.add(item.getPreco().multiply(new BigDecimal(quantidade)));
        }
        return total;
    }

    // Getters e Setters

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public int getIdMesa() {
        return idMesa;
    }
    public StatusPedido getStatus() {
        return status;
    }
    public void setStatus(StatusPedido status) {
        this.status = status;
    }
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    public Map<ItemCardapio, Integer> getItens() {
        return itens;
    }
    public String getObservacao() {
        return observacao;
    }
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}