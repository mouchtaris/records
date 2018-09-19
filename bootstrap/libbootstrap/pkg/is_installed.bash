##
## Make an ad-hoc test about whether something is
## sufficiently installed.
##
## Delegates to `_is_$pkg_installed`
##
## # Arguments
## * pkg    : package name
##
## # Example
##
##    _pkg__is_installed pyenv
##
function _pkg__is_installed () {
  local pkg="$1"; shift;

  "_is_${pkg}_installed"
}
