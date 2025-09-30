package com.seu.restaurante;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

enum StatusPedido {
    ABERTO,
    ENVIADO,
    PRONTO,
    PAGO
}

public class Pedido {

    private int id;
    private int idMesa;
    private StatusPedido status;
    private LocalDateTime dataHora;
    // MUDANÇA PRINCIPAL: Trocamos a List<ItemCardapio> por um Map
    private Map<ItemCardapio, Integer> itens;

    public Pedido(int id, int idMesa) {
        this.id = id;
        this.idMesa = idMesa;
        this.status = StatusPedido.ABERTO;
        this.dataHora = LocalDateTime.now();
        this.itens = new HashMap<>(); // Inicializa com um HashMap
    }

    // --- MÉTODOS DE NEGÓCIO ATUALIZADOS ---

    // A lógica de adicionar agora é mais inteligente
    public void adicionarItem(ItemCardapio item) {
        // Se o ‘item’ já existe no mapa, apenas incrementa a quantidade. Senão, adiciona com quantidade 1.
        this.itens.put(item, this.itens.getOrDefault(item, 0) + 1);
    }

    // Novo método para remover ou decrementar itens
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

    // O cálculo do valor total agora multiplica pela quantidade
    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<ItemCardapio, Integer> entry : itens.entrySet()) {
            ItemCardapio item = entry.getKey();
            Integer quantidade = entry.getValue();
            total = total.add(item.getPreco().multiply(new BigDecimal(quantidade)));
        }
        return total;
    }

    // --- Getters e Setters ---

    public void setId(int id) {
        this.id = id;
    }
    public int getId() { return id; }
    public int getIdMesa() { return idMesa; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    // O getter de itens agora retorna o Map
    public Map<ItemCardapio, Integer> getItens() {
        return itens;
    }
}