package org.PartyGames.Common.Networking;

import com.google.gson.Gson;
import jdk.jfr.BooleanFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;

/** The underlying data that gets sent over the WebSocket */
public class NetworkMessage {
    /** Object for JSON parsing */
    private static final Gson gson = new Gson();
    @NotNull private MessageType type;
    /** Send value with this */
    @Nullable private String data;
    /** Address */
    @Nullable private String address;

    /* Constructors */
    public NetworkMessage() {
        type = MessageType.Invalid;
        data = null;
        address = "broadcast";
    }
    /* --Constructors-- */

    /* Builder Pattern methods */
    @NotNull
    public NetworkMessage setMessageType(MessageType type) { this.type = type; return this; }
    @NotNull
    public MessageType getMessageType() {
        return type;
    }

    @NotNull
    public NetworkMessage setData(String text) { this.data = text; return this; }
    @Nullable
    public String getData() {
        return data;
    }

    @NotNull
    public NetworkMessage setAddress(String address) { this.address = address; return this; }
    @Nullable
    public String getAddress() { return this.address; }

    /** This function can be skipped, since by default address is broadcast */
    @NotNull
    public NetworkMessage setToBroadcast() { this.address = "broadcast"; return this; }
    @BooleanFlag
    public boolean isBroadcast() {
        assert address != null;
        return address.equalsIgnoreCase("broadcast");
    }
    /* --Builder Pattern methods-- */

    /* Import/Export */

    public static NetworkMessage fromString(String message) throws ParseException {
        NetworkMessage parsed = gson.fromJson(message, NetworkMessage.class);
        if (parsed != null) {
            return parsed;
        } else {
            throw new ParseException("Internal NetworkMessage String format error, please contact project author", 0);
        }
    }

    @NotNull
    @Override
    public String toString() {
        return gson.toJson(this);
    }
    /* --Import/Export-- */
}
