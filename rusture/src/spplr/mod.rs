///
///
///
/// token_type = {
///     id = token_type@id
/// }
///
/// token = {
///     new = { type, value }
/// }
///
/// tokens = >>
///     token.new.{ token_type.id }."LOL" >>
///     token.new.{ type = token_type.id, value = "LAL" } >>|
///
///
///
///
///
///
/// point = {
///     x.y,
/// }
///
///
///
///
///
/// simplicity := {
///
///     x   := 3 + 4
///     y   := 6 / 2
///     add := a => b => a + b
///     z   := add x y
///
///     #mod #yolo vec2 := {
///         new     := { x := 0 y := 0 }
///         '+      := vec2< x1 y1 > => vec2< x2 y2 >                                   -- de-composition
///    --   with_x  := x'   => #mod vec2            => ...                                           -- ERROR!
///         with_x  := x'   => vec2                 => #yolo vec2.new { x := x2 ..vec2 }                      -- re-composition
///         with_x' := vec2 => #mod vec2< y1 := y > => vec2#yolo { x := vec2 ..{ y := y1 } }    -- #clarification
///
/// }
///
/// lex := {
/// ( stream<byte> => ... )
///
///
/// }
///
///
///
///
/// loo := path< elements >
///
