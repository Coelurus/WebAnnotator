package cz.cuni.mff.vopalenf.annotator.client;

import cz.cuni.mff.vopalenf.annotator.api.model.LogData;
import cz.cuni.mff.vopalenf.annotator.api.model.PredictionTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AIClient {
    private final RestTemplate restTemplate;

    @Autowired
    public AIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PredictionTriple> sendLogData(Long projectId, List<LogData> logDataList) {
        String url = "http://localhost:8081/api/ai/" + projectId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<LogData>> requestEntity = new HttpEntity<>(logDataList, headers);

        ResponseEntity<List<PredictionTriple>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<PredictionTriple>>() {}
        );

        return response.getBody();
    }
}
