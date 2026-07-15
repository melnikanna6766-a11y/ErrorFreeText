package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestSender {

    public CorrectedTextResponse sendRequest(String json) {
        final CorrectedTextResponse[] correctedTextResponse = {null};
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://speller.yandex.net/services/spellservice.json"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try (HttpClient client = HttpClient.newHttpClient()) {
            client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            correctedTextResponse[0] = parsJson(response.body());
                            return correctedTextResponse[0];
                        } else {
                            return response.statusCode();
                        }
                    })
                    .exceptionally(Throwable::fillInStackTrace);
        } catch (Exception e) {

        }
        return correctedTextResponse[0];
    }

    private CorrectedTextResponse parsJson(String response) {
        JsonNode node = new ObjectMapper().readTree(response);
        return new CorrectedTextResponse(
                node.findValue("code").asInt(),
                node.findValue("pos").asInt(),
                node.findValue("row").asInt(),
                node.findValue("col").asInt(),
                node.findValue("len").asInt(),
                node.findValue("word").asString(),
                node.findValue("s").asString()
        );
    }
}
