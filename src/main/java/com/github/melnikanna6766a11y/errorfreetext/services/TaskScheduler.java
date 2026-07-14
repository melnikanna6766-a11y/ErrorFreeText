package com.github.melnikanna6766a11y.errorfreetext.services;

import com.github.melnikanna6766a11y.errorfreetext.CheckTextsResponseHandler;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.repositories.TaskCriteriaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class TaskScheduler {
    private TaskCriteriaRepository taskCriteriaRepository;

    @Scheduled(fixedRate = 2000)
    @Async
    public void handleTask() {
        Task task = taskCriteriaRepository.findEarliestCreatedTask();
        List<String> jsons = new CheckTextsResponseHandler().createCheckTextResponse(task);
        for (String json: jsons) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://speller.yandex.net/services/spellservice.json"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            try(HttpClient client = HttpClient.newHttpClient()) {
                client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {

            }
        }
    }
}
