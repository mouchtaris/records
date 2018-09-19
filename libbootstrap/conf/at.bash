##
## Evaluate an expression in the given environment
function _conf__at() {
  local env="$1"; shift;

  (
    _conf__configure "$env" &&
      eval "$@"
  )
}
