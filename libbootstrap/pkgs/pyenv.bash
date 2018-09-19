##
## PyEnv ##

##
## PyEnv's git source, relative to "git master".
##
function _pyenv_git_source() {
  echo "$( _conf__gitmaster )"/pyenv/pyenv.git;
}

##
## PyEnv is installed if its in PATH and can
## be successfully invoked.
##
function _is_pyenv_installed() {
  pyenv init - 1>/dev/null 2>&1
}

##
## "Installing" PyEnv simply means cloning from the
## source git repo.
##
function _install_pyenv() {
  git clone "$( _pyenv_git_source )" "$PYENV_ROOT"
}
