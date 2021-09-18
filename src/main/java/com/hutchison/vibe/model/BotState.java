package com.hutchison.vibe.model;

import com.hutchison.vibe.lava.TrackScheduler;
import com.hutchison.vibe.lava.VibeAudioManager;
import com.hutchison.vibe.lava.handlers.VibeAudioLoadResultHandler;
import com.hutchison.vibe.swan.jda.CommandMessage;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.Future;

@Component
@Log4j2
public class BotState {

    private final VibeAudioManager vibeAudioManager;

    @Autowired
    public BotState(VibeAudioManager vibeAudioManager) {
        this.vibeAudioManager = vibeAudioManager;
    }

    public void join(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            event.getChannel().sendMessage("Joining channel...").queue();
            AudioManager audioManager = event.getGuild().getAudioManager();
            try {
                audioManager.openAudioConnection(channel.get());
            } catch (InsufficientPermissionException ex) {
                event.getChannel().sendMessage("Vibe does not have permissions to join that channel.").queue();
            }
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void disconnect(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.closeAudioConnection();
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void play(CommandMessage commandMessage, MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            AudioManager audioManager = event.getGuild().getAudioManager();
            try {
                audioManager.openAudioConnection(channel.get());
                audioManager.setSendingHandler(vibeAudioManager.getVibeAudioSendHandler());
                String id = !commandMessage.getArgs().isEmpty() ? commandMessage.getArgs().get(0) : "LpC0SKU6O00"; //Default to one of the best songs of all time!
                Future<Void> loaded = vibeAudioManager.loadItem(id, new VibeAudioLoadResultHandler(vibeAudioManager));
                //This while ensures that the given track is loaded when the scheduler attempts to send the Track Title in the response.
                Instant maxTimeToWait = Instant.now().plus(10, ChronoUnit.MINUTES);
                while (!loaded.isDone()) {
                    if (Instant.now().isAfter(maxTimeToWait)) break;
                }
                event.getChannel().sendMessage("Playing " + vibeAudioManager.getTrackScheduler().getCurrentTrackTitle()).queue();
            } catch (InsufficientPermissionException ex) {
                event.getChannel().sendMessage("Vibe does not have permissions to join that channel.").queue();
            }
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void stop(CommandMessage commandMessage, MessageReceivedEvent event) {
        if (vibeAudioManager.getTrackScheduler().hasCurrentTrack()) {
            vibeAudioManager.getTrackScheduler().stop();
        } else {
            event.getChannel().sendMessage("No track is playing to stop.").queue();
        }
    }

    public void pause(CommandMessage commandMessage, MessageReceivedEvent event) {
        TrackScheduler scheduler = vibeAudioManager.getTrackScheduler();
        if (!scheduler.hasCurrentTrack()) return;
        if (scheduler.isPaused()) return;
        scheduler.togglePause();
    }

    public void resume(CommandMessage commandMessage, MessageReceivedEvent event) {
        TrackScheduler scheduler = vibeAudioManager.getTrackScheduler();
        if (!scheduler.hasCurrentTrack()) return;
        if (!scheduler.isPaused()) return;
        scheduler.togglePause();
    }

    private Optional<VoiceChannel> getChannel(MessageReceivedEvent event) {
        if (event.getMember() == null ||
                event.getMember().getVoiceState() == null ||
                event.getMember().getVoiceState().getChannel() == null) {
            return Optional.empty();
        } else {
            return Optional.of(event.getMember().getVoiceState().getChannel());
        }
    }
}
