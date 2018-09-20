##
## Activate PyEnv after it's installed
##

##
## pyenv_activate is never "installed", so it's triggered on every run.
##
function pkgs::pyenv_activate::is_installed() {
  false
}

##
## "Installing" pyenv_activate simply means to add `pyenv` to PATH and
## install shell hooks
##
function pkgs::pyenv_activate::install() {
  export PATH="$PATH":"$PYENV_ROOT"/bin
  eval "$(pyenv init -)"
}
