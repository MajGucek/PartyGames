impl :: bincode :: Encode for ClientMessage
{
    fn encode < __E : :: bincode :: enc :: Encoder >
    (& self, encoder : & mut __E) ->core :: result :: Result < (), :: bincode
    :: error :: EncodeError >
    {
        :: bincode :: Encode :: encode(&self.message, encoder) ?; :: bincode
        :: Encode :: encode(&self.client_id, encoder) ?; :: bincode :: Encode
        :: encode(&self.event, encoder) ?; :: bincode :: Encode ::
        encode(&self.timestamp, encoder) ?; core :: result :: Result :: Ok(())
    }
}