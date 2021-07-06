package de.conveyor.server;

import de.conveyor.game.Character;
import de.conveyor.game.Game;
import de.conveyor.game.Player;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.UUID;

@Controller
public class CharacterController {

    @MessageMapping("/character")
    public Character getCharacterDate(UUID uuid) throws Exception {
        System.out.println(uuid);
        Thread.sleep(1000); // simulated delay
        return new Game().getPlayer(uuid).getCharacter();// TODO replace with actual game
    }
}
