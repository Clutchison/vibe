package com.hutchison.vibe.client.youtube;

import com.hutchison.vibe.model.youtube.YouTubeSearchResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.hutchison.vibe.client.youtube.YouTubeParams.Key.*;

@Component
public class VibeYouTube {

    private final YouTubeClient youTubeClient;

    public VibeYouTube(YouTubeClient youTubeClient) {
        this.youTubeClient = youTubeClient;
    }

    public List<YouTubeSearchResult> search(String query) throws GeneralSecurityException, IOException {
        return youTubeClient.search(defaultParams()
                .param(QUERY, query)
                .build())
                .getItems().stream()
                .map(YouTubeSearchResult::new)
                .collect(Collectors.toList());
    }

    private static YouTubeParams.YouTubeParamsBuilder defaultParams() {
        return YouTubeParams.builder()
                .param(MAX_RESULTS, 5L)
                .param(ORDER, "relevance")
                .param(TYPE, "video")
                .param(VIDEO_DEFINITION, "high");
    }
}
