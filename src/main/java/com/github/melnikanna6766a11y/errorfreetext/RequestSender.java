package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsRequest;
import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.buf.UriUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class RequestSender {

    public List<List<CorrectedTextResponse>> sendRequest(CheckTextsRequest checkTextsRequest) {
        log.info("Sending a request {}, with lang {} and options {}", checkTextsRequest.text(), checkTextsRequest.lang(), checkTextsRequest.option());
        RestClient restClient = RestClient.builder().baseUrl("https://speller.yandex.net/services/spellservice.json/checkTexts").build();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        for (String value: checkTextsRequest.text()) {
            params.add("text", URLEncoder.encode(value, StandardCharsets.UTF_8));
        }
        params.add("lang", checkTextsRequest.lang());
        params.add("options", Integer.toString(checkTextsRequest.option()));
        var response = restClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve();
        log.info(response.toString());
        return response.body(new ParameterizedTypeReference<List<List<CorrectedTextResponse>>>() {});
    }
}
