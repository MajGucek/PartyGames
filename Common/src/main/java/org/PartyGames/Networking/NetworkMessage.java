package org.PartyGames.Networking;

import org.PartyGames.Shared.Games;

public class NetworkMessage {
    private MessageType type;
    private String text;
    private Games game;
    public NetworkMessage() {
        type = MessageType.Invalid;
        text = "";
        game = Games.Null;
    }
    public NetworkMessage(MessageType type, String text, Games game) {
        this.type = type;
        this.text = text;
        this.game = game;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
    public MessageType getType() {
        return type;
    }

    public void setText(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }

    public void setGame(Games game) {this.game = game; }
    public Games getGame() { return this.game; }
}
