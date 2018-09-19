##
## Python ##

function _is_python_installed() { pyenv which python >/dev/null 2>&1; }

function _install_python() { pyenv install; }
