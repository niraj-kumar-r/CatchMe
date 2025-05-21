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
        // Read the image from byte array
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(bais);

        if (image == null) {
            throw new Exception("Invalid image format or corrupted image data");
        }

        // Convert message to binary string
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        StringBuilder binaryMessage = new StringBuilder();

        // Add message length as a 32-bit integer prefix
        int messageLength = messageBytes.length;
        for (int i = 31; i >= 0; i--) {
            binaryMessage.append((messageLength >> i) & 1);
        }

        // Append message bytes as binary
        for (byte b : messageBytes) {
            for (int i = 7; i >= 0; i--) {
                binaryMessage.append((b >> i) & 1);
            }
        }

        // Check if the message can fit in the image
        int totalPixels = image.getWidth() * image.getHeight();
        int maxBits = totalPixels * 3; // 3 channels (RGB) per pixel

        if (binaryMessage.length() > maxBits) {
            throw new Exception("Message is too large for this image. Maximum capacity: " +
                    (maxBits / 8) + " bytes, but message is " + messageBytes.length + " bytes.");
        }

        // Embed the message bits into the image's pixel LSBs
        int bitIndex = 0;
        outerLoop:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);

                // Extract RGB components
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                // Modify the LSB of each color component if there are still bits to embed
                if (bitIndex < binaryMessage.length()) {
                    red = (red & 0xFE) | (binaryMessage.charAt(bitIndex++) == '1' ? 1 : 0);
                }

                if (bitIndex < binaryMessage.length()) {
                    green = (green & 0xFE) | (binaryMessage.charAt(bitIndex++) == '1' ? 1 : 0);
                }

                if (bitIndex < binaryMessage.length()) {
                    blue = (blue & 0xFE) | (binaryMessage.charAt(bitIndex++) == '1' ? 1 : 0);
                }

                // Reconstruct the pixel and set it back in the image
                int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(x, y, newPixel);

                // If we've embedded all bits, break out of both loops
                if (bitIndex >= binaryMessage.length()) {
                    break outerLoop;
                }
            }
        }

        // Convert the modified image back to byte array (PNG format)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);

        return baos.toByteArray();
    }

    // Decode message from image bytes
    public String decodeMessageFromImage(byte[] imageBytes) throws Exception {
        // Read the image from byte array
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(bais);

        if (image == null) {
            throw new Exception("Invalid image format or corrupted image data");
        }

        // First, extract the length of the hidden message (first 32 bits)
        StringBuilder binaryLengthBuilder = new StringBuilder();
        int bitIndex = 0;

        outerLoop1:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);

                // Extract RGB components
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                // Extract LSB from each color component
                binaryLengthBuilder.append(red & 1);
                bitIndex++;

                if (bitIndex >= 32) {
                    break outerLoop1;
                }

                if (bitIndex < 32) {
                    binaryLengthBuilder.append(green & 1);
                    bitIndex++;
                }

                if (bitIndex >= 32) {
                    break outerLoop1;
                }

                if (bitIndex < 32) {
                    binaryLengthBuilder.append(blue & 1);
                    bitIndex++;
                }

                if (bitIndex >= 32) {
                    break outerLoop1;
                }
            }
        }

        // Convert binary string to message length
        int messageLength = 0;
        for (int i = 0; i < 32; i++) {
            messageLength = (messageLength << 1) | (binaryLengthBuilder.charAt(i) == '1' ? 1 : 0);
        }

        // Validate that the message length is reasonable
        if (messageLength <= 0 || messageLength > (image.getWidth() * image.getHeight() * 3 - 32) / 8) {
            throw new Exception("Invalid or corrupted steganography data detected");
        }

        // Extract the message bits
        StringBuilder binaryMessageBuilder = new StringBuilder();
        int bitsNeeded = messageLength * 8;
        bitIndex = 0;

        // Start from where we left off (after the 32-bit length)
        int currentY = 0;
        int currentX = 0;
        int colorIndex = 0; // 0 = red, 1 = green, 2 = blue

        // Skip to where we left off after reading the message length
        for (int i = 0; i < 32; i++) {
            colorIndex++;
            if (colorIndex > 2) {
                colorIndex = 0;
                currentX++;
                if (currentX >= image.getWidth()) {
                    currentX = 0;
                    currentY++;
                }
            }
        }

        // Now extract the actual message
        for (int i = 0; i < bitsNeeded; i++) {
            int pixel = image.getRGB(currentX, currentY);

            switch (colorIndex) {
                case 0: // Red
                    binaryMessageBuilder.append((pixel >> 16) & 1);
                    break;
                case 1: // Green
                    binaryMessageBuilder.append((pixel >> 8) & 1);
                    break;
                case 2: // Blue
                    binaryMessageBuilder.append(pixel & 1);
                    break;
            }

            // Move to next color channel or pixel
            colorIndex++;
            if (colorIndex > 2) {
                colorIndex = 0;
                currentX++;
                if (currentX >= image.getWidth()) {
                    currentX = 0;
                    currentY++;

                    if (currentY >= image.getHeight()) {
                        break; // Reached end of image
                    }
                }
            }
        }

        // Convert the binary message to bytes and then to string
        byte[] messageBytes = new byte[messageLength];
        for (int i = 0; i < messageLength; i++) {
            for (int j = 0; j < 8; j++) {
                if (binaryMessageBuilder.charAt(i * 8 + j) == '1') {
                    messageBytes[i] |= (1 << (7 - j));
                }
            }
        }

        return new String(messageBytes, StandardCharsets.UTF_8);
    }
}