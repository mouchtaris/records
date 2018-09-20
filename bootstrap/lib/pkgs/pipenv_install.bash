##
## Pipenv install
##
## pipenv_install is a target for running 'pipenv install'.
##

function pkgs::pipenv_install::is_installed() { false; } # Let Pipenv figure this out

function pkgs::pipenv_install::install() {

  #
  # For the moment we need to use --skip-lock because of
  # some pipenv bug that messes up dependency resolution
  # about version locking.
  #
  # The work around is that we specify strict versions
  # in Pipfile itself.
  #
  pipenv install --skip-lock

}
