##
## VEnv

function _venv_activate() { source "$_VENV_ROOT"/bin/activate && which python && which pip; }

function _is_venv_installed() { ( _venv_activate 2>&1 1>/dev/null ) 2>&1 1>/dev/null; }

function _install_venv() { python -m venv "$_VENV_ROOT" && _venv_activate; }
