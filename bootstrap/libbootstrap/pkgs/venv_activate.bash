##
## VEnv activate

##
## VEnv activate should be always run, so it's never "installed"
##
function _is_venv_activate_installed() {
  false
}

##
## VEnv "installation" means simply to activate
## the venv IN THIS SHELL.
##
function _install_venv_activate() {
  _venv__activate
}
