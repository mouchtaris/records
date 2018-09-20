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
##    pkg::is_installed pyenv
##
function pkg::is_installed () {
  local pkg="$1"; shift;

  "pkgs::${pkg}::is_installed"
}
