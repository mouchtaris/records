##
## Initialize local mirrors (git repoes, PIP mirrors, etc)
##
function _local_mirrors() {
  git clone \
    --mirror \
    "$( _conf__at deploy _pyenv_git_source )" \
    "$( _conf__at local  _pyenv_git_source )"
}
