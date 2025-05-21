package org.mytest.catchme.controller;

import org.mytest.catchme.service.SteganographyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/steganography")
@CrossOrigin(origins = "http://localhost:4200")  // Allow Angular frontend
public class SteganographyController {

    private final SteganographyService stegoService;

    public SteganographyController(SteganographyService stegoService) {
        this.stegoService = stegoService;
    }

    // Encode message into image
    @PostMapping("/encode")
    public ResponseEntity<byte[]> encode(
            @RequestParam("image") MultipartFile image,
            @RequestParam("message") String message
    ) throws Exception {
        byte[] encodedImage = stegoService.encodeMessageInImage(image.getBytes(), message);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=encoded.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(encodedImage);
    }

    // Decode message from image
    @PostMapping("/decode")
    public ResponseEntity<String> decode(
            @RequestParam("image") MultipartFile image
    ) throws Exception {
        String message = stegoService.decodeMessageFromImage(image.getBytes());
        return ResponseEntity.ok(message);
    }
}
