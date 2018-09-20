##
## Pipenv
##

function pkgs::pipenv::is_installed() {
  ver::is_at_least \
    $( ver::get_pip_version pipenv ) \
    $( conf::pkg_ver_major_minor 'PIPENV' )
}

function pkgs::pipenv::install() {
  pip install pipenv
}
