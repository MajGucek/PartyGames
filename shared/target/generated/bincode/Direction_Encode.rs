impl :: bincode :: Encode for Direction
{
    fn encode < __E : :: bincode :: enc :: Encoder >
    (& self, encoder : & mut __E) ->core :: result :: Result < (), :: bincode
    :: error :: EncodeError >
    {
        match self
        {
            Self ::Up
            =>{
                < u32 as :: bincode :: Encode >:: encode(& (0u32), encoder) ?
                ; core :: result :: Result :: Ok(())
            }, Self ::Down
            =>{
                < u32 as :: bincode :: Encode >:: encode(& (1u32), encoder) ?
                ; core :: result :: Result :: Ok(())
            }, Self ::Left
            =>{
                < u32 as :: bincode :: Encode >:: encode(& (2u32), encoder) ?
                ; core :: result :: Result :: Ok(())
            }, Self ::Right
            =>{
                < u32 as :: bincode :: Encode >:: encode(& (3u32), encoder) ?
                ; core :: result :: Result :: Ok(())
            },
        }
    }
}