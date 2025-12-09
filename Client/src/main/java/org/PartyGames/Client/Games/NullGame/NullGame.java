package org.PartyGames.Client.Games.NullGame;

import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Client.Sprites.Sprite;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.List;

@SuppressWarnings("unused")
public class NullGame extends GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(NullGame.class);
    private Sprite test;

    public NullGame() {
        super();
        logger.warn("You've instantiated a NullGame Object, Beware!");
        this.test = new Sprite("penguin");
        try {
            this.test.loadSprite();
        } catch (FileNotFoundException e) {
            logger.error("Couldn't load file: {}", String.valueOf(e));
        }
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void handleGame(List<NetworkMessage> messages, int tick) {
        if (test.isLoaded()) {
            logger.info("Width: {}", test.sprite.width);
        }
        io_controller.clearScreen();
        io_controller.drawText(2, 5, "Waiting for a Connection!", "rgb(255, 255, 255)");
        io_controller.render();
    }
}
