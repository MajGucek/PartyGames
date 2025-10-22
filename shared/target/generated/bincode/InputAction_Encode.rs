impl :: bincode :: Encode for InputAction
{
    fn encode < __E : :: bincode :: enc :: Encoder >
    (& self, encoder : & mut __E) ->core :: result :: Result < (), :: bincode
    :: error :: EncodeError >
    {
        match self
        {
            Self ::Key { key }
            =>{
                < u32 as :: bincode :: Encode >:: encode(& (0u32), encoder) ?
                ; :: bincode :: Encode :: encode(key, encoder) ?; core ::
                result :: Result :: Ok(())
            }, Self ::Word { word }
            =>{
                < u32 as :: bincode :: Encode >:: encode(& (1u32), encoder) ?
                ; :: bincode :: Encode :: encode(word, encoder) ?; core ::
                result :: Result :: Ok(())
            }, Self ::Special { action }
            =>{
                < u32 as :: bincode :: Encode >:: encode(& (2u32), encoder) ?
                ; :: bincode :: Encode :: encode(action, encoder) ?; core ::
                result :: Result :: Ok(())
            },
        }
    }
}