##
## Pip
##

function pkgs::pip::is_installed() {
  ver::is_at_least \
    $( ver::get_pip_version pip ) \
    $( conf::pkg_ver_major_minor 'PIP' )
}

function pkgs::pip::install() {
  env PIP_UPGRADE=true pip install pip
}
