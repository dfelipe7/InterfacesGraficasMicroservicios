package vistasAutor;

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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class ArticleService {

    private static final String BASE_URL = "http://localhost:8095/api/articles";
    private static final String CONFERENCES_URL = "http://localhost:8085/api/conferences"; // Microservicio de conferencias
    private HttpClient client = HttpClient.newHttpClient();
//private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();

    public String createArticle(String name, String summary, String keywords, String conferenceId, String userId, String filePath) throws Exception {
        // Crear el JSON del artículo con los campos correctos
        Map<String, Object> articleData = Map.of(
            "name", name,         // Cambiado de "title" a "name"
            "summary", summary,    // Cambiado de "abstract" a "summary"
            "keywords", keywords,
            "filePath", filePath  // Añadir la ruta del archivo
        );

        String json = objectMapper.writeValueAsString(articleData);

        System.out.println("JSON a enviar: " + json);

        // Modificar la URL para incluir userId y conferenceId como parámetros de consulta
        String urlWithParams = String.format("%s?userId=%s&conferenceId=%s", BASE_URL, userId, conferenceId);

        System.out.println("URL con parámetros: " + urlWithParams);

        // Crear la solicitud HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlWithParams))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        // Enviar la solicitud y obtener la respuesta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Imprimir la respuesta del servidor para depuración
        System.out.println("Código de respuesta: " + response.statusCode());
        System.out.println("Respuesta del servidor: " + response.body());

        // Comprobar el código de respuesta
        if (response.statusCode() == 201) {
            return "Artículo creado exitosamente.";
        } else {
            throw new Exception("Error al crear el artículo: " + response.body());
        }
    }



    // Método para obtener las conferencias
    public String[][] getConferences() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(CONFERENCES_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonElement jsonElement = JsonParser.parseString(response.body());
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            // Aumenta el tamaño a 6 columnas para incluir el ID
            String[][] data = new String[jsonArray.size()][2];

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement element = jsonArray.get(i);

                // Incluye el ID en la primera columna
                data[i][0] = element.getAsJsonObject().get("id").getAsString();
                data[i][1] = element.getAsJsonObject().get("name").getAsString();
            }

            return data;
        } else {
            throw new Exception("Error al obtener las conferencias: " + response.body());
        }
    }
    
    
   public String[][] getArticles() throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(BASE_URL))
            .GET()
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        // Ajusta el tamaño de la matriz de acuerdo a los campos que necesitas mostrar
        String[][] data = new String[jsonArray.size()][6];

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement element = jsonArray.get(i);

            data[i][0] = element.getAsJsonObject().get("id").getAsString();
            data[i][1] = element.getAsJsonObject().get("name").getAsString(); // "name" en lugar de "title"
            data[i][2] = element.getAsJsonObject().get("summary").getAsString(); // "summary" en lugar de "abstract"
            data[i][3] = element.getAsJsonObject().get("keywords").getAsString();
            data[i][4] = element.getAsJsonObject().get("filePath").getAsString();
            data[i][5] = element.getAsJsonObject().get("autorId").getAsString(); // "autorId" en lugar de "authorId"
        }

        return data;
    } else {
        throw new Exception("Error al obtener los artículos: " + response.body());
    }
}


   public String updateArticle(Long articleId, String title, String abstractText, String keywords, String pdfFilePath,  String userId) throws Exception {
    String json = String.format("{\"name\": \"%s\", \"summary\": \"%s\", \"keywords\": \"%s\", \"filePath\": \"%s\"}",
            title, abstractText, keywords, pdfFilePath);

    // URL que incluye el userId como parámetro de consulta
    String urlWithParams = String.format("%s/%d?userId=%s", BASE_URL, articleId, userId);

    HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(urlWithParams))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 200) {
        return "Artículo actualizado con éxito";
    } else {
        throw new Exception("Error al actualizar el artículo: " + response.body());
    }
}

    public String deleteArticle(Long articleId, String userId) throws Exception {
        // URL que incluye el userId como parámetro de consulta
        String urlWithParams = String.format("%s/%d?userId=%s", BASE_URL, articleId, userId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlWithParams))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return "Artículo eliminado con éxito";
        } else {
            throw new Exception("Error al eliminar el artículo: " + response.body());
        }
    }

}
