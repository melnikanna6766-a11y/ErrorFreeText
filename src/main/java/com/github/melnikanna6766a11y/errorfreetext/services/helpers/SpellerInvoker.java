package com.github.melnikanna6766a11y.errorfreetext.services.helpers;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.SpellerResponse;
import com.github.melnikanna6766a11y.errorfreetext.entity.Task;
import com.github.melnikanna6766a11y.errorfreetext.exceptions.SpellerServerErrorException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class SpellerInvoker {

    public List<List<SpellerResponse>> composeResponseFromSpeller(List<List<SpellerResponse>> spellerResponse) {
        List<List<SpellerResponse>> spellerResponses = new ArrayList<>();
        if (spellerResponse != null) {
            spellerResponses.add(
                    spellerResponse.stream()
                            .filter(response -> !response.isEmpty())
                            .map(List::removeFirst)
                            .toList()
            );
        } else {
            log.warn("Response for task, was not created because no response body was available");
            return null;
        }
        return spellerResponses;
    }

    public List<List<SpellerResponse>> sendRequestToSpeller(CheckTextsRequest checkTextsRequest, Task task) {
        log.info("Sending a request {}, with lang {} and options {}", checkTextsRequest.text(), checkTextsRequest.lang(), checkTextsRequest.option());
        RestClient restClient = RestClient.builder().baseUrl("https://speller.yandex.net/services/spellservice.json/checkTexts").build();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        for (String value: checkTextsRequest.text()) {
            params.add("text", URLEncoder.encode(value, StandardCharsets.UTF_8));
        }
        params.add("lang", checkTextsRequest.lang());
        params.add("options", Integer.toString(checkTextsRequest.option()));
        RestClient.ResponseSpec response = restClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve();
        try {
            return response.body(new ParameterizedTypeReference<>() {});
        } catch (HttpServerErrorException e) {
            throw new SpellerServerErrorException("503 yandex speller service unavailable", task);
        }
    }
}
