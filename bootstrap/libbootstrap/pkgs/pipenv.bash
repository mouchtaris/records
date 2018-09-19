##
## Pipenv

function _is_pipenv_installed() {
  _ver__is_at_least \
    $( _ver__get_pip_version pipenv ) \
    $( _conf__pkg_ver_major_minor 'PIPENV' )
}

function _install_pipenv() { pip install pipenv; }
