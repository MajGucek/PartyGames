impl < __Context > :: bincode :: Decode < __Context > for InputAction
{
    fn decode < __D : :: bincode :: de :: Decoder < Context = __Context > >
    (decoder : & mut __D) ->core :: result :: Result < Self, :: bincode ::
    error :: DecodeError >
    {
        let variant_index = < u32 as :: bincode :: Decode ::< __D :: Context
        >>:: decode(decoder) ?; match variant_index
        {
            0u32 =>core :: result :: Result ::
            Ok(Self ::Key
            {
                key : :: bincode :: Decode ::< __D :: Context >::
                decode(decoder) ?,
            }), 1u32 =>core :: result :: Result ::
            Ok(Self ::Word
            {
                word : :: bincode :: Decode ::< __D :: Context >::
                decode(decoder) ?,
            }), 2u32 =>core :: result :: Result ::
            Ok(Self ::Special
            {
                action : :: bincode :: Decode ::< __D :: Context >::
                decode(decoder) ?,
            }), variant =>core :: result :: Result ::
            Err(:: bincode :: error :: DecodeError :: UnexpectedVariant
            {
                found : variant, type_name : "InputAction", allowed : &::
                bincode :: error :: AllowedEnumVariants :: Range
                { min: 0, max: 2 }
            })
        }
    }
} impl < '__de, __Context > :: bincode :: BorrowDecode < '__de, __Context >
for InputAction
{
    fn borrow_decode < __D : :: bincode :: de :: BorrowDecoder < '__de,
    Context = __Context > > (decoder : & mut __D) ->core :: result :: Result <
    Self, :: bincode :: error :: DecodeError >
    {
        let variant_index = < u32 as :: bincode :: Decode ::< __D :: Context
        >>:: decode(decoder) ?; match variant_index
        {
            0u32 =>core :: result :: Result ::
            Ok(Self ::Key
            {
                key : :: bincode :: BorrowDecode ::< __D :: Context >::
                borrow_decode(decoder) ?,
            }), 1u32 =>core :: result :: Result ::
            Ok(Self ::Word
            {
                word : :: bincode :: BorrowDecode ::< __D :: Context >::
                borrow_decode(decoder) ?,
            }), 2u32 =>core :: result :: Result ::
            Ok(Self ::Special
            {
                action : :: bincode :: BorrowDecode ::< __D :: Context >::
                borrow_decode(decoder) ?,
            }), variant =>core :: result :: Result ::
            Err(:: bincode :: error :: DecodeError :: UnexpectedVariant
            {
                found : variant, type_name : "InputAction", allowed : &::
                bincode :: error :: AllowedEnumVariants :: Range
                { min: 0, max: 2 }
            })
        }
    }
}