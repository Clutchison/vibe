package com.hutchison.vibe.model;

import com.hutchison.vibe.jda.VibeJDA;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BotState {

    private final VibeJDA jda;

    @Autowired
    public BotState(VibeJDA jda) {
        this.jda = jda;
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
