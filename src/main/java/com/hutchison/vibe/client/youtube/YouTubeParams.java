package com.hutchison.vibe.client.youtube;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

@Builder
class YouTubeParams {

    @Getter
    private final Map<Key, Object> paramMap;

    private YouTubeParams(Map<Key, Object> paramMap) {
        this.paramMap = paramMap;
    }

    enum Key {
        MAX_RESULTS("maxResults"),
        ORDER("order"),
        QUERY("q"),
        TYPE("type"),
        VIDEO_DEFINITION("videoDefinition"),
        ;

        @Getter
        private final String keyValue;

        Key(String keyValue) {
            this.keyValue = keyValue;
        }
    }

    public static class YouTubeParamsBuilder {

        private final Map<Key, Object> map;

        YouTubeParamsBuilder() {
            map = new HashMap<>();
        }

        private Map<Key, Object> paramMap(Map<Key, Object> map) {
            throw new UnsupportedOperationException();
        }

        public YouTubeParamsBuilder param(Key key, Object s) {
            if (key != null && !ObjectUtils.isEmpty(s)) {
                map.put(key, s);
            }
            return this;
        }

        public YouTubeParams build() {
            return new YouTubeParams(map);
        }

        public String toString() {
            return "YouTubeParams.YouTubeParamsBuilder()";
        }
    }
}
