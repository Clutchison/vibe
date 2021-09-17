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


    @Override
    public void onPlayerPause(AudioPlayer player) {
        log.info("Player Paused Track");
        super.onPlayerPause(player);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        log.info("Player Resumed Track");
        super.onPlayerResume(player);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        log.info("Player Started Track");
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        log.info("Track Ended: " + endReason.name());
        super.onTrackEnd(player, track, endReason);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        log.error("Track Exception", exception);
        super.onTrackException(player, track, exception);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        log.info("Track Stuck");
        super.onTrackStuck(player, track, thresholdMs);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        log.info("Track Stuck");
        super.onTrackStuck(player, track, thresholdMs, stackTrace);
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
