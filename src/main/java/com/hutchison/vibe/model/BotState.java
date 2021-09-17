package com.hutchison.vibe.model;

import com.hutchison.vibe.jda.CommandMessage;
import com.hutchison.vibe.jda.VibeJDA;
import com.hutchison.vibe.lava.VibeAudioManager;
import com.hutchison.vibe.lava.handlers.VibeAudioLoadResultHandler;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                String id = commandMessage.getSubCommand() != null && !commandMessage.getSubCommand().isEmpty() ? commandMessage.getSubCommand() : "LpC0SKU6O00"; //Default to one of the best songs of all time!
                vibeAudioManager.loadItem(id, new VibeAudioLoadResultHandler(vibeAudioManager));
            } catch (InsufficientPermissionException ex) {
                event.getChannel().sendMessage("Vibe does not have permissions to join that channel.").queue();
            }
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
