impl :: bincode :: Encode for WorldSnapshot
{
    fn encode < __E : :: bincode :: enc :: Encoder >
    (& self, encoder : & mut __E) ->core :: result :: Result < (), :: bincode
    :: error :: EncodeError >
    {
        :: bincode :: Encode :: encode(&self.players, encoder) ?; :: bincode
        :: Encode :: encode(&self.timestamp, encoder) ?; core :: result ::
        Result :: Ok(())
    }
}