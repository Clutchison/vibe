package com.hutchison.vibe.model.bot;

import com.hutchison.vibe.client.youtube.VibeYouTube;
import com.hutchison.vibe.exception.UnauthorizedException;
import com.hutchison.vibe.lava.LoopStatus;
import com.hutchison.vibe.lava.TrackScheduler;
import com.hutchison.vibe.lava.VibeAudioManager;
import com.hutchison.vibe.lava.handlers.VibeAudioLoadResultHandler;
import com.hutchison.vibe.model.entity.SavedQueue;
import com.hutchison.vibe.model.queue.QueueResponse;
import com.hutchison.vibe.model.youtube.YouTubeSearchResult;
import com.hutchison.vibe.service.SavedQueueService;
import com.hutchison.vibe.spotify.SpotifyLinkConverter;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Log4j2
public class Bot {

    private final VibeAudioManager vibeAudioManager;
    private final SavedQueueService savedQueueService;
    private final VibeYouTube vibeYouTube;
    private final SpotifyLinkConverter spotifyLinkConverter;

    public Bot(VibeAudioManager vibeAudioManager,
               SavedQueueService savedQueueService,
               VibeYouTube vibeYouTube,
               SpotifyLinkConverter spotifyLinkConverter) {
        this.vibeAudioManager = vibeAudioManager;
        this.savedQueueService = savedQueueService;
        this.vibeYouTube = vibeYouTube;
        this.spotifyLinkConverter = spotifyLinkConverter;
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
        if (channel.isEmpty()) {
            event.getChannel().sendMessage("User not in voice channel").queue();
            return;
        }
        AudioManager audioManager = event.getGuild().getAudioManager();
        try {
            audioManager.openAudioConnection(channel.get());
            audioManager.setSendingHandler(vibeAudioManager.getVibeAudioSendHandler());
            List<String> ids = calculateIds(commandMessage);
            if (ids.size() == 1) {
                queueSingleTrack(ids.get(0), event);
            } else {
                queueMultipleTracks(ids, event);
            }
        } catch (InsufficientPermissionException ex) {
            event.getChannel().sendMessage("Vibe does not have permissions to join that channel.").queue();
        }
    }

    private void queueSingleTrack(String id, MessageReceivedEvent event) {
        VibeAudioLoadResultHandler resultHandler = new VibeAudioLoadResultHandler(vibeAudioManager, event);
        Future<Void> loaded = vibeAudioManager.loadItem(id, resultHandler);
        Instant maxTimeToWait = Instant.now().plus(10, ChronoUnit.SECONDS);
        while (!loaded.isDone()) {
            if (Instant.now().isAfter(maxTimeToWait)) break;
        }
        if (resultHandler.isNoMatches()) {
            try {
                List<YouTubeSearchResult> search = vibeYouTube.search(id);
                if (search.size() < 1) {
                    event.getChannel().sendMessage("No search results for query: " + id).queue();
                } else {
                    event.getChannel().sendMessage("Loading " + search.get(0).getName() + "...").queue();
                    String newId = search.get(0).getId();
                    try {
                        vibeAudioManager.loadItem(newId,
                                new VibeAudioLoadResultHandler(vibeAudioManager, event)).get();
                    } catch (InterruptedException | ExecutionException e) {
                        event.getChannel().sendMessage("Something went wrong playing the track: " + newId).queue();
                    }
                }
            } catch (GeneralSecurityException | IOException e) {
                event.getChannel().sendMessage("Unable to search youtube with query: " + id).queue();
            }
        }
    }

    private void queueMultipleTracks(List<String> ids, MessageReceivedEvent event) {
        QueueResponse queueResponse = new QueueResponse(ids, event.getChannel());
        ids.forEach(id -> {
            try {
                List<YouTubeSearchResult> youtubeSearchResult = vibeYouTube.search(id);
                if (youtubeSearchResult.size() < 1) {
                    queueResponse.trackFailed(id, "No search results for query.");
                } else {
                    String newId = youtubeSearchResult.get(0).getId();
                    try {
                        vibeAudioManager.loadItem(newId,
                                        new VibeAudioLoadResultHandler(vibeAudioManager,
                                                event,
                                                queueResponse,
                                                id))
                                .get();
                    } catch (InterruptedException | ExecutionException e) {
                        queueResponse.trackFailed(id, "Something went wrong playing the track: ");
                    }
                }
            } catch (GeneralSecurityException | IOException e) {
                queueResponse.trackFailed(id, "Unable to search youtube with query. ");
            }
        });
    }

