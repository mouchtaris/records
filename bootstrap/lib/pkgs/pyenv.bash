##
## PyEnv ##

##
## PyEnv's git source, relative to "git master".
##
function _pyenv_git_source() {
  echo "$( conf::gitmaster )"/pyenv/pyenv.git;
}

##
## PyEnv is installed if its in PATH and can
## be successfully invoked.
##
function pkgs::pyenv::is_installed() {
  $PYENV_ROOT/bin/pyenv init - 1>/dev/null 2>&1
}

##
## "Installing" PyEnv simply means cloning from the
## source git repo.
##
function pkgs::pyenv::install() {
  git clone "$( _pyenv_git_source )" "$PYENV_ROOT"
}
