use bincode::{Decode, Encode};

// This is the "Packet" that the server sends to Client-s.
#[derive(Encode, Decode, Debug)]
pub struct ServerMessage {
    pub message: String,     // sends over important messages, game-changes and such
    pub address: Recipient,  // who to send to player/broadcast
    pub data: WorldSnapshot, // Snapshot of the world state
}

#[derive(Encode, Decode, Debug)]
pub struct WorldSnapshot {
    pub players: Vec<(u128, GameData)>, // pairs of u128(Uuid) and the state of the player within a
    // specific game
    pub timestamp: u64,
}

// This is the enum that contains memory layout for each game

#[derive(Encode, Decode, Debug)]
pub enum GameData {
    Lobby {
        players: Vec<String>,
    },
    Snake {
        x: i32,
        y: i32,
        size: u32,
        direction: Direction,
    },
}

// Enum for defining direction, useful in many mini-games

#[derive(Encode, Decode, Debug)]
pub enum Direction {
    Up,
    Down,
    Left,
    Right,
}

// Pretty self explanatory

#[derive(Encode, Decode, Debug)]
pub enum Recipient {
    Client(u128),
    Broadcast,
}

// What the client sends to the server

#[derive(Encode, Decode, Debug)]
pub struct ClientMessage {
    pub message: String,
    pub client_id: u128,
    pub event: ClientEvent,
    pub timestamp: u64,
}

#[derive(Encode, Decode, Debug)]
pub enum ClientEvent {
    Connect { name: String },
    Disconnect,
    Input { action: InputAction },
}

#[derive(Encode, Decode, Debug)]
pub enum InputAction {
    Key { key: char },
    Word { word: String },
    Special { action: String },
}
