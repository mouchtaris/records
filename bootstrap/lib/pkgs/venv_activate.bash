##
## VEnv activate

##
## VEnv activate should be always run, so it's never "installed"
##
function pkgs::venv_activate::is_installed() {
  false
}

##
## VEnv "installation" means simply to activate
## the venv IN THIS SHELL.
##
function pkgs::venv_activate::install() {
  venv::activate
}
