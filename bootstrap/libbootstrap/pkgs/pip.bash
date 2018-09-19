##
## Pip

function _is_pip_installed() {
  _ver__is_at_least \
    $( _ver__get_pip_version pip ) \
    $( _conf__pkg_ver_major_minor 'PIP' )
}

function _install_pip() { env PIP_UPGRADE=true pip install pip; }
