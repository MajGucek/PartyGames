#![deny(warnings)]

use color_eyre::Result;
use ratatui::{
    DefaultTerminal, Frame,
    crossterm::event::{self, Event, KeyCode, KeyEventKind},
    layout::Position,
    text::Text,
    widgets::Paragraph,
};

use bincode;
use shared::*;
use uuid::Uuid;

use tokio::{
    io::AsyncWriteExt,
    net::{TcpSocket, TcpStream},
};

#[tokio::main]
async fn main() -> Result<()> {
    color_eyre::install()?;
    let terminal = ratatui::init();
    let mut app = App::new();
    app.init().await;
    app.run(terminal).await;

    ratatui::restore();
    Ok(())
}

struct ServerConnection {
    address: String,
    uuid: u128,
    //game: GameData,
    stream: Option<TcpStream>,
}

impl ServerConnection {
    fn new() -> Self {
        Self {
            address: "127.0.0.1:8080".to_string(),
            uuid: Uuid::new_v4().as_u128(),
            stream: None,
        }
    }
    async fn connect(&mut self) -> std::io::Result<()> {
        let socket = TcpSocket::new_v4().unwrap();
        let socket_addr = self.address.parse().unwrap();
        self.stream = match socket.connect(socket_addr).await {
            Ok(tcp_stream) => Some(tcp_stream),
            Err(_e) => None,
        };

        Ok(())
    }

    async fn send(&mut self, client_message: ClientMessage) {
        let encoded = bincode::encode_to_vec(client_message, BINCODE_CONFIG).unwrap();
        let len = (encoded.len() as u32).to_be_bytes();

        // First send len
        self.stream.as_mut().unwrap().write_all(&len).await.unwrap();

        // Then send encoded
        self.stream
            .as_mut()
            .unwrap()
            .write_all(&encoded)
            .await
            .unwrap();
    }
}

struct App {
    input: String,
    input_mode: InputMode,
    character_index: usize,
    connection: ServerConnection,
}
enum InputMode {
    Typing,
    Navigating,
}

impl App {
    fn new() -> Self {
        Self {
            input: String::new(),
            input_mode: InputMode::Typing,
            character_index: 0,
            connection: ServerConnection::new(),
        }
    }

    async fn init(&mut self) {
        self.connection.connect().await.unwrap();
    }

    fn move_cursor_left(&mut self) {
        let cursor_moved_left = self.character_index.saturating_sub(1);
        self.character_index = self.clamp_cursor(cursor_moved_left);
    }

    fn move_cursor_right(&mut self) {
        let cursor_moved_right = self.character_index.saturating_add(1);
        self.character_index = self.clamp_cursor(cursor_moved_right);
    }

    fn enter_char(&mut self, new_char: char) {
        let index = self.byte_index();
        self.input.insert(index, new_char);
        self.move_cursor_right();
    }

    fn byte_index(&self) -> usize {
        self.input
            .char_indices()
            .map(|(i, _)| i)
            .nth(self.character_index)
            .unwrap_or(self.input.len())
    }

    fn delete_char(&mut self) {
        let is_not_cursor_leftmost = self.character_index != 0;
        if is_not_cursor_leftmost {
            let current_index = self.character_index;
            let from_left_to_current_index = current_index - 1;

            let before_char_to_delete = self.input.chars().take(from_left_to_current_index);

            let after_char_to_delete = self.input.chars().skip(current_index);

            self.input = before_char_to_delete.chain(after_char_to_delete).collect();
            self.move_cursor_left();
        }
    }

    fn clamp_cursor(&self, new_cursor_pos: usize) -> usize {
        new_cursor_pos.clamp(0, self.input.chars().count())
    }

    fn reset_cursor(&mut self) {
        self.character_index = 0;
    }

    async fn submit_message(&mut self) {
        // implement logic for submitting message
        self.connection
            .send(ClientMessage {
                message: "Pošiljam sporočilo".to_string(),
                client_id: self.connection.uuid,
                event: ClientEvent::Input {
                    action: InputAction::Word {
                        word: self.input.clone(),
                    },
                },
                timestamp: get_timestamp(),
            })
            .await;
        self.input.clear();
        self.reset_cursor();
    }

    async fn handle_press(&mut self, key: event::KeyEvent) {
        match key.code {
            KeyCode::Char(character) => {
                self.connection
                    .send(ClientMessage {
                        message: "Testno sporočilo!".to_string(),
                        client_id: self.connection.uuid,
                        event: (ClientEvent::Input {
                            action: InputAction::Key { key: character },
                        }),
                        timestamp: get_timestamp(),
                    })
                    .await
            }
            _ => {}
        }
    }

    async fn run(mut self, mut terminal: DefaultTerminal) {
        loop {
            terminal.draw(|frame| self.draw(frame)).unwrap();

            if let Event::Key(key) = event::read().unwrap() {
                match self.input_mode {
                    InputMode::Typing => {
                        if key.kind == KeyEventKind::Press {
                            match key.code {
                                KeyCode::Enter => self.submit_message().await,
                                KeyCode::Char(to_insert) => self.enter_char(to_insert),
                                KeyCode::Backspace => self.delete_char(),
                                KeyCode::Left => self.move_cursor_left(),
                                KeyCode::Right => self.move_cursor_right(),
                                KeyCode::Tab => self.input_mode = InputMode::Navigating,
                                KeyCode::Esc => return,
                                _ => {}
                            }
                        }
                    }
                    InputMode::Navigating => {
                        if key.kind == KeyEventKind::Press {
                            match key.code {
                                KeyCode::Tab => self.input_mode = InputMode::Typing,
                                KeyCode::Esc => return,
                                _ => {}
                            }
                            self.handle_press(key).await;
                        }
                    }
                }
            }
        }
    }

    fn draw(&self, frame: &mut Frame) {
        if matches!(self.connection.stream, None) {
            // render: Error connecting to the server
            let error_text = Paragraph::new(Text::from("Sorry, can't connect to the server!"));
            frame.render_widget(error_text, frame.area());
            return;
        }

        match self.input_mode {
            InputMode::Typing => {
                frame.set_cursor_position(Position::new(
                    frame.area().x + self.character_index as u16,
                    frame.area().y,
                ));
            }
            InputMode::Navigating => {}
        }

        // display word:
        let paragraph = Paragraph::new(Text::from(self.input.clone()));
        frame.render_widget(paragraph, frame.area());
    }
}