    @NotNull
    private List<String> calculateIds(CommandMessage commandMessage) {
        String link = String.join(" ", commandMessage.getArgs());
        if (SpotifyLinkConverter.isSpotifyLink(link)) {
            try {
                return spotifyLinkConverter.convert(link);
            } catch (ParseException | SpotifyWebApiException | IOException e) {
                e.printStackTrace();
            }
            return Collections.singletonList(
                    commandMessage.getArgs().isEmpty() ? "LpC0SKU6O00" : link);
        } else {
            return Collections.singletonList(
                    commandMessage.getArgs().isEmpty() ? "LpC0SKU6O00" : link);
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

    public void shuffle(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            vibeAudioManager.getTrackScheduler().shuffleQueue();
            event.getChannel().sendMessage("Shuffle on.").queue();
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void shuffleOff(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            vibeAudioManager.getTrackScheduler().shuffleOff();
            event.getChannel().sendMessage("Shuffle off.").queue();
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void start(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            AudioManager audioManager = event.getGuild().getAudioManager();
            try {
                audioManager.openAudioConnection(channel.get());
                audioManager.setSendingHandler(vibeAudioManager.getVibeAudioSendHandler());
                vibeAudioManager.getTrackScheduler().start();
                event.getChannel().sendMessage(
                        playingTrackText(vibeAudioManager.getTrackScheduler())).queue();
            } catch (InsufficientPermissionException ex) {
                event.getChannel().sendMessage("Vibe does not have permissions to join that channel.").queue();
            }
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void stop(MessageReceivedEvent event) {
        if (vibeAudioManager.getTrackScheduler().hasCurrentTrack()) {
            vibeAudioManager.getTrackScheduler().stop();
        } else {
            event.getChannel().sendMessage("No track is playing to stop.").queue();
        }
    }

    public void pause() {
        TrackScheduler scheduler = vibeAudioManager.getTrackScheduler();
        if (!scheduler.hasCurrentTrack()) return;
        if (scheduler.isPaused()) return;
        scheduler.togglePause();
    }

    public void resume() {
        TrackScheduler scheduler = vibeAudioManager.getTrackScheduler();
        if (!scheduler.hasCurrentTrack()) return;
        if (!scheduler.isPaused()) return;
        scheduler.togglePause();
    }

    private void clearQueue(MessageReceivedEvent event, boolean silent) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            vibeAudioManager.getTrackScheduler().clearQueue();
            if (!silent) event.getChannel().sendMessage("Queue cleared Successfully.").queue();
        } else {
            event.getChannel().sendMessage("User not in voice channel").queue();
        }
    }

    public void clearQueue(MessageReceivedEvent event) {
        clearQueue(event, false);
    }

    public void sendQueueInfo(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        List<String> queueInfo = vibeAudioManager.getTrackScheduler().getQueueInfo();
        if (channel.isPresent() && queueInfo != null) {
            queueInfo.forEach(qi -> event.getChannel().sendMessage(qi).queue());
        } else {
            event.getChannel().sendMessage("Queue is already empty.").queue();
        }
    }

    public void back(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && vibeAudioManager.getTrackScheduler().back()) {
            event.getChannel().sendMessage(
                    playingTrackText(vibeAudioManager.getTrackScheduler())).queue();
        } else {
            event.getChannel().sendMessage("Could not go back in queue.").queue();
        }
    }

    public void skip(MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && vibeAudioManager.getTrackScheduler().skip()) {
            event.getChannel().sendMessage(
                    playingTrackText(vibeAudioManager.getTrackScheduler())).queue();
        } else {
            event.getChannel().sendMessage("Could not skip in queue.").queue();
        }
    }

    public void jump(int trackIndex, MessageReceivedEvent event) {
        TrackScheduler trackScheduler = vibeAudioManager.getTrackScheduler();
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && (trackIndex <= (trackScheduler.getQueue().size() - 1))) {
            trackScheduler.jump(trackIndex);
            event.getChannel().sendMessage(
                    playingTrackText(trackScheduler)).queue();
        } else {
            event.getChannel().sendMessage("Could not jump in queue.").queue();
        }
    }

