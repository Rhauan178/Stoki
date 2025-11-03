package com.seu.restaurante;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.UUID;

public class PagamentoViewController {

    @FXML private Label labelTotalAPagar;
    @FXML private Button botaoDinheiro;
    @FXML private Button botaoCredito;
    @FXML private Button botaoDebito;
    @FXML private Button botaoPix;
    @FXML private Button botaoCancelar;
    @FXML private HBox painelMetodos;
    @FXML private VBox painelPix;
    @FXML private ImageView imageViewQrCode;
    @FXML private TextField campoPixCopiaECola;
    @FXML private Button botaoConfirmarPagamento;

    private Pedido pedido;
    private PedidoDAO pedidoDAO;
    private Runnable sucessoCallback;

    @FXML
    public void initialize() {
        botaoDinheiro.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.MONEY));
        botaoCredito.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CREDIT_CARD));
        botaoDebito.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CREDIT_CARD_ALT));
        botaoPix.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.QRCODE));
        botaoConfirmarPagamento.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
    }

    public void carregarDados(Pedido pedido, PedidoDAO pedidoDAO, Runnable sucessoCallback) {
        this.pedido = pedido;
        this.pedidoDAO = pedidoDAO;
        this.sucessoCallback = sucessoCallback;
        labelTotalAPagar.setText(String.format("Total a Pagar: R$ %.2f", pedido.getValorTotal()));
    }

    @FXML
    private void mostrarPainelPix() {
        String chavePixExemplo = UUID.randomUUID().toString();
        Image qrCodeImage = QRCodeUtil.gerarQRCodeImage(chavePixExemplo, 200, 200);

        if (qrCodeImage != null) {
            imageViewQrCode.setImage(qrCodeImage);
            campoPixCopiaECola.setText(chavePixExemplo);
            painelMetodos.setVisible(false);
            painelMetodos.setManaged(false);
            painelPix.setVisible(true);
            painelPix.setManaged(true);
        } else {
            AlertaUtil.mostrarErro("Erro de QR Code", "Não foi possível gerar a imagem do QR Code.");
        }
    }

    @FXML
    private void pagamentoPixConfirmado() {
        processarPagamento(MetodoPagamento.PIX);
    }
    @FXML private void pagarComDinheiro() {
        processarPagamento(MetodoPagamento.DINHEIRO);
    }
    @FXML private void pagarComCredito() {
        processarPagamento(MetodoPagamento.CARTAO_CREDITO);
    }
    @FXML private void pagarComDebito() {
        processarPagamento(MetodoPagamento.CARTAO_DEBITO);
    }

    private void processarPagamento(MetodoPagamento metodo) {
        if (pedido.getId() == 0) {
            pedidoDAO.salvarPedido(pedido);
        }
        pedidoDAO.marcarComoPago(pedido.getId(), metodo);

        if (sucessoCallback != null) {
            sucessoCallback.run();
        }

        Stage stage = (Stage) botaoCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelarPagamento() {
        Stage stage = (Stage) botaoCancelar.getScene().getWindow();
        stage.close();
    }
}