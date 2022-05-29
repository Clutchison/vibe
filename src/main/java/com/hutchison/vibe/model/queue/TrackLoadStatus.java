package com.hutchison.vibe.model.queue;

import lombok.Value;
import lombok.experimental.NonFinal;

import static com.hutchison.vibe.model.queue.TrackLoadStatus.Status.*;

@Value
public class TrackLoadStatus {

    String searchId;
    @NonFinal
    String track;
    @NonFinal
    Status status;
    @NonFinal
    String failReason;

    public TrackLoadStatus(String searchId) {
        this.searchId = searchId;
        this.status = LOADING;
        this.track = "";
    }

    public TrackLoadStatus(String searchId, Status loaded) {
        this.searchId = searchId;
        this.status = loaded;
        this.track = "";
    }

    public void load(String track) {
        if (status == LOADING) {
            status = LOADED;
            this.track = track;
        }
    }

    public void fail(String reason) {
        if (status == LOADING) {
            status = FAILED;
            this.failReason = reason;
        }
    }

    public enum Status {
        LOADING, LOADED, FAILED
    }
}
