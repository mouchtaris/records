##
## Initialize local mirrors (git repoes, PIP mirrors, etc)
##
function ::local_mirrors() {
  git clone \
    --mirror \
    "$( conf::at deploy _pyenv_git_source )" \
    "$( conf::at local  _pyenv_git_source )"
}
