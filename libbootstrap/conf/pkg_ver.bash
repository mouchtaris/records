
##
## from configuration.
##
## Version is described by a package name
## and a version level.
##
## Package name + version level respond to a
## specific variable set through config.
##
##    _${package}_REQUIRED_${level}
##
function _conf__pkg_ver() {
  local name="$1"; shift;
  local level="$1"; shift;

  local varname='_'"$name"'_REQUIRED_'"$level"
  local result="$( printenv "$varname" )"

  if [ -n "$result" ]
  then
    echo "$result"
  else
    _err_and_die "$( printf 'No version requirement found for %s @%s (%s)' "$name" "$level" "$varname" )"
  fi
}
##
## Alias for version level MAJOR
function _conf__pkg_ver_major() { _conf__pkg_ver "$1" 'MAJOR'; }
##
## Alias for version level MINOR
function _conf__pkg_ver_minor() { _conf__pkg_ver "$1" 'MINOR'; }
##
## Alias for version "MAJOR MINOR"
function _conf__pkg_ver_major_minor() {
  local name="$1"; shift;

  echo \
    "$( _conf__pkg_ver_major "$name" )" \
    "$( _conf__pkg_ver_minor "$name" )"
}
