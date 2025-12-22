package org.PartyGames.Server.Games.BombChaser;

import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.PartyGames.Server.Games.GameServerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unused")
public class BombChaser extends GameServerController {
    private static final Logger logger = LoggerFactory.getLogger(BombChaser.class);

    private final int world_width;
    private final int world_height;
    private Map<String, Player> players;

    public BombChaser() {
        super();
        world_width = 700;
        world_height = 200;
        this.players = null;
    }

    @Override
    public void start() {
        super.start();

        Random r = new Random();
        for (var entry : client_names.entrySet()) {
            // Create Player for each client
            Player p = new Player(r.nextInt(world_width), r.nextInt(world_height));
            players.put(entry.getValue(), p);
            // Send to client the player
            NetworkMessage message = new NetworkMessage();
            message
                    .setAddress(entry.getKey())
                    .setMessageType(MessageType.ClientEvent)
                    .setFilter("Spawn")
                    .setData(p.toString());
            connection.send(message);

        }
    }

    @Override
    public void handleGame(List<NetworkMessage> messages, int tick) {

        logger.info("Bomb chaser! tick: {}", tick);
    }
}
