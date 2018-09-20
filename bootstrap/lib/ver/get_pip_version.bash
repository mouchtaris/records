##
## Extract the version from a pip package
##
## The version is echoed as
##
##    MAJOR MINOR
##
function ver::get_pip_version () {
  local pkg="$1"; shift;

  pip show "$pkg" |
    sed -r -n -e '/.*Version: ([[:digit:]]+)\.([[:digit:]]+).*/s//\1 \2/p'
}
