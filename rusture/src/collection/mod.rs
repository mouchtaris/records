fn _plus<R>(
    _x: u32,
    _y: u32,
    _success: &Fn(u32) -> R
)
    -> R
{
    _success(_x + _y)
}

fn _plus_async(
    _x: u32,
    _y: u32,
    _success: &Fn(u32),
    _failure: &Fn(String),
)
{
    let callb = |r: u32|
        if r > 10 {
            _success(r)
        }
        else {
            _failure(String::from("omg"))
        }
    ;
    _plus(_x, _y, &callb);
}

pub fn test()
{
}
