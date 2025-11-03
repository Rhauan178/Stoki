package com.seu.restaurante;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public final class QRCodeUtil {

    private QRCodeUtil() {
        // Construtor privado para impedir a criação de instâncias
    }

    /**
     * Gera uma imagem de QR code a partir de um texto.
     * @param texto O texto a ser codificado no QR Code (ex: chave PIX).
     * @param largura A largura da imagem em pixels.
     * @param altura A altura da imagem em pixels.
     * @return Um objeto Image do JavaFX contendo o QR code, ou null em caso de erro.
     */
    public static Image gerarQRCodeImage(String texto, int largura, int altura) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, largura, altura);

            // Converte a matriz de bits para uma imagem em memória (BufferedImage)
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Converte a BufferedImage (do Java AWT/Swing) para uma Image (do JavaFX)
            return SwingFXUtils.toFXImage(bufferedImage, null);

        } catch (WriterException e) {
            System.err.println("Erro ao gerar o QR Code: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}