package com.bajaj.WebhookApplication;



import com.bajaj.WebhookApplication.WebhookResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
        import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class WebhookRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) {
        String webhookGenerationUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "name", "Himanshu Patidar",
                "regNo", "REG684",
                "email", "himanshu@gmail.com"
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                webhookGenerationUrl, request, WebhookResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            WebhookResponse webhookData = response.getBody();

            String sql = getSqlQuery(); // Your solution here

            postSqlSolution(webhookData.getWebhook(), webhookData.getAccessToken(), sql);
        }
    }

    private String getSqlQuery() {
        // Replace this with your final SQL solution
        return "SELECT \n" +
                "    e.EMP_ID,\n" +
                "    e.FIRST_NAME,\n" +
                "    e.LAST_NAME,\n" +
                "    d.DEPARTMENT_NAME,\n" +
                "    COUNT(y.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT\n" +
                "FROM \n" +
                "    EMPLOYEE e\n" +
                "JOIN \n" +
                "    DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID\n" +
                "LEFT JOIN \n" +
                "    EMPLOYEE y ON e.DEPARTMENT = y.DEPARTMENT AND e.DOB > y.DOB\n" +
                "GROUP BY \n" +
                "    e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME\n" +
                "ORDER BY \n" +
                "    e.EMP_ID DESC;\n";
    }

    private void postSqlSolution(String webhookUrl, String token, String sqlQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token); // Authorization: Bearer <token>

        Map<String, String> body = Map.of("finalQuery", sqlQuery);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);

        System.out.println("Webhook response: " + response.getBody());
    }
}
