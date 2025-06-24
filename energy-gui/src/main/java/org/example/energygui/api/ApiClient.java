package org.example.energygui.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.energygui.model.CurrentData;

import java.net.http.*;
import java.net.URI;
import java.util.List;

public class ApiClient {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static CurrentData getCurrent() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/energy/current"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), CurrentData.class);
    }

    public static List<CurrentData> getHistorical(String start, String end) throws Exception {
        String url = "http://localhost:8080/energy/historical?start=" + start + "&end=" + end;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<List<CurrentData>>() {});
    }


}
