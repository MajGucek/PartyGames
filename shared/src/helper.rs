use bincode::config;

use std::time::{SystemTime, UNIX_EPOCH};

pub const BINCODE_CONFIG: config::Configuration = config::standard();

pub fn get_timestamp() -> u64 {
    SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_millis() as u64
}
