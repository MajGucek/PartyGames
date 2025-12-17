package org.PartyGames.Client.Games.NullGame;

import com.googlecode.lanterna.TextColor;
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
    private Sprite penguin;
    private Sprite dancer;
    private TextColor.RGB border_color;

    public NullGame() {
        this.penguin = new Sprite();
        this.dancer = new Sprite();
        scheduler = new ScheduledServiceController();
        this.border_color = null;
    }

    @Override
    public void start() {
        super.start();
        logger.warn("You've instantiated a NullGame Object, Beware!");
        try {
            penguin.loadSprite("penguin");
            dancer.loadSprite("dancer");
        } catch (Exception e) {
            logger.error("Couldn't load sprite: {}", String.valueOf(e));
            System.exit(1);
        }

        scheduler.start(getTPS());
        scheduler.addService("animate_dancer", () -> dancer.incrementFrame(), 10);
    }

    @Override
    public void stop() {
        super.stop();
    }

    private void drawBorder() {
        String e = "@";
        for (int i = 0; i < io_controller.getCharWidth(); i++) {
            io_controller.drawText(e, i, 0);
            io_controller.drawText(e, i, io_controller.getCharHeight() - 1);
        }
        for (int i = 0; i < io_controller.getCharHeight(); i++) {
            io_controller.drawText(e, 0, i);
            io_controller.drawText(e, io_controller.getCharWidth() - 1, i);
        }


    }

    @Override
    public void handleGame(List<NetworkMessage> messages, int tick) {
        io_controller.clearScreen();
        drawBorder();
        io_controller.drawSprite(penguin, 2, 6, IOController.getRGB(255, 255, 255));
        io_controller.drawSprite(dancer, 20, 6);
        io_controller.drawText("Waiting for a Connection!", 2, 2);
        io_controller.render();
        scheduler.executeServices(tick);
    }
}
