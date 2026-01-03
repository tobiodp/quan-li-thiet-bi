package com.example.ResourcesManagement.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QRCodeService {

    /**
     * Tạo QR Code từ text và trả về dưới dạng Base64 string
     * @param text Nội dung cần mã hóa vào QR Code
     * @param width Chiều rộng QR Code (pixels)
     * @param height Chiều cao QR Code (pixels)
     * @return Base64 string của ảnh QR Code
     */
    public String generateQRCodeBase64(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrCodeBytes = outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Lỗi khi tạo QR Code: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo QR Code với kích thước mặc định (300x300)
     */
    public String generateQRCodeBase64(String text) {
        return generateQRCodeBase64(text, 300, 300);
    }
}

