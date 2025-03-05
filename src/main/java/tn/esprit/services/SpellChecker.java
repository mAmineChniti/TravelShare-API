package tn.esprit.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

public class SpellChecker {

    private static final String API_URL = "https://api.languagetool.org/v2/check";  // L'URL de l'API

    public static List<String> checkSpelling(String text) {
        try {
            // Encoder le texte pour éviter les problèmes avec les caractères spéciaux
            String encodedText = URLEncoder.encode(text, "UTF-8");

            // Créer la requête POST avec le texte à vérifier
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("text=" + encodedText + "&language=fr"))
                    .build();

            // Envoyer la requête et obtenir la réponse
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Vérifier si la réponse est valide
            if (response.statusCode() != 200) {
                throw new Exception("Erreur dans la réponse de l'API, code: " + response.statusCode());
            }

            // Analyser la réponse JSON
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray matches = jsonResponse.getJSONArray("matches");

            // Extraire les messages d'erreur
            return matches.toList().stream()
                    .map(match -> ((JSONObject) match).getString("message"))
                    .collect(Collectors.toList());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return List.of("Erreur d'encodage de texte");
        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Erreur de vérification orthographique");
        }
    }
}


