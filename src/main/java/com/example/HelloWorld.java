package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class HelloWorld {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    private static final int SECRET_NUMBER = new Random().nextInt(100) + 1;
    
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        logger.info("Starting server on port: " + port);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new GuessHandler());
        server.start();
        logger.info("Server started successfully! Secret number generated.");
    }
    
    static class GuessHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            
            String query = exchange.getRequestURI().getQuery();
            String response;
            
            if (query != null && query.startsWith("guess=")) {
                try {
                    int guess = Integer.parseInt(query.split("=")[1]);
                    response = checkGuess(guess);
                } catch (NumberFormatException e) {
                    response = "Invalid input. Please enter a number between 1 and 100.";
                }
            } else {
                response = "Welcome to Guess The Number! Make a guess by adding ?guess=your_number to the URL.";
            }
            
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String checkGuess(int guess) {
            if (guess < SECRET_NUMBER) {
                return "Too Low! Try again.";
            } else if (guess > SECRET_NUMBER) {
                return "Too High! Try again.";
            } else {
                return "Correct! You guessed the number!";
            }
        }
    }
}