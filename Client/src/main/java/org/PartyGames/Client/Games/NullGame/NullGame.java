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
    private Sprite wifi;
    private Sprite x;
    private TextColor.RGB border_color;

    public NullGame() {
        this.wifi = new Sprite();
        this.x = new Sprite();
        scheduler = new ScheduledServiceController();
        this.border_color = null;
    }

    @Override
    public void start() {
        super.start();
        scheduler.start(getTPS());
        logger.warn("You've instantiated a NullGame Object, Beware!");
        try {
            wifi.loadSprite("wifi");
            x.loadSprite("X");
        } catch (Exception e) {
            logger.error("Couldn't load sprite: {}", String.valueOf(e));
            System.exit(1);
        }


        scheduler.addService("X_advance_frame", () -> x.incrementFrame(), 3);
    }

    @Override
    public void stop() {
        super.stop();
    }



    @Override
    public void handleGame(List<NetworkMessage> messages, int tick) {
        io_controller.clearScreen();
        drawBorder();
        io_controller.drawSprite(
                wifi,
                io_controller.getHorizontalCenter() - wifi.getWidth() / 2,
                io_controller.getVerticalCenter() - wifi.getHeight() / 2
        );
        io_controller.drawSprite(
                x,
                (io_controller.getHorizontalCenter() - x.getWidth() / 2) + 21,
                (io_controller.getVerticalCenter() - x.getHeight() / 2) - 7,
                IOController.getRGB(255, 0, 0)
        );
        String connection_text = "Waiting for a Connection!";
        io_controller.drawText(
                connection_text,
                io_controller.getHorizontalCenter() - connection_text.length() / 2,
                io_controller.getVerticalCenter() + 15
        );
        io_controller.render();
        scheduler.executeServices(tick);
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
}
