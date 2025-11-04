package com.seu.restaurante;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

enum StatusPedido {
    ABERTO,
    ENVIADO,
    EM_PREPARO,
    PRONTO,
    PAGO,
    ARQUIVADO
}

public class Pedido {

    private int id;
    private int idMesa;
    private int idFuncionario;
    private StatusPedido status;
    private LocalDateTime dataHora;
    private Map<ItemCardapio, Integer> itens;
    private String observacao;
    private Integer idContaPaga;

    public Pedido(int id, int idMesa, int idFuncionario) {
        this.id = id;
        this.idMesa = idMesa;
        this.idFuncionario = idFuncionario;
        this.status = StatusPedido.ABERTO;
        this.dataHora = LocalDateTime.now();
        this.itens = new HashMap<>();
        this.observacao = "";
        this.idContaPaga = null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdMesa() { return idMesa; }
    public int getIdFuncionario() { return idFuncionario; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public Map<ItemCardapio, Integer> getItens() { return itens; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Integer getIdContaPaga() { return idContaPaga; }
    public void setIdContaPaga(Integer idContaPaga) { this.idContaPaga = idContaPaga; }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return id == pedido.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}