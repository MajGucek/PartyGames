package org.PartyGames.Client.Games.NullGame;

import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Common.Shared.Games;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NullGame extends GameClientController {
    protected static final Logger logger = LoggerFactory.getLogger(NullGame.class);
    public NullGame() {
        super();
        logger.warn("You've instantiated a NullGame Object, Beware!");
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
    public void handleGame(List<NetworkMessage> messages) {
        io_handler.drawText(2, 5, "Waiting for a Connection!", "rgb(255, 255, 255)");
        io_handler.render();
        logger.warn("You're handling the NullGame, Beware!");
    }

    @Override
    public Games getGame() {
        return Games.Null;
    }
}
