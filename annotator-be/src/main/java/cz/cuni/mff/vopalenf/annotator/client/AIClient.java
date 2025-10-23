package cz.cuni.mff.vopalenf.annotator.client;

import cz.cuni.mff.vopalenf.annotator.api.model.AIModelUpdateResponse;
import cz.cuni.mff.vopalenf.annotator.api.model.LogData;
import cz.cuni.mff.vopalenf.annotator.api.model.PredictionTriple;
import cz.cuni.mff.vopalenf.annotator.api.model.ProjectExportAnnotated;
import cz.cuni.mff.vopalenf.annotator.api.model.ProjectExportWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Client for interacting with the AI service.
 */
@Service
public class AIClient {
    /**
     * RestTemplate used for making HTTP requests to the AI service.
     */
    private final RestTemplate restTemplate;
    /**
     * Base URL of the AI service.
     */
    @Value("${app.ai.url}")
    String aiUrl = "http://localhost:8081";

    /**
     * Constructor for AIClient.
     *
     * @param restTemplate
     *            The RestTemplate used for making HTTP requests.
     */
    @Autowired
    public AIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Sends log data to the AI service for processing.
     *
     * @param projectId
     *            The ID of the project associated with the log data.
     * @param annotatedDataInCsv
     *            Data in csv containing data from sensor and label from app.
     * @return An AIModelUpdateResponse object containing the status, accuracy, and number of projects.
     */
    public AIModelUpdateResponse sendLogData(Long projectId, ProjectExportWrapper annotatedDataInCsv) {
        String url = aiUrl + "/api/ai/" + projectId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> requestEntity = new HttpEntity<>(annotatedDataInCsv.getCsvData(), headers);

        ResponseEntity<AIModelUpdateResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }
}
