impl < __Context > :: bincode :: Decode < __Context > for GameData
{
    fn decode < __D : :: bincode :: de :: Decoder < Context = __Context > >
    (decoder : & mut __D) ->core :: result :: Result < Self, :: bincode ::
    error :: DecodeError >
    {
        let variant_index = < u32 as :: bincode :: Decode ::< __D :: Context
        >>:: decode(decoder) ?; match variant_index
        {
            0u32 =>core :: result :: Result ::
            Ok(Self ::Lobby
            {
                players : :: bincode :: Decode ::< __D :: Context >::
                decode(decoder) ?,
            }), 1u32 =>core :: result :: Result ::
            Ok(Self ::Snake
            {
                x : :: bincode :: Decode ::< __D :: Context >::
                decode(decoder) ?, y : :: bincode :: Decode ::< __D :: Context
                >:: decode(decoder) ?, size : :: bincode :: Decode ::< __D ::
                Context >:: decode(decoder) ?, direction : :: bincode ::
                Decode ::< __D :: Context >:: decode(decoder) ?,
            }), variant =>core :: result :: Result ::
            Err(:: bincode :: error :: DecodeError :: UnexpectedVariant
            {
                found : variant, type_name : "GameData", allowed : &:: bincode
                :: error :: AllowedEnumVariants :: Range { min: 0, max: 1 }
            })
        }
    }
} impl < '__de, __Context > :: bincode :: BorrowDecode < '__de, __Context >
for GameData
{
    fn borrow_decode < __D : :: bincode :: de :: BorrowDecoder < '__de,
    Context = __Context > > (decoder : & mut __D) ->core :: result :: Result <
    Self, :: bincode :: error :: DecodeError >
    {
        let variant_index = < u32 as :: bincode :: Decode ::< __D :: Context
        >>:: decode(decoder) ?; match variant_index
        {
            0u32 =>core :: result :: Result ::
            Ok(Self ::Lobby
            {
                players : :: bincode :: BorrowDecode ::< __D :: Context >::
                borrow_decode(decoder) ?,
            }), 1u32 =>core :: result :: Result ::
            Ok(Self ::Snake
            {
                x : :: bincode :: BorrowDecode ::< __D :: Context >::
                borrow_decode(decoder) ?, y : :: bincode :: BorrowDecode ::<
                __D :: Context >:: borrow_decode(decoder) ?, size : :: bincode
                :: BorrowDecode ::< __D :: Context >:: borrow_decode(decoder)
                ?, direction : :: bincode :: BorrowDecode ::< __D :: Context
                >:: borrow_decode(decoder) ?,
            }), variant =>core :: result :: Result ::
            Err(:: bincode :: error :: DecodeError :: UnexpectedVariant
            {
                found : variant, type_name : "GameData", allowed : &:: bincode
                :: error :: AllowedEnumVariants :: Range { min: 0, max: 1 }
            })
        }
    }
}