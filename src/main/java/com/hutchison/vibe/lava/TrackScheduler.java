package com.hutchison.vibe.lava;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TrackScheduler extends AudioEventAdapter {

    private AudioPlayer player;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
    }

    public void play(AudioTrack track) {
        logTrackInfo(track.getInfo());
        player.playTrack(track);
    }

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
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        log.info("Track Ended: " + endReason.name());
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

    private void logTrackInfo(AudioTrackInfo info) {
        log.info("Audio Track Details: ");
        log.info(info.author);
        log.info(info.title);
        log.info(info.identifier);
        log.info(info.uri);
        log.info(info.isStream);
        log.info(info.length);
    }
}
