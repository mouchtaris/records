##
## Activate the Venv
##
function venv::activate() {
  source "$_VENV_ROOT"/bin/activate &&
    which python &&
    which pip
}
