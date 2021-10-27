package com.hutchison.vibe.client.youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
@Slf4j
class YouTubeClient {

    private static final String APPLICATION_NAME = "Vibe";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final String developerKey;
    private YouTube yt;


    public YouTubeClient(@Value("${youtube.api.key}") String developer_key) {
        developerKey = developer_key;
        try {
            yt = getService();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error setting up service for Youtube.", e);
        }
    }

    public SearchListResponse search(YouTubeParams params) throws IOException, GeneralSecurityException {
        YouTube youtubeService = getYoutube();
        YouTube.Search.List request = youtubeService.search()
                .list("snippet")
                .setKey(developerKey);
        params.getParamMap().forEach((key, value) ->
                request.set(key.getKeyValue(), value));
        return request.execute();
    }

    private YouTube getYoutube() throws GeneralSecurityException, IOException {
        if (yt == null) {
            yt = getService();
        }
        return yt;
    }

    private static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
