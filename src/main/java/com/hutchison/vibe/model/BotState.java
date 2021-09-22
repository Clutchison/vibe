package com.hutchison.vibe.model;

import com.hutchison.vibe.lava.LoopStatus;
import com.hutchison.vibe.lava.TrackScheduler;
import com.hutchison.vibe.lava.VibeAudioManager;
import com.hutchison.vibe.lava.handlers.VibeAudioLoadResultHandler;
import com.hutchison.vibe.model.entity.SavedQueue;
import com.hutchison.vibe.service.SavedQueueService;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Log4j2
public class BotState {

    private final VibeAudioManager vibeAudioManager;
    private final SavedQueueService savedQueueService;

    @Autowired
    public BotState(VibeAudioManager vibeAudioManager, SavedQueueService savedQueueService) {
        this.vibeAudioManager = vibeAudioManager;
        this.savedQueueService = savedQueueService;
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
            clearQueue(event, true);
            vibeAudioManager.getTrackScheduler().setLoopStatus(LoopStatus.OFF);
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
                event.getChannel().sendMessage("Queued " + vibeAudioManager.getTrackScheduler().getLastQueuedTrackTitle()).queue();
            } catch (InsufficientPermissionException ex) {
                event.getChannel().sendMessage("Vibe does not have permissions to join that channel.").queue();
            }
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void setLoop(CommandMessage commandMessage, MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            LoopStatus loopStatus = LoopStatus.valueOf(commandMessage.getSubCommand().toUpperCase());
            vibeAudioManager.getTrackScheduler().setLoopStatus(loopStatus);
            event.getChannel().sendMessage("Loop set to " + StringUtils.capitalize(commandMessage.getSubCommand())).queue();
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void start(CommandMessage commandMessage, MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            AudioManager audioManager = event.getGuild().getAudioManager();
            try {
                audioManager.openAudioConnection(channel.get());
                audioManager.setSendingHandler(vibeAudioManager.getVibeAudioSendHandler());
                vibeAudioManager.getTrackScheduler().start();
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

    private void clearQueue(MessageReceivedEvent event, boolean silent) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            vibeAudioManager.getTrackScheduler().clearQueue();
            if(!silent) event.getChannel().sendMessage("Queue cleared Successfully.").queue();
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void clearQueue(MessageReceivedEvent event) {
        clearQueue(event, false);
    }

    public void sendQueueInfo(CommandMessage commandMessage, MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        String queueInfo = vibeAudioManager.getTrackScheduler().getQueueInfo();
        if (channel.isPresent() && queueInfo != null) {
            event.getChannel().sendMessage(queueInfo).queue();
        } else {
            event.getChannel().sendMessage("Queue is already empty.").queue();
        }
    }

    public void back(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && vibeAudioManager.getTrackScheduler().back()) {
            event.getChannel().sendMessage("Playing " + vibeAudioManager.getTrackScheduler().getCurrentTrackTitle()).queue();
        } else {
            event.getChannel().sendMessage("Could not go back in queue.").queue();
        }
    }

    public void skip(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && vibeAudioManager.getTrackScheduler().skip()) {
            event.getChannel().sendMessage("Playing " + vibeAudioManager.getTrackScheduler().getCurrentTrackTitle()).queue();
        } else {
            event.getChannel().sendMessage("Could not skip in queue.").queue();
        }
    }

    public void jump(int trackIndex, MessageReceivedEvent event) {
        TrackScheduler trackScheduler = vibeAudioManager.getTrackScheduler();
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && (trackIndex <= (trackScheduler.getQueue().size() - 1))) {
            trackScheduler.jump(trackIndex);
            event.getChannel().sendMessage("Playing " + trackScheduler.getCurrentTrackTitle()).queue();
        } else {
            event.getChannel().sendMessage("Could not jump in queue.").queue();
        }
    }

    public void remove(int trackIndex, MessageReceivedEvent event) {
        TrackScheduler trackScheduler = vibeAudioManager.getTrackScheduler();
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && (trackIndex <= (trackScheduler.getQueue().size() - 1))) {
            trackScheduler.remove(trackIndex);
            if(trackScheduler.hasCurrentTrack()) {
                event.getChannel().sendMessage("Playing " + trackScheduler.getCurrentTrackTitle()).queue();
            }
        } else {
            event.getChannel().sendMessage("Could not jump in queue.").queue();
        }
    }

    public void saveCurrentQueue(String queueName, MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && vibeAudioManager.getTrackScheduler().hasCurrentTrack()) {
            String username = event.getMember().getUser().getName();
            List<AudioTrack> queue = vibeAudioManager.getTrackScheduler().getQueue();
            savedQueueService.createQueue(queueName, username, queue);
            event.getChannel().sendMessage("Queue of name " + queueName + " saved.").queue();
        } else {
            event.getChannel().sendMessage("Could not save queue.").queue();
        }
    }

    public void loadQueue(String queueName, MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            TrackScheduler trackScheduler = vibeAudioManager.getTrackScheduler();
            trackScheduler.stop();
            trackScheduler.clearQueue();

            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(channel.get());
            audioManager.setSendingHandler(vibeAudioManager.getVibeAudioSendHandler());

            String username = event.getMember().getUser().getName();
            SavedQueue savedQueue = savedQueueService.getSavedQueue(queueName, username);

            List<Future<Void>> loaded = savedQueue.getTracks().stream()
                    .map(t -> vibeAudioManager.loadItemOrdered("key", t.getLoadId(), new VibeAudioLoadResultHandler(vibeAudioManager)))
                    .collect(Collectors.toList());

            Instant maxTimeToWait = Instant.now().plus(10, ChronoUnit.MINUTES);
            while(!loaded.stream().allMatch(Future::isDone)) {
                if (Instant.now().isAfter(maxTimeToWait)) break;
            }

            event.getChannel().sendMessage("Queue of name " + queueName + " loaded.").queue();
        } else {
            event.getChannel().sendMessage("Could not save queue.").queue();
        }
    }

    public void updateSavedQueue(String queueName, String username, MessageReceivedEvent event) {
        List<AudioTrack> tracks = vibeAudioManager.getTrackScheduler().getQueue();
        if(savedQueueService.updateSavedQueue(queueName, username, tracks)) {
            event.getChannel().sendMessage("Sucessfully updated queue of name " + queueName + ".").queue();
        }
        else {
            event.getChannel().sendMessage("Failed to update queue of name " + queueName + ".").queue();
        }
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
