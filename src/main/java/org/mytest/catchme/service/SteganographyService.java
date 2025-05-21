package org.mytest.catchme.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class SteganographyService {

    // Encode message into image bytes using LSB (only PNG supported here)
    public byte[] encodeMessageInImage(byte[] imageBytes, String message) throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (image == null) throw new IllegalArgumentException("Invalid image");

        byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
        int msgLength = msgBytes.length;

        if (msgLength * 8 + 32 > image.getWidth() * image.getHeight()) {
            throw new IllegalArgumentException("Message too long to encode in image");
        }

        // Encode message length in first 32 pixels' least significant bits
        for (int i = 0; i < 32; i++) {
            int bit = (msgLength >>> (31 - i)) & 1;
            int rgb = image.getRGB(i, 0);
            rgb = (rgb & 0xFFFFFFFE) | bit; // Replace LSB of blue channel
            image.setRGB(i, 0, rgb);
        }

        // Encode message bits starting at pixel 32
        int msgBitIndex = 0;
        outer:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = (y == 0 ? 32 : 0); x < image.getWidth(); x++) {
                if (msgBitIndex >= msgLength * 8) break outer;

                int bit = (msgBytes[msgBitIndex / 8] >>> (7 - (msgBitIndex % 8))) & 1;
                int rgb = image.getRGB(x, y);
                rgb = (rgb & 0xFFFFFFFE) | bit; // Replace LSB of blue channel
                image.setRGB(x, y, rgb);

                msgBitIndex++;
            }
        }

        // Convert back to byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    // Decode message from image bytes
    public String decodeMessageFromImage(byte[] imageBytes) throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (image == null) throw new IllegalArgumentException("Invalid image");

        // Decode message length from first 32 pixels
        int msgLength = 0;
        for (int i = 0; i < 32; i++) {
            int rgb = image.getRGB(i, 0);
            int bit = rgb & 1;
            msgLength = (msgLength << 1) | bit;
        }

        byte[] msgBytes = new byte[msgLength];
        int msgBitIndex = 0;
        outer:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = (y == 0 ? 32 : 0); x < image.getWidth(); x++) {
                if (msgBitIndex >= msgLength * 8) break outer;

                int rgb = image.getRGB(x, y);
                int bit = rgb & 1;
                msgBytes[msgBitIndex / 8] = (byte) ((msgBytes[msgBitIndex / 8] << 1) | bit);

                msgBitIndex++;
            }
        }

        // Fix byte alignment: each byte is built bit by bit MSB first, so after loop,
        // bytes are reversed per byte, fix that:
        for (int i = 0; i < msgBytes.length; i++) {
            msgBytes[i] = reverseByte(msgBytes[i]);
        }

        return new String(msgBytes, StandardCharsets.UTF_8);
    }

    // Helper: reverse bits in a byte
    private byte reverseByte(byte b) {
        int rev = 0;
        for (int i = 0; i < 8; i++) {
            rev = (rev << 1) | (b & 1);
            b >>= 1;
        }
        return (byte) rev;
    }
}
