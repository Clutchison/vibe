package com.hutchison.vibe.model.youtube;

import com.google.api.services.youtube.model.SearchResult;
import lombok.Getter;

@Getter
public class YouTubeSearchResult {
    private final String name;
    private final String id;

    // .map(item -> new YouTubeSearchResult(item.getSnippet().getTitle(), item.getId().getVideoId()))

    public YouTubeSearchResult(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public YouTubeSearchResult(SearchResult item) {
        this.name = item.getSnippet().getTitle();
        this.id = item.getId().getVideoId();
    }

    @Override
    public String toString() {
        return name + ": " + id;
    }
}
