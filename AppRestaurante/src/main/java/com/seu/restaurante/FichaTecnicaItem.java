package com.seu.restaurante;

import java.text.DecimalFormat;

public class FichaTecnicaItem {

    private Ingrediente ingrediente;
    private double quantidadeUsada;

    public FichaTecnicaItem(Ingrediente ingrediente, double quantidadeUsada) {
        this.ingrediente = ingrediente;
        this.quantidadeUsada = quantidadeUsada;
    }

    // Getters e Setters
    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public double getQuantidadeUsada() {
        return quantidadeUsada;
    }

    public void setQuantidadeUsada(double quantidadeUsada) {
        this.quantidadeUsada = quantidadeUsada;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.###");
        return String.format("%s: %s %s",
                ingrediente.getNome(),
                df.format(quantidadeUsada),
                ingrediente.getUnidadeMedida());
    }
}