    public void remove(int trackIndex, MessageReceivedEvent event) {
        TrackScheduler trackScheduler = vibeAudioManager.getTrackScheduler();
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && (trackIndex <= (trackScheduler.getQueue().size() - 1))) {
            trackScheduler.remove(trackIndex);
            if (trackScheduler.hasCurrentTrack()) {
                event.getChannel().sendMessage(playingTrackText(trackScheduler)).queue();
            }
        } else {
            event.getChannel().sendMessage("Could not jump in queue.").queue();
        }
    }

    public void saveCurrentQueue(String queueName, MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent() && vibeAudioManager.getTrackScheduler().hasCurrentTrack()) {
            User user = event.getMember().getUser();
            List<AudioTrack> queue = vibeAudioManager.getTrackScheduler().getQueue();
            savedQueueService.createQueue(queueName, user, queue);
            event.getChannel().sendMessage("Queue of name " + queueName + " saved.").queue();
        } else {
            event.getChannel().sendMessage("Could not save queue.").queue();
        }
    }

    @Transactional
    public void loadQueue(String queueName, MessageReceivedEvent event) {
        Optional<VoiceChannel> channel = getChannel(event);
        if (channel.isPresent()) {
            TrackScheduler trackScheduler = vibeAudioManager.getTrackScheduler();
            trackScheduler.stop();
            trackScheduler.clearQueue();

            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.openAudioConnection(channel.get());
            audioManager.setSendingHandler(vibeAudioManager.getVibeAudioSendHandler());

            Long userId = event.getMember().getUser().getIdLong();
            try {
                SavedQueue savedQueue = savedQueueService.getSavedQueue(queueName, userId);

                List<Future<Void>> loaded = savedQueue.getTracks().stream()
                        .map(t -> vibeAudioManager.loadItemOrdered("key", t.getLoadId(),
                                new VibeAudioLoadResultHandler(vibeAudioManager, event)))
                        .collect(Collectors.toList());
                Instant maxTimeToWait = Instant.now().plus(10, ChronoUnit.MINUTES);
                while (!loaded.stream().allMatch(Future::isDone)) {
                    if (Instant.now().isAfter(maxTimeToWait)) break;
                }

                event.getChannel().sendMessage("Queue of name " + queueName + " loaded.").queue();
            } catch (UnauthorizedException e) {
                event.getChannel().sendMessage("You do not have permission to load queue \"" + queueName + "\"").queue();
            }
        } else {
            event.getChannel().sendMessage("Could not save queue.").queue();
        }
    }

    public void updateSavedQueue(String queueName, Long ownerId, MessageReceivedEvent event) {
        List<AudioTrack> tracks = vibeAudioManager.getTrackScheduler().getQueue();
        try {
            savedQueueService.updateSavedQueue(queueName, ownerId, tracks);
            event.getChannel().sendMessage("Queue \"" + queueName + "\" updated.").queue();
        } catch (UnauthorizedException e) {
            event.getChannel().sendMessage("You do not have permission to update queue \"" + queueName + "\"").queue();
        }
    }

    public static Optional<VoiceChannel> getChannel(MessageReceivedEvent event) {
        if (event.getMember() == null ||
                event.getMember().getVoiceState() == null ||
                event.getMember().getVoiceState().getChannel() == null) {
            return Optional.empty();
        } else {
            return Optional.of(event.getMember().getVoiceState().getChannel());
        }
    }

    private String playingTrackText(TrackScheduler trackScheduler) {
        return trackScheduler.getCurrentTrackTitle().orElse("No track playing.");
    }
}
