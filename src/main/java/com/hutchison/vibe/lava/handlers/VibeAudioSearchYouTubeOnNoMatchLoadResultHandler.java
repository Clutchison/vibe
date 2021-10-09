package com.hutchison.vibe.lava.handlers;

import com.hutchison.vibe.client.youtube.VibeYouTube;
import com.hutchison.vibe.lava.VibeAudioManager;
import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.model.youtube.YouTubeSearchResult;
import com.hutchison.vibe.swan.jda.CommandMessage;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Log4j2
public class VibeAudioSearchYouTubeOnNoMatchLoadResultHandler extends VibeAudioLoadResultHandler {

    private final CommandMessage commandMessage;
    private final MessageReceivedEvent event;
    private final VibeYouTube vibeYouTube;
    private final String id;

    public VibeAudioSearchYouTubeOnNoMatchLoadResultHandler(VibeAudioManager manager,
                                                            MessageReceivedEvent event,
                                                            CommandMessage commandMessage,
                                                            VibeYouTube vibeYouTube,
                                                            String id) {
        super(manager, event);
        this.commandMessage = commandMessage;
        this.event = event;
        this.vibeYouTube = vibeYouTube;
        this.id = id;
    }

    @Override
    public void noMatches() {
        log.info("No matches for query. Trying to search youtube instead.");
        try {
            List<YouTubeSearchResult> search = vibeYouTube.search(id);
            if (search.size() < 1) {
                event.getChannel().sendMessage("No search results for query: " + id).queue();
            } else {
                event.getChannel().sendMessage("Loading " + search.get(0).getName() + "...").queue();
                try {
                    manager.loadItem(search.get(0).getId(),
                            new VibeAudioLoadResultHandler(manager, event)).get();
                } catch (InterruptedException | ExecutionException e) {
                    event.getChannel().sendMessage("Something went wrong playing the track.").queue();
                }
            }
        } catch (GeneralSecurityException | IOException e) {
            event.getChannel().sendMessage("Unable to search youtube with query: " + id).queue();
        }
    }
}
