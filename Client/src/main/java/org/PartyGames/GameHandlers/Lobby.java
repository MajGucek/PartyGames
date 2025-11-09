package org.PartyGames.GameHandlers;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.Networking.MessageType;
import org.PartyGames.Networking.NetworkMessage;
import org.PartyGames.Networking.NetworkMessageBuilder;
import org.PartyGames.Shared.Games;
import org.PartyGames.Terminal.TerminalIOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Lobby extends GameHandler {
    private String name;
    private boolean has_sent_name;
    protected static final Logger logger = LoggerFactory.getLogger(Lobby.class);
    public Lobby(TerminalIOHandler io_handler, WebSocketConnectionHandler connection) {
        super(io_handler, connection);
        this.name = "";
        has_sent_name = false;
    }

    @Override
    public void startGame() {
        logger.info("Entered Lobby!");
    }

    @Override
    public void stopGame() {
        logger.info("Exiting Lobby!");
    }

    @Override
    public void handleGame(List<NetworkMessage> messages) {


        KeyStroke inputs = io_handler.poll();
        io_handler.clearScreen();

        NetworkMessageBuilder builder = new NetworkMessageBuilder();
        builder.setMessageType(MessageType.PlayerEvent);

        StringBuilder string_builder = new StringBuilder(name);
        String input = String.valueOf(inputs.getCharacter());
        if (inputs.getKeyType() == KeyType.Character) {
            if (!has_sent_name) {
                string_builder.append(inputs.getCharacter());
                name = string_builder.toString();
            }
        } else {
            if (inputs.getKeyType() == KeyType.Backspace) {
                if (!has_sent_name) {
                    if (!string_builder.isEmpty()) {
                        string_builder.deleteCharAt(string_builder.length() - 1);
                        name = string_builder.toString();
                    }
                }
            }
            if (inputs.getKeyType() == KeyType.Enter) {
                if (!has_sent_name) {
                    has_sent_name = true;
                    builder.setText(name);
                    connection.send(builder.exportJSON());
                    logger.info("Sent: {}", builder.exportJSON());
                }
            }
        }
        logger.info(input);

        if (has_sent_name) {
            io_handler.drawText(0, 6, "You have sent name: " + name, "rgb(0, 255, 0)");
        } else {
            io_handler.drawText(0, 5, "Input name: " + name, "rgb(255, 125, 255)");
        }

        /*
        for (NetworkMessage message : messages) {
            logger.info("{}, {}", message.getType().toString(), message.getText());
        }
         */

        io_handler.render();

    }

    @Override
    public Games getGame() {
        return Games.Lobby;
    }
}
