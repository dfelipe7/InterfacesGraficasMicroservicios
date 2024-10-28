package vistasOrganizador;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Unicauca
 */
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ConferenceService {

    private static final String BASE_URL = "http://localhost:8085/api/conferences";

    private HttpClient client = HttpClient.newHttpClient();

    public String createConference(String name, String location, String startDate, String endDate, String topics, String userId) throws Exception {
        String json = String.format("{\"name\": \"%s\", \"location\": \"%s\", \"startDate\": \"%s\", \"endDate\": \"%s\", \"topics\": \"%s\"}",
                name, location, startDate, endDate, topics);

        System.out.println("JSON a enviar: " + json); // Para depuración

        // Modifica la URL para incluir el userId como parámetro de consulta
        String urlWithParams = String.format("%s?userId=%s", BASE_URL, userId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlWithParams))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return "Conferencia creada con éxito";
        } else {
            throw new Exception("Error al crear la conferencia: " + response.body());
        }
    }

    public String[][] getConferences() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonElement jsonElement = JsonParser.parseString(response.body());
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            // Aumenta el tamaño a 6 columnas para incluir el ID
            String[][] data = new String[jsonArray.size()][6];

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement element = jsonArray.get(i);

                // Incluye el ID en la primera columna
                data[i][0] = element.getAsJsonObject().get("id").getAsString();
                data[i][1] = element.getAsJsonObject().get("name").getAsString();
                data[i][2] = element.getAsJsonObject().get("location").getAsString();
                data[i][3] = element.getAsJsonObject().get("startDate").getAsString();
                data[i][4] = element.getAsJsonObject().get("endDate").getAsString();
                data[i][5] = element.getAsJsonObject().get("topics").getAsString();
            }

            return data;
        } else {
            throw new Exception("Error al obtener las conferencias: " + response.body());
        }
    }

    public String updateConference(Long conferenceId, String name, String location, String startDate, String endDate, String topics, String userId) throws Exception {
        String json = String.format("{\"name\": \"%s\", \"location\": \"%s\", \"startDate\": \"%s\", \"endDate\": \"%s\", \"topics\": \"%s\"}",
                name, location, startDate, endDate, topics);

        // URL que incluye el userId como parámetro de consulta
        String urlWithParams = String.format("%s/%d?userId=%s", BASE_URL, conferenceId, userId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlWithParams))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return "Conferencia actualizada con éxito";
        } else {
            throw new Exception("Error al actualizar la conferencia: " + response.body());
        }
    }

    public String deleteConference(Long conferenceId, String userId) throws Exception {
        // URL que incluye el userId como parámetro de consulta
        String urlWithParams = String.format("%s/%d?userId=%s", BASE_URL, conferenceId, userId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlWithParams))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return "Conferencia eliminada con éxito";
        } else {
            throw new Exception("Error al eliminar la conferencia: " + response.body());
        }
    }

}

// Métodos para Update y Delete pueden seguir la misma estructura

