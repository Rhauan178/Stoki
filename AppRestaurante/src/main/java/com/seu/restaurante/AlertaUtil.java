package com.seu.restaurante;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class AlertaUtil {

    // Construtor privado para impedir a criação de instâncias desta classe
    private AlertaUtil() {}

    /**
     * Mostra um alerta de informação simples.
     * @param titulo O título da janela de alerta.
     * @param mensagem A mensagem a ser exibida.
     */
    public static void mostrarInformacao(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null); // Sem cabeçalho
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Mostra um alerta de erro.
     * @param titulo O título da janela de alerta.
     * @param mensagem A mensagem de erro a ser exibida.
     */
    public static void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Mostra um alerta de confirmação e retorna a resposta do utilizador.
     * @param titulo O título da janela de confirmação.
     * @param mensagem A pergunta de confirmação.
     * @return true se o utilizador clicar em OK, false caso contrário.
     */
    public static boolean mostrarConfirmacao(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}