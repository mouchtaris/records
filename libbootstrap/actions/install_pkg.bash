##
## Install a package if not installed and fail if installation fails.
##
function _install_pkg() {
  local pkg="$1"; shift

  _pkg__install_unless_installed "$pkg" || {
    local msg="$( printf 'Failed during "installation"-unless-installed of %s' "$pkg" )"
    _err_and_die "$msg"
  }
}
