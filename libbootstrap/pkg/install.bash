##
## Install a given package.
##
## Delegates to `_install_$pkg`.
##
## # Arguments
## * pkg    : package name
##
## # Example
##
##    _pkg__install pyenv
##
function _pkg__install () {
  local pkg="$1"; shift;

  "_install_${pkg}"
}
