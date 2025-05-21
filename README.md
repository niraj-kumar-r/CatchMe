# 🕵️‍♂️ CatchMe - Digital Steganography in Java (Spring Boot + Thymeleaf)

## 📌 Overview

**CatchMe** is a secure web application that hides secret text messages inside image files using **Least Significant Bit (LSB)** steganography. Built using **Java, Spring Boot**, and **Thymeleaf**, this project demonstrates how data can be transmitted covertly using images as carriers — without drawing attention or arousing suspicion.

> This project includes both encoding (hiding text in images) and decoding (extracting hidden text from images) functionalities through a clean web interface.

---

## 🔐 What is Digital Steganography?

Digital Steganography is the art of concealing information within digital files — such as images — in a way that does not attract attention. Unlike encryption, which protects data by scrambling it, **steganography hides the very existence of the data**.

---

## 🧰 Tech Stack

| Layer         | Technology            |
|---------------|------------------------|
| Backend       | Java 17, Spring Boot   |
| Frontend      | Thymeleaf (HTML + CSS) |
| Security      | Spring Security (customized or disabled for demo) |
| Build Tool    | Maven            |
| Deployment    | Embedded Tomcat (Spring Boot) |
| Version Control | Git + GitHub         |

---

## 🚀 Features

- 🔐 Encode secret messages inside PNG images
- 🔓 Decode and retrieve hidden messages from images
- 🧼 Simple web interface using Thymeleaf
- 📁 Upload and download modified image files
- 🚫 403-protected endpoints with Spring Security (optional)
- 🛠️ Pluggable for future improvements like password protection or multiple image formats

---

## 📂 Project Structure

```
CatchMe/
├── src/
│   ├── main/
│   │   ├── java/org/mytest/catchme/
│   │   │   ├── CatchMeApplication.java
│   │   │   ├── controller/
│   │   │   │   └── StegoController.java
│   │   │   ├── service/
│   │   │   │   └── StegoService.java
│   │   │   └── config/
│   │   │       └── SecurityConfig.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── index.html
│   │       │   └── decode.html
│   │       └── static/
│   │           └── css/, js/
│   │       └── application.properties
├── pom.xml
└── README.md
```

---

## 🛠️ Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/CatchMe.git
cd CatchMe
```

### 2. Add Stego Functionality

Make sure your `StegoService` includes LSB-based image manipulation logic to hide and reveal messages using Java’s image processing libraries (`BufferedImage`, `ImageIO`).

### 3. Build the Project

If using Maven:

```bash
mvn clean install
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

### 5. Access the Web UI

Open your browser and go to:  
`http://localhost:8080`

---

## 🧪 How It Works

### Encoding Process

1. Upload a PNG image
2. Enter the message to hide
3. App embeds the message into the image’s least significant bits (LSBs)
4. Download the modified image

### Decoding Process

1. Upload a stego image
2. The app reads the LSBs and extracts the message
3. The hidden text is revealed on the web interface

---

## 🛡️ Security

Spring Security is configured via `SecurityConfig.java`. By default, it:

- Disables CSRF (for simple forms)
- Permits unauthenticated access to encoding/decoding endpoints

To enable authentication, modify the config and add login credentials as needed.

---

## 🧩 Future Improvements

- 🧠 Support for other formats (JPG, BMP)
- 🔑 Password-protected stego images
- 🌐 Angular-based frontend (optional)
- 🧪 Unit + Integration testing
- 💾 Store message history using a database

---

## 🤝 Contributions

Pull requests are welcome! If you'd like to contribute, feel free to fork the repo and submit a PR.

---

## 📝 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 🙋‍♂️ Author

👤 **Niraj Kumar**  
Feel free to connect via [GitHub](https://github.com/niraj-kumar-r) or [LinkedIn](https://www.linkedin.com/in/niraj-kumar-r/).