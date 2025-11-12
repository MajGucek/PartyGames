package org.PartyGames.Games.Meteor;

import org.PartyGames.Games.GameHandler;
import org.PartyGames.Games.GameStrategy;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.ServerHandlers.WebSocketServerHandler;
import org.PartyGames.Shared.Games;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MeteorGame extends GameStrategy  {
    private static final Logger logger = LoggerFactory.getLogger(MeteorGame.class);

    public MeteorGame(WebSocketServerHandler connection) {
        super(connection);
    }

    @Override
    public void start() {
        logger.info("Entering MeteorGame");
    }

    @Override
    public void handleGame(List<NetworkMessage> messages) {
        ///logger.info("Handling MeteorGame");
    }

    @Override
    public Games getGame() {
        return Games.Meteor;
    }
}
