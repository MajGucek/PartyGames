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
    /** Use this for extra filtering */
    @Nullable private String extra_filter;
    /** Address */
    @Nullable private String address;

    /** Creates a NetworkMessage Object, by default the address is broadcast.
     * If Type isn't set the Client/Server will discard the Message! */
    public NetworkMessage() {
        type = MessageType.Invalid;
        data = null;
        address = "broadcast";
        extra_filter = "";
    }
    /* --Constructors-- */

    /* Builder Pattern methods */
    /** Use this field for any extra filtering of your own choice. */
    @NotNull
    public NetworkMessage setFilter(String filter) { extra_filter = filter; return this; }
    @Nullable
    public String getFilter() { return extra_filter; }

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
    /** Tries to convert: String -> NetworkMessage {@link NetworkMessage}
     * {@throws ParseException} */
    public static NetworkMessage fromString(String message) throws ParseException {
        NetworkMessage parsed = gson.fromJson(message, NetworkMessage.class);
        if (parsed != null) {
            return parsed;
        } else {
            throw new ParseException("Internal NetworkMessage String format error, please contact project author", 0);
        }
    }

    /** Tries to convert: NetworkMessage -> String
     * Should never fail*/
    @NotNull
    @Override
    public String toString() {
        return gson.toJson(this);
    }
    /* --Import/Export-- */
}
