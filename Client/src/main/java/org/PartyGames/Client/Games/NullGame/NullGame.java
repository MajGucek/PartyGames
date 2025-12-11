package org.PartyGames.Client.Games.NullGame;

import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Client.Sprites.Sprite;
import org.PartyGames.Client.Terminal.IOController;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Common.Scheduler.ScheduledServiceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unused")
public class NullGame extends GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(NullGame.class);
    private ScheduledServiceController scheduler;

    @Override
    public void start() {
        super.start();
        logger.warn("You've instantiated a NullGame Object, Beware!");

        this.scheduler = new ScheduledServiceController(super.TPS);
        scheduler.addService("Remained", () -> logger.warn("Handling NullGame"), 1);
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void handleGame(List<NetworkMessage> messages, int tick) {
        io_controller.clearScreen();
        io_controller.drawSprite(penguin, 2, 6, IOController.getRGB(255, 255, 255));
        io_controller.drawSprite(dancer, 20, 6);
        io_controller.drawText("Waiting for a Connection!", 2, 2);
        io_controller.render();
        scheduler.executeServices(tick);
    }
}
