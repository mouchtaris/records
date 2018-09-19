##
## Python ##
##

##
## Python is "installed" is pyenv can detect a python
## executable.
##
function _is_python_installed() {
  pyenv which python 1>/dev/null 2>&1
}

##
## "Install" python means install it with PyEnv.
##
function _install_python() {
  pyenv install
}
