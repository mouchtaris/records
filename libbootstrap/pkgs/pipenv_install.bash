##
## Pipenv install
##
## pipenv_install is a target for running 'pipenv install'.
##

function _is_pipenv_install_installed() { false; } # Let Pipenv figure this out

function _install_pipenv_install() {

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
