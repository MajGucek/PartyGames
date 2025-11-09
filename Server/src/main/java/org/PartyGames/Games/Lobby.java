package org.PartyGames.Games;

import org.PartyGames.Networking.MessageType;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.ServerHandlers.*;
import org.PartyGames.Shared.Games;
import org.java_websocket.WebSocket;

import java.util.List;
import java.util.Map;



public class Lobby extends GameStrategy {
    public Lobby(WebSocketServerHandler connection) {
        super(connection);
    }

    @Override
    public void start() {
        logger.info("Now in Lobby state");
    }

    @Override
    public void handleGame() {
        Map<WebSocket, List<NetworkMessage>> messages = connection.consumeMessages();
        for (Map.Entry<WebSocket, List<NetworkMessage>> client_messages : messages.entrySet()) {
            WebSocket client = client_messages.getKey();
            List<NetworkMessage> actions = client_messages.getValue();
            for (NetworkMessage action : actions) {
                if (action.getType() == MessageType.PlayerEvent) {
                    logger.info("{}, with event: {}", client.getRemoteSocketAddress(), action.getText());
                }
            }
        }

    }

    @Override
    public void stop() {
        super.stop();
        /* Below is extra behavior, you don't need to overload stop, unless you need extra cleanup */
        logger.info("Exiting Lobby state");
    }

    @Override
    public Games getGame() {
        /* Make sure to register your game in Common:org.PartyGames.Shared.Games */
        return Games.Lobby;
    }
}
