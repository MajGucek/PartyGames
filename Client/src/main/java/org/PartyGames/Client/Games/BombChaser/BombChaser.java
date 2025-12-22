package org.PartyGames.Client.Games.BombChaser;

import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unused")
public class BombChaser extends GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(BombChaser.class);

    @Override
    public void handleGame(List<NetworkMessage> messages, int tick) {
        logger.info("Bomb chaser");

    }
}
