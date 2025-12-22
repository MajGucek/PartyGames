package org.PartyGames.Server.Games.BombChaser;

import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Server.Games.GameServerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unused")
public class BombChaser extends GameServerController {
    private static final Logger logger = LoggerFactory.getLogger(BombChaser.class);

    @Override
    public void start() {
        super.start();

        for (var entry : client_names.entrySet()) {
            // Create Player for each client
        }
    }

    @Override
    public void handleGame(List<NetworkMessage> messages, int tick) {

        logger.info("Bomb chaser! tick: {}", tick);
    }
}
