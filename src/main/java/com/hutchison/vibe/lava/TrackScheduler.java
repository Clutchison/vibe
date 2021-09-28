package com.hutchison.vibe.lava;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private int currentTrackIndex;
    private List<AudioTrack> original;

    @Getter
    private List<AudioTrack> queue;
    @Setter
    private LoopStatus loopStatus;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new ArrayList<>();
        this.original = new ArrayList<>();
        this.loopStatus = LoopStatus.OFF;
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

    public void shuffleQueue() {
        //Only set original if it is empty, this allows the queue to be shuffled multiple times and still retain original order
        original = original.isEmpty() ? new ArrayList<>(queue) : original;
        //If a track is playing, Remove current track before shuffling to ensure current track doesn't end up later in the queue
        if(player.getPlayingTrack() != null) {
            AudioTrack currentTrack = queue.remove(currentTrackIndex);
            Collections.shuffle(queue);
            queue.add(0, currentTrack);
            currentTrackIndex = 0;
        }
        else {
            Collections.shuffle(queue);
        }
    }

    public void shuffleOff() {
        //Check if any new tracks have been queued since shuffle began, if so pop them onto end of list
        List<AudioTrack> newTracks = getAnyNewTracks();
        queue = new ArrayList<>(original);
        if(!newTracks.isEmpty()) {
            queue.addAll(newTracks);
        }
        original.clear();
        currentTrackIndex = queue.indexOf(player.getPlayingTrack());
    }

    private List<AudioTrack> getAnyNewTracks() {
        List<String> orginalIds = original.stream().map(AudioTrack::getIdentifier).collect(Collectors.toList());
        return queue.stream().filter(t -> !orginalIds.contains(t.getIdentifier())).collect(Collectors.toList());
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
                return true;
            }
        }
        return false;
    }

    public boolean jump(int trackIndex) {
        ListIterator<AudioTrack> it = queue.listIterator(trackIndex);
        if(it.hasNext()) {
            player.startTrack(it.next(), false);
            return true;
        }
        return false;
    }

    public void remove(int trackIndex) {
        if(trackIndex > currentTrackIndex) {
            queue.remove(trackIndex);
        }
        else if(trackIndex < currentTrackIndex) {
            queue.remove(trackIndex);
            currentTrackIndex--;
        }
        else {
            player.stopTrack();
            queue.remove(trackIndex);
            start();
        }
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
        AudioTrack clone = track.makeClone();
        queue.set(currentTrackIndex, clone);

        if(endReason.mayStartNext) {
            ListIterator<AudioTrack> it;
            switch (loopStatus) {
                case TRACK:
                    player.playTrack(clone);
                    break;
                case QUEUE:
                    it = queue.listIterator(currentTrackIndex + 1);
                    if(!it.hasNext()) {
                        it = queue.listIterator();
                        if(it.hasNext()) {
                            currentTrackIndex = it.nextIndex();
                            player.playTrack(it.next());
                        }
                    } else {
                        currentTrackIndex = it.nextIndex();
                        player.playTrack(it.next());
                    }
                    break;
                case OFF:
                    it = queue.listIterator(currentTrackIndex + 1);
                    if(it.hasNext()) {
                        currentTrackIndex = it.nextIndex();
                        player.playTrack(it.next());
                    } else {
                        currentTrackIndex = 0;
                    }
                    break;
            }
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
            sb.append(i + 1);
            sb.append(". Title: ");
            sb.append(trackInfo.title);
            sb.append(", Author: ");
            sb.append(trackInfo.author);
            sb.append("\n");
        });
        return sb.toString();
    }
}
