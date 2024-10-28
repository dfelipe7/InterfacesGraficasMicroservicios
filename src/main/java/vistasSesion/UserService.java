package vistasSesion;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Unicauca
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;



public class UserService {

    private static final String BASE_URL = "http://localhost:8090/api/users";

    public String login(String email, String password) throws Exception {
        String json = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return "Inicio de sesion correcto";
        } else {
            throw new Exception("Error al iniciar sesión: " + response.body());
        }
    }
public String registerUser(String name, String email, String password, String role) {
        try {
            URL url = new URL(BASE_URL + "/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Crear el JSON para enviar
            String jsonInputString = String.format("{\"name\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"role\":\"%s\"}", name, email, password, role);

            // Enviar la solicitud
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Leer la respuesta
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // Si la respuesta es correcta
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString(); // Retorna la respuesta
                }
            } else {
                // Leer la respuesta de error
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    return "Error en el registro, código de respuesta: " + responseCode + ", Detalles: " + errorResponse.toString();
                }
            }
        } catch (IOException e) {
            return "Error al registrarse: " + e.getMessage(); // Manejar el error
        }
    }


    public String getUserRole(String email) {
        try {
            URL url = new URL(BASE_URL + "/role?email=" + email);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // Leer la respuesta
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString(); // Retorna el rol del usuario
                }
            } else {
                return "Error al obtener el rol del usuario, código de respuesta: " + responseCode;
            }
        } catch (IOException e) {
            return "Error al obtener el rol del usuario: " + e.getMessage();
        }
    }

    public String getUserId(String email) {
        try {
            URL url = new URL(BASE_URL + "/id?email=" + email);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // Leer la respuesta
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString(); // Retorna el ID del usuario
                }
            } else {
                return "Error al obtener el ID del usuario, código de respuesta: " + responseCode;
            }
        } catch (IOException e) {
            return "Error al obtener el ID del usuario: " + e.getMessage();
        }
    }

}




