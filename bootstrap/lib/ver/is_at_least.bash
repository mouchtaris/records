##
## Compare versions
##
## v1j.v1m >= v2j.v2m
##
## # Arguments
## * v1j    : version 1 major
## * v1m    : version 1 minor
## * v2j    : version 2 major
## * v2m    : version 2 minor
##
## # Example
##
##    _is_version_at_least $v_maj $v_min 2 1 || ::install_pkg $smth
##
function ver::is_at_least() {
  local v1j="$1"; shift
  local v1m="$1"; shift
  local v2j="$1"; shift
  local v2m="$1"; shift

  # Fail if some argument not provided
  [ -z "$v1j" -o -z "$v1m" -o -z "$v2j" -o -z "$v2m" ] &&
    return 1

  [[
      $((v1j)) > $((v2j))
    ||
        $((v1j)) == $((v2j))
      &&
      (   $((v1m)) > $((v2m))
        ||
          $((v1m)) == $((v2m)) )
  ]] || return 1
}
