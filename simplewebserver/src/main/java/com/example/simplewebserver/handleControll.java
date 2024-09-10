//package com.example.simplewebserver;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//public class handleControll {
//    protected static void handleClient(Socket socket) {
//        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
//
//            String message;
//            while ((message = in.readLine()) != null) {
//                System.out.println("Received from Python: " + message);
//                String response = "Processed: " + message;
//                out.println(response);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
