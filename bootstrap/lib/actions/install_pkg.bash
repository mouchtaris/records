##
## Install a package if not installed and fail if installation fails.
##
function ::install_pkg() {
  local pkg="$1"; shift

  pkg::install_unless_installed "$pkg" || {
    local msg="$( printf 'Failed during "installation"-unless-installed of %s' "$pkg" )"
    ::err_and_die "$msg"
  }
}
