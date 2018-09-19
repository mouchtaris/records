##
## Install a given package unless it's already installed.
##
## # Arguments
## * pkg    : package name
##
## # Example
##
##    _pkg__install_unless_installed pyenv
##
function _pkg__install_unless_installed() {
  local pkg="$1"; shift;

  if ! _pkg__is_installed "$pkg"
  then
    echo "Installing $pkg ..."
    _pkg__install "$pkg"
  else
    echo "Skipping installing $pkg..."
  fi
}
