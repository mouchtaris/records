##
## Load the given environment and fail if loading fails.
##
function ::configure() {
  local env="$1"; shift

  conf::configure "$env" || {
    local msg="$( printf 'No such environemnt: %s' "$env" )"
    ::err_and_die "$msg"
  }
}
