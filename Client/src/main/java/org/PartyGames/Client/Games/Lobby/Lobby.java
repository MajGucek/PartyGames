package org.PartyGames.Client.Games.Lobby;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Client.Terminal.IOController;
import org.PartyGames.Common.Networking.MessageType;
import org.PartyGames.Common.Networking.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class Lobby extends GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);
    private String unconfirmed_name;
    private String confirmed_name;
    private boolean has_sent_name;
    private boolean is_name_registered;
    private boolean was_name_denied;
    private boolean vote_start_games;

    @Override
    public void start() {
        super.start();
        this.unconfirmed_name = "";
        this.confirmed_name = "";
        this.has_sent_name = false;
        this.is_name_registered = false;
        this.was_name_denied = false;
        this.vote_start_games = false;
        logger.info("Entered Lobby!");
    }

    @Override
    public void stop() {
        super.stop();
        logger.info("Exiting Lobby!");
    }
    private void processServerMessages(List<NetworkMessage> server_messages) {
        if (server_messages == null) {
            io_controller.clearScreen();
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
                default -> { }
            }
        }
    }

    @Override
    public void handleGame(List<NetworkMessage> messages, int tick) {
        processServerMessages(messages);
        processIO();
        io_controller.render();
    }

    private void processIO() {
        io_controller.clearScreen();
        Optional<KeyStroke> input_opt = io_controller.poll();
        StringBuilder string_builder = new StringBuilder(unconfirmed_name);
        if (input_opt.isPresent()) {
            KeyStroke input = input_opt.get();
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
                default -> { }
            }
        }

        if (has_sent_name && !is_name_registered) {
            if (was_name_denied) {
                io_controller.drawText("Your name was denied!", 0, 5, IOController.getRGB(255, 0, 0));
                io_controller.drawText("Press Enter to re-enter name", 0, 6);
            } else {
                io_controller.drawText("You have sent Name: "
                        + unconfirmed_name +
                        ", Waiting for response", 0, 5, IOController.getRGB(255, 0, 255));
            }

        } else if (!has_sent_name && !is_name_registered) {
            io_controller.drawText("Input Name: " + unconfirmed_name, 0, 5, IOController.getRGB(255, 125, 255));
        }

        if (is_name_registered && !confirmed_name.isEmpty()) {
            io_controller.drawText("Your Name: " + confirmed_name, 4, 2, IOController.getRGB(0, 255, 0));
            io_controller.drawText("Press y/n to vote to start/remove your vote", 4, 3);
            if (vote_start_games) {
                io_controller.drawText("Yes", 11, 4, IOController.getRGB(0, 0, 255));
            } else {
                io_controller.drawText("No", 11, 4, IOController.getRGB(255, 0, 0));
            }
        }
    }
}
