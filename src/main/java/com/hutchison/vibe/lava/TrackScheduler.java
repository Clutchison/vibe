package com.hutchison.vibe.lava;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

@Log4j2
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private List<AudioTrack> queue;
    private int currentTrackIndex;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new ArrayList<>();
    }

    public void play(AudioTrack track) {
        player.playTrack(track);
    }

    public void start() {
        if(!queue.isEmpty()) {
            player.playTrack(queue.get(currentTrackIndex));
        }
    }

    public void queue(AudioTrack track) {
        queue.add(track);
        if(player.getPlayingTrack() == null) {
            player.startTrack(track, false);
        }
    }

    public String getLastQueuedTrackTitle() { return !queue.isEmpty() ? queue.get(queue.size() - 1).getInfo().title : null; }

    public String getCurrentTrackTitle() {
        return player.getPlayingTrack().getInfo().title;
    }

    public void togglePause() {
        player.setPaused(!player.isPaused());
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public boolean hasCurrentTrack() {
        return player.getPlayingTrack() != null;
    }

    public void clearQueue() {
        queue.clear();
        currentTrackIndex = 0;
    }

    public void stop() {
        player.stopTrack();
        currentTrackIndex = 0;
    }

    public boolean back() {
        if(currentTrackIndex > 0 && queue.size() > 1) {
            ListIterator<AudioTrack> it = queue.listIterator(currentTrackIndex);
            player.startTrack(it.previous(), false);
            return true;
        }
        return false;
    }

    public boolean skip() {
        if(currentTrackIndex < queue.size() - 1) {
            ListIterator<AudioTrack> it = queue.listIterator(currentTrackIndex + 1);
            if(it.hasNext()) {
                player.startTrack(it.next(), false);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        log.info("Player Paused Track");
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        log.info("Player Resumed Track");
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        log.info("Player Started Track");
        currentTrackIndex = queue.indexOf(track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        log.info("Track Ended: " + endReason.name());
        queue.set(currentTrackIndex, track.makeClone());
        ListIterator<AudioTrack> it = queue.listIterator(currentTrackIndex + 1);
        if(it.hasNext() && endReason.mayStartNext) {
            currentTrackIndex = it.nextIndex();
            player.playTrack(it.next());
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        log.error("Track Exception", exception);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        log.info("Track Stuck");
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        log.info("Track Stuck");
    }

    public String getQueueInfo() {
        if(queue.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, queue.size()).forEach(i -> {
            AudioTrackInfo trackInfo = queue.get(i).getInfo();
            if(currentTrackIndex == i) sb.append("*");
            sb.append(i);
            sb.append(". ");
            sb.append(trackInfo.title);
            sb.append(" - ");
            sb.append(trackInfo.author);
            sb.append("\n");
        });
        return sb.toString();
    }
}
