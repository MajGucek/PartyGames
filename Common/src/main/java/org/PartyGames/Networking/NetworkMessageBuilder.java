package org.PartyGames.Networking;

import com.google.gson.Gson;
import org.PartyGames.Shared.Games;

import java.text.ParseException;

public class NetworkMessageBuilder {
    private final NetworkMessage network_message;
    private static final Gson gson = new Gson();

    public NetworkMessageBuilder setMessageType(MessageType type) {
        network_message.setType(type);
        return this;
    }
    public NetworkMessageBuilder setText(String text) {
        network_message.setText(text);
        return this;
    }
    public NetworkMessageBuilder setGame(Games game) {
        network_message.setGame(game);
        return this;
    }
    public NetworkMessageBuilder setUUID(String uuid) {
        network_message.setUUID(uuid);
        return this;
    }
    public NetworkMessageBuilder setToBroadcast() {
        network_message.setUUID("broadcast");
        return this;
    }

    public static NetworkMessage parseNetworkMessage(String message) throws ParseException {
        NetworkMessage parsed = gson.fromJson(message, NetworkMessage.class);
        if (parsed != null) {
            return parsed;
        } else {
            throw new ParseException("Your String was not formatted correctly, kys!", 0);
        }
    }
    public static String parseString(NetworkMessage message) {
        return gson.toJson(message);
    }

    public String exportJSON() {
        return gson.toJson(network_message);
    }
    public NetworkMessage exportMessage() { return network_message; }
    public NetworkMessageBuilder() {
        network_message = new NetworkMessage();
    }
}
