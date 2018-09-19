##
## Activate the Venv
##
function _venv__activate() {
  source "$_VENV_ROOT"/bin/activate &&
    which python &&
    which pip
}
