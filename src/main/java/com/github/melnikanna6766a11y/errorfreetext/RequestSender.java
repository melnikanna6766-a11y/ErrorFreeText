package com.github.melnikanna6766a11y.errorfreetext;

import com.github.melnikanna6766a11y.errorfreetext.dto.CheckTextsResponse;
import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

public class RequestSender {

    public List<List<CorrectedTextResponse>> sendRequest(CheckTextsResponse checkTextsResponse) {
        RestClient restClient = RestClient.builder().baseUrl("https://speller.yandex.net/services/spellservice.json/checkTexts").build();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", Arrays.toString(checkTextsResponse.text()));
        params.add("lang", checkTextsResponse.lang());
        params.add("options", Integer.toString(checkTextsResponse.option()));
        return restClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve()
                .body(new ParameterizedTypeReference<List<List<CorrectedTextResponse>>>() {});
    }
}
