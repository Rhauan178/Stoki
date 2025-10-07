package com.seu.restaurante;

public class Ingrediente {
    private int id;
    private String nome;
    private double estoqueAtual;
    private String unidadeMedida; // Ex: "kg", "unidade", "litro"
    private double estoqueMinimo;

    // Construtor para criar um ingrediente (sem ‘ID’)
    public Ingrediente(String nome, double estoqueAtual, String unidadeMedida, double estoqueMinimo) {
        this.nome = nome;
        this.estoqueAtual = estoqueAtual;
        this.unidadeMedida = unidadeMedida;
        this.estoqueMinimo = estoqueMinimo;
    }

    // Construtor para carregar um ingrediente do banco de dados (com ID)
    public Ingrediente(int id, String nome, double estoqueAtual, String unidadeMedida, double estoqueMinimo) {
        this.id = id;
        this.nome = nome;
        this.estoqueAtual = estoqueAtual;
        this.unidadeMedida = unidadeMedida;
        this.estoqueMinimo = estoqueMinimo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public double getEstoqueAtual() {
        return estoqueAtual;
    }
    public void setEstoqueAtual(double estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }
    public String getUnidadeMedida() {
        return unidadeMedida;
    }
    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
    public double getEstoqueMinimo() {
        return estoqueMinimo;
    }
    public void setEstoqueMinimo(double estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }
    @Override
    public String toString() {
        return this.nome;
    }
}