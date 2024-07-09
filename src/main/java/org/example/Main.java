package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int limit = 1;
        String url = "https://ismp.crpt.ru/api/v3/lk/documents/create";
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, limit, url);
        String json = "";
        try {
            Path path = Paths.get("src/main/java/org/example/doc.json");
            if (Files.exists(path)) {
                byte[] bytes = Files.readAllBytes(path);
                json = new String(bytes, StandardCharsets.UTF_8);
            } else {
                System.out.println("Файл не найден");
            }
        } catch (IOException e) {
            System.err.println("Произошла ошибка при чтении файла: " + e.getMessage());
        }
        Gson gson = new Gson();
        CrptApi.Document doc = gson.fromJson(json, CrptApi.Document.class);
        String sign = "New signature";
        crptApi.createDocument(doc, sign);
        crptApi.shutdown();
    }
}
