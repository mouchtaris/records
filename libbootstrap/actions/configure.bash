##
## Load the given environment and fail if loading fails.
##
function _configure() {
  local env="$1"; shift

  {
    _conf__load '_common' &&
    _conf__load "$env" &&
    export _CONF__ENV="$env"
  } ||
    _err_and_die "$( printf 'No such environemnt: %s' "$env" )"
}
