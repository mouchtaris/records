##
## Install a given package unless it's already installed.
##
## # Arguments
## * pkg    : package name
##
## # Example
##
##    pkg::install_unless_installed pyenv
##
function pkg::install_unless_installed() {
  local pkg="$1"; shift;

  if ! pkg::is_installed "$pkg"
  then
    echo "Installing $pkg ..."
    pkg::install "$pkg"
  else
    echo "Skipping installing $pkg..."
  fi
}
