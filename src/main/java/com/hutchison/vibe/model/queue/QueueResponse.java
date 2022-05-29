package com.hutchison.vibe.model.queue;

import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Value
public class QueueResponse {

    MessageChannel channel;
    List<TrackLoadStatus> tracks;
    Map<String, TrackLoadStatus> trackMap;
    @NonFinal
    String messageId;

    static String CHECKMARK = "<:green_check:980559692113772584>";
    static String XMARK = "<:red_check:978502939851243530>";

    public QueueResponse(List<String> trackSearchIds, MessageChannel channel) {
        this.tracks = trackSearchIds.stream()
                .map(TrackLoadStatus::new)
                .collect(Collectors.toList())
                .stream()
                .distinct()
                .collect(Collectors.toList());
        trackMap = tracks.stream()
                .collect(Collectors.toMap(TrackLoadStatus::getSearchId, track -> track));
        this.channel = channel;
        updateMessage();
    }

    public void trackLoaded(String id, String track) {
        TrackLoadStatus trackLoadStatus = trackMap.get(id);
        if (trackLoadStatus != null) {
            trackLoadStatus.load(track);
            updateMessage();
        } else {
            System.out.println("No track in queue response for id: " + id);
        }
    }

    public void trackFailed(String id, String reason) {
        TrackLoadStatus trackLoadStatus = trackMap.get(id);
        if (trackLoadStatus != null) {
            trackLoadStatus.fail(reason);
        } else {
            System.out.println("No track in queue response for id: " + id);
        }
    }

    private void updateMessage() {
        String msg = buildMessage();
        if (messageId == null) {
            channel.sendMessage(msg).queue(message -> messageId = message.getId());
        } else {
            channel.editMessageById(messageId, msg).queue();
        }
    }

    private String buildMessage() {
        String header = getHeader().getMessage();
        StringBuilder sb = new StringBuilder(header + "\n");
        tracks.forEach(track -> {
            switch (track.getStatus()) {
                case LOADED:
                    sb.append(CHECKMARK)
                            .append(" ")
                            .append(track.getTrack());
                    break;
                case LOADING:
                    sb.append(track.getSearchId())
                            .append(" is loading...");
                    break;
                case FAILED:
                    sb.append(XMARK)
                            .append(track.getSearchId())
                            .append(" failed to load. Reason: ")
                            .append(track.getFailReason());
                    break;
            }
            sb.append("\n");
        });
        return sb.toString();
    }

    private HEADER getHeader() {
        if (tracks.stream().anyMatch(t -> t.getStatus().equals(TrackLoadStatus.Status.FAILED)))
            return HEADER.ERROR;

        return tracks.stream().allMatch(t -> t.getStatus().equals(TrackLoadStatus.Status.LOADED))
                ? HEADER.COMPLETE : HEADER.LOADING;
    }

    private enum HEADER {
        LOADING("***Loading Playlist...***"),
        ERROR("***Error loading a track***"),
        COMPLETE("***Load Complete***");

        @Getter
        private String message;

        HEADER(String message) {
            this.message = message;
        }
    }
}
