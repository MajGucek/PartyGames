package org.PartyGames.Client.Games.Lobby;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Lobby extends GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);
    private String unconfirmed_name;
    private String confirmed_name;
    private boolean has_sent_name;
    private boolean is_name_registered;
    private boolean was_name_denied;
    private boolean vote_start_games;

    public Lobby() {
        super();
        this.unconfirmed_name = "";
        this.confirmed_name = "";
        has_sent_name = false;
        is_name_registered = false;
        was_name_denied = false;
        vote_start_games = false;
    }

    @Override
    public void start() {
        super.start();
        logger.info("Entered Lobby!");
    }

    @Override
    public void stop() {
        super.stop();
        logger.info("Exiting Lobby!");
    }
    private void processServerMessages(List<NetworkMessage> server_messages) {
        if (server_messages == null) {
            io_handler.clearScreen();
            return;
        }
         for (NetworkMessage action : server_messages) {
             MessageType message_type = action.getMessageType();
             if (message_type.equals(MessageType.Invalid)) { continue; }

             switch (message_type) {
                 case MessageType.ClientName -> {
                     String data = action.getData();
                     if (data == null) { continue; }
                     // unconfirmed_name was accepted
                     logger.info("Name was accepted");
                     is_name_registered = true;
                     confirmed_name = data;
                 }
                 case MessageType.ClientError -> {
                     logger.info("Name was denied!");
                     is_name_registered = false;
                     unconfirmed_name = "";
                     was_name_denied = true;
                 }
             }
        }
    }

    @Override
    public void handleGame(List<NetworkMessage> messages) {
        processServerMessages(messages);
        processIO();
        io_handler.render();
    }

    private void processIO() {
        io_handler.clearScreen();
        KeyStroke input = io_handler.poll();
        StringBuilder string_builder = new StringBuilder(unconfirmed_name);
        if (input != null) {
            switch (input.getKeyType()) {
                case KeyType.Character -> {
                    if (!has_sent_name) {
                        string_builder.append(input.getCharacter());
                        unconfirmed_name = string_builder.toString();
                    }
                    if (is_name_registered) {
                        NetworkMessage vote = new NetworkMessage();

                        vote.setAddress(uuid).setMessageType(MessageType.ClientEvent);

                        if (input.getCharacter().equals('y')) {
                            vote.setData("Y");

                            connection.send(vote);

                            vote_start_games = true;
                        } else if (input.getCharacter().equals('n')) {
                            vote.setData("N");
                            connection.send(vote);
                            vote_start_games = false;
                        }
                    }
                }
                case KeyType.Backspace -> {
                    if (!has_sent_name) {
                        if (!string_builder.isEmpty()) {
                            string_builder.deleteCharAt(string_builder.length() - 1);
                            unconfirmed_name = string_builder.toString();
                        }
                    }
                }
                case KeyType.Enter -> {
                    if (input.getKeyType() == KeyType.Enter) {
                        if (!has_sent_name) {
                            NetworkMessage message = new NetworkMessage();
                            message.setAddress(uuid).setMessageType(MessageType.ClientName);
                            has_sent_name = true;
                            message.setData(unconfirmed_name);
                            connection.send(message);
                            logger.info("Sent: {}", message);
                        }
                        if (was_name_denied) {
                            unconfirmed_name = "";
                            has_sent_name = false;
                            was_name_denied = false;
                            is_name_registered = false;
                        }
                    }
                }
            }
        }

        if (has_sent_name && !is_name_registered) {
            if (was_name_denied) {
                io_handler.drawText(0, 5, "Your name was denied!", "rgb(255, 0, 0)");
                io_handler.drawText(0, 6, "Press Enter to re-enter name", "rgb(255, 255, 255)");
            } else {
                io_handler.drawText(0, 5, "You have sent Name: "
                        + unconfirmed_name +
                        ", Waiting for response", "rgb(255, 0, 255)");
            }

        } else if (!has_sent_name && !is_name_registered) {
            io_handler.drawText(0, 5, "Input Name: " + unconfirmed_name, "rgb(255, 125, 255)");
        }

        if (is_name_registered && !confirmed_name.isEmpty()) {
            io_handler.drawText(4, 2, "Your Name: " + confirmed_name, "rgb(0, 255, 0)");
            io_handler.drawText(4, 3, "Press y/n to vote to start/remove your vote", "rgb(255, 255, 255)");
            if (vote_start_games) {
                io_handler.drawText(11, 4, "Yes", "rgb(0, 0, 255)");
            } else {
                io_handler.drawText(11, 4, "No", "rgb(255, 0, 0)");
            }
        }
    }
}
