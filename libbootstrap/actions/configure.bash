##
## Load the given environment and fail if loading fails.
##
function _configure() {
  local env="$1"; shift

  _conf__configure "$env" || {
    local msg="$( printf 'No such environemnt: %s' "$env" )"
    _err_and_die "$msg"
  }
}
