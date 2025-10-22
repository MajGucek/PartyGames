use shared::{BINCODE_CONFIG, ClientMessage};
use tokio::io::{AsyncReadExt, BufReader};
use tokio::net::TcpListener;

use bincode;
use uuid::Uuid;

#[tokio::main]
async fn main() -> Result<(), ()> {
    let addr = "127.0.0.1:8080".to_string();

    let listener = TcpListener::bind(&addr).await.unwrap();
    println!("Listening on: {}", addr);

    loop {
        let (socket, addr) = listener.accept().await.unwrap();
        println!("+1 User: {:?}", addr.ip());

        tokio::spawn(async move {
            let mut reader = BufReader::new(socket);

            loop {
                // Firstly we sent the size of the encoded message
                let len = match read_u32(&mut reader).await {
                    Ok(l) => l as usize,
                    Err(_) => {
                        panic!("Something went Wong!");
                    }
                };

                // Now we know how much to read
                let mut buf = vec![0u8; len];
                if let Err(_) = reader.read_exact(&mut buf).await {
                    println!("Connection closed while reading message");
                    return;
                }

                // Bincode desirialize
                match bincode::decode_from_slice::<ClientMessage, _>(&buf, BINCODE_CONFIG) {
                    Ok((msg, _bytes_read)) => {
                        println!(
                            "Received message from {:?}: {:?}",
                            Uuid::from_u128(msg.client_id),
                            msg.event
                        );
                        // here add to a queue for game logic that runs every x-timestep
                        panic!("Implement onward");
                    }
                    Err(_e) => {
                        println!("Decode Error");
                    }
                }
            }
        });
    }
}

async fn read_u32<R: AsyncReadExt + Unpin>(reader: &mut R) -> Result<u32, std::io::Error> {
    let mut buf = [0u8; 4];
    reader.read_exact(&mut buf).await?;
    Ok(u32::from_be_bytes(buf))
}
