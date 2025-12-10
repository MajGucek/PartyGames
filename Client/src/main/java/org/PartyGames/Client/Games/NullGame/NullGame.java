package org.PartyGames.Client.Games.NullGame;

import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Client.Sprites.Sprite;
import org.PartyGames.Client.Terminal.IOController;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.List;

@SuppressWarnings("unused")
public class NullGame extends GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(NullGame.class);
    private Sprite penguin;

    @Override
    public void start() {
        super.start();
        logger.warn("You've instantiated a NullGame Object, Beware!");
        penguin = new Sprite("penguin");
        try {
            penguin.loadSprite();
        } catch (FileNotFoundException e) {
            logger.error("File not found! {}", String.valueOf(e));
        }
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void handleGame(List<NetworkMessage> messages, int tick) {
        io_controller.clearScreen();
        io_controller.drawSprite(penguin, 0, 2, 6, IOController.getRGB(255, 255, 255));
        io_controller.drawText("Waiting for a Connection!", 2, 2);
        io_controller.render();
    }
}
