package org.example;

import lombok.Data;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;


public class CrptApi {
    private final TimeUnit timeUnit;
    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String apiUrl;
    @Data
    static class Document {
        private String description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private String production_date;
        private String production_type;
        private List<Product> products;
        private String reg_date;
        private String reg_number;
    }
    @Data
    public class Product {
        private String certificate_document;
        private String certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private String production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;
    }

    public CrptApi(TimeUnit timeUnit, int limit, String apiUrl) {
        this.timeUnit = timeUnit;
        this.semaphore = new Semaphore(limit);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.apiUrl = apiUrl;
    }

    public void createDocument(Document document, String signature) throws InterruptedException {
        semaphore.acquire();
        scheduler.scheduleAtFixedRate(() -> semaphore.release(), 1, 1, timeUnit);
        try {
            HttpRequest request = buildRequest(document, signature);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Успешно");
            } else {
                System.out.println(response.statusCode());
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    private HttpRequest buildRequest(Document document, String signature) {
        String json = convertToJson(document);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return request;
    }

    private String convertToJson(Document document) {
        Gson gson = new Gson();
        return gson.toJson(document);
    }


}