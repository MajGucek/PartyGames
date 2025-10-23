use bincode::config;
use tokio::{io::Join, task};

use std::{
    net::TcpStream,
    thread::JoinHandle,
    time::{SystemTime, UNIX_EPOCH},
};

pub const BINCODE_CONFIG: config::Configuration = config::standard();

pub fn get_timestamp() -> u64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_millis() as u64
}

pub struct NetworkHandler {
    pub thread: JoinHandle<TcpStream>,
}

impl NetworkHandler {
    pub fn connect() {
        let join = tokio::spawn(async {});
    }
}
