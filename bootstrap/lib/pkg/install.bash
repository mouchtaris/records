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
##    pkg::install pyenv
##
function pkg::install () {
  local pkg="$1"; shift;

  "pkgs::${pkg}::install"
}
