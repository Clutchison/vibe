package com.hutchison.vibe.rest.controller;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.SwanJDA;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Log4j2
@RestController
public class VibeController {

    SwanJDA swanJDA;
    BotState botState;

    @Autowired
    public VibeController(SwanJDA swanJDA, BotState botState) {
        this.swanJDA = swanJDA;
        this.botState = botState;
    }

    @GetMapping("/{queueId}/test")
    @ResponseBody
    public void test(HttpServletRequest request, @PathVariable("queueId") String queueName) {
        Optional<VoiceChannel> botChannel = swanJDA.findBotChannel();
        Optional<TextChannel> textChannel = swanJDA.getTextChannel();
        botChannel.ifPresentOrElse(channel -> botState.loadQueueFromRest(queueName, channel, textChannel),
                () -> textChannel.ifPresent(txtChannel -> txtChannel.sendMessage("Vibe must be a voice channel.").queue()));
    }
}
