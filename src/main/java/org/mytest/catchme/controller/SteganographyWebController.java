package org.mytest.catchme.controller;

import org.mytest.catchme.service.SteganographyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SteganographyWebController {

    private final SteganographyService stegoService;
    private static final Logger logger = LoggerFactory.getLogger(SteganographyWebController.class);

    public SteganographyWebController(SteganographyService stegoService) {
        this.stegoService = stegoService;
    }

    // Show home page
    @GetMapping("/")
    public String home() {
        return "index";  // Thymeleaf template 'index.html'
    }

    // Handle encoding form
    @PostMapping("/encode")
    public ResponseEntity<ByteArrayResource> encode(
            @RequestParam("image") MultipartFile image,
            @RequestParam("message") String message
    ) throws Exception {
        byte[] encodedImage = stegoService.encodeMessageInImage(image.getBytes(), message);
        logger.info("Message encoded successfully!");

        // Get original filename without extension
        String originalFilename = image.getOriginalFilename();
        String baseName = (originalFilename != null) ? originalFilename.replaceFirst("[.][^.]+$", "") : "encoded";

        // Create dynamic filename with timestamp
        String dynamicFilename = baseName + "_encoded_" + System.currentTimeMillis() + ".png";

        ByteArrayResource resource = new ByteArrayResource(encodedImage);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + dynamicFilename)
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    // Show decode page
    @GetMapping("/decode")
    public String decodePage() {
        return "decode";  // Thymeleaf template 'decode.html'
    }

    // Handle decoding form
    @PostMapping("/decode")
    public String decode(
            @RequestParam("image") MultipartFile image,
            Model model
    ) throws Exception {
        String message = stegoService.decodeMessageFromImage(image.getBytes());
        model.addAttribute("decodedMessage", message);
        return "decode";  // Show decoded message on same page
    }
}
