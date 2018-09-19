##
## PyEnv ##

_pyenv_target_dir="$PYENV_ROOT"

function _pyenv_git_source() { echo "$( _conf__gitmaster )"/pyenv/pyenv.git; }

function _is_pyenv_installed() { [ -d "$_pyenv_target_dir" ]; }

function _install_pyenv() { git clone "$( _pyenv_git_source )" "$PYENV_ROOT"; }
