impl < __Context > :: bincode :: Decode < __Context > for ServerMessage
{
    fn decode < __D : :: bincode :: de :: Decoder < Context = __Context > >
    (decoder : & mut __D) ->core :: result :: Result < Self, :: bincode ::
    error :: DecodeError >
    {
        core :: result :: Result ::
        Ok(Self
        {
            message : :: bincode :: Decode :: decode(decoder) ?, address : ::
            bincode :: Decode :: decode(decoder) ?, data : :: bincode ::
            Decode :: decode(decoder) ?,
        })
    }
} impl < '__de, __Context > :: bincode :: BorrowDecode < '__de, __Context >
for ServerMessage
{
    fn borrow_decode < __D : :: bincode :: de :: BorrowDecoder < '__de,
    Context = __Context > > (decoder : & mut __D) ->core :: result :: Result <
    Self, :: bincode :: error :: DecodeError >
    {
        core :: result :: Result ::
        Ok(Self
        {
            message : :: bincode :: BorrowDecode ::< '_, __Context >::
            borrow_decode(decoder) ?, address : :: bincode :: BorrowDecode ::<
            '_, __Context >:: borrow_decode(decoder) ?, data : :: bincode ::
            BorrowDecode ::< '_, __Context >:: borrow_decode(decoder) ?,
        })
    }
}