impl :: bincode :: Encode for ServerMessage
{
    fn encode < __E : :: bincode :: enc :: Encoder >
    (& self, encoder : & mut __E) ->core :: result :: Result < (), :: bincode
    :: error :: EncodeError >
    {
        :: bincode :: Encode :: encode(&self.message, encoder) ?; :: bincode
        :: Encode :: encode(&self.address, encoder) ?; :: bincode :: Encode ::
        encode(&self.data, encoder) ?; core :: result :: Result :: Ok(())
    }
}