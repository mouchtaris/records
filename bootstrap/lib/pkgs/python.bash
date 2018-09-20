##
## Python ##
##

##
## Python is "installed" is pyenv can detect a python
## executable.
##
function pkgs::python::is_installed() {
  pyenv which python 1>/dev/null 2>&1
}

##
## "Install" python means install it with PyEnv.
##
function pkgs::python::install() {
  pyenv install
}
