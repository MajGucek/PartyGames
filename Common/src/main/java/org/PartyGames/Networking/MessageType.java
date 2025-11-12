package org.PartyGames.Networking;

/** All the Types of Messages that can be sent over the WebSocket */
public enum MessageType {
    NetworkStatus,
    GameStatus,
    NewGame,
    ClientStatus,
    /** The Value that should be used for all Client Events (keys, requests) */
    ClientEvent,
    /** The Value that should be used for All the Errors */
    ClientError,
    ClientName,
    ClientDisconnect,
    ClientUUID,
    Invalid,
}
