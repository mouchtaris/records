##
## VEnv

##
## Venv is "installed" if activating it is successful.
##
function _is_venv_installed() {
  ( _venv__activate 2>&1 1>/dev/null ) 2>&1 1>/dev/null
}

function _install_venv() {
  python -m venv "$_VENV_ROOT"
}
