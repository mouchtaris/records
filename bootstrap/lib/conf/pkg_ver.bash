##
## Get a version requirement for a package
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
function conf::pkg_ver() {
  local name="$1"; shift;
  local level="$1"; shift;

  local varname='_'"$name"'_REQUIRED_'"$level"
  local result="$( printenv "$varname" )"

  if [ -n "$result" ]
  then
    echo "$result"
  else
    ::err_and_die "$( printf 'No version requirement found for %s @%s (%s)' "$name" "$level" "$varname" )"
  fi
}
##
## Alias for version level MAJOR
function conf::pkg_ver_major() { conf::pkg_ver "$1" 'MAJOR'; }
##
## Alias for version level MINOR
function conf::pkg_ver_minor() { conf::pkg_ver "$1" 'MINOR'; }
##
## Alias for version "MAJOR MINOR"
function conf::pkg_ver_major_minor() {
  local name="$1"; shift;

  echo \
    "$( conf::pkg_ver_major "$name" )" \
    "$( conf::pkg_ver_minor "$name" )"
}
