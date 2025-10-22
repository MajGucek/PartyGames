impl :: bincode :: Encode for GameData
{
    fn encode < __E : :: bincode :: enc :: Encoder >
    (& self, encoder : & mut __E) ->core :: result :: Result < (), :: bincode
    :: error :: EncodeError >
    {
        match self
        {
            Self ::Lobby { players }
            =>{
                < u32 as :: bincode :: Encode >:: encode(& (0u32), encoder) ?
                ; :: bincode :: Encode :: encode(players, encoder) ?; core ::
                result :: Result :: Ok(())
            }, Self ::Snake { x, y, size, direction }
            =>{
                < u32 as :: bincode :: Encode >:: encode(& (1u32), encoder) ?
                ; :: bincode :: Encode :: encode(x, encoder) ?; :: bincode ::
                Encode :: encode(y, encoder) ?; :: bincode :: Encode ::
                encode(size, encoder) ?; :: bincode :: Encode ::
                encode(direction, encoder) ?; core :: result :: Result ::
                Ok(())
            },
        }
    }
}