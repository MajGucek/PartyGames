package org.PartyGames.Networking;

public enum MessageType {
    NetworkStatus,
    GameStatus,
    NewGame,
    ClientStatus,
    //
    ClientEvent,
    ClientError,
    //
    ClientName,
    ClientDisconnect,
    ClientUUID,
    Error,
    Invalid,
}
