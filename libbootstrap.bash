###
### ## Utility ##
###

##
## Print the error message on `STDERR` and `exit(1)`.
##
## # Arguments
## * msg  : error message to print
##
## # Example
##
##    false || _err_and_die 'There was no hope'
##
function _err_and_die() {
  local msg="$1"; shift;

  printf '%s\n' "$msg" 1>&2
  exit 1
}





###
### ## Enviornment / Configuration ##
###
_envs_dir=./envs

##
## Load the specified environment
##
## # Config
## * _envs_dir  : base directory where env files are located
##
## # Arguments
## * env        : environment name
##
## # Example
##
##    _load_env 'local' || _err_and_die 'No such env: local'
##
function _load_env() {
  local env="$1"; shift;

  local src="$_envs_dir/$env.bash"
  [ -f "$src" ] && source "$src"
}

##
## Print the root location of git-master
##
## This value is sourced from Configuration and
## this function inserts a default-value guard.
##
function _gitmaster() {
  local default_gitmaster=https://github.com
  echo "${GITMASTER:-$default_gitmaster}"
}

##
## Value of PYTHON_BUILD_MIRROR_URL
##
function _conf__python_build_mirror_url() {
  echo "$PYTHON_BUILD_MIRROR_URL"
}

##
## The required pip major version
function _conF__pip_major() { echo "$_PIP_REQUIRED_MAJOR"; }
##
## The required pip minor version
function _conf__pip_minor() { echo "$_PIP_REQUIRED_MINOR"; }
##
## The required pip version
function _conf__pip_required_version() {
  echo \
    "$( _conF__pip_major )" \
    "$( _conf__pip_minor )"
}







###
### ## "Package Management" ##
###

##
## Make an ad-hoc test about whether something is
## sufficiently installed.
##
## Delegates to `_is_$pkg_installed`
##
## # Arguments
## * pkg    : package name
##
## # Example
##
##    _is_installed pyenv
##
function _is_installed () {
  local pkg="$1"; shift;

  "_is_${pkg}_installed"
}

##
## Install a given package.
##
## Delegates to `_install_$pkg`.
##
## # Arguments
## * pkg    : package name
##
## # Example
##
##    _install pyenv
##
function _install () {
  local pkg="$1"; shift;

  "_install_${pkg}"
}

##
## Install a given package unless it's already installed.
##
## # Arguments
## * pkg    : package name
##
## # Example
##
##    _install_unless_installed pyenv
##
function _install_unless_installed() {
  local pkg="$1"; shift;

  if ! _is_installed "$pkg"
  then
    _install "$pkg"
  else
    echo "Skipping installing $pkg..."
  fi
}

##
## Compare versions
##
## v1j.v1m >= v2j.v2m
##
## # Arguments
## * v1j    : version 1 major
## * v1m    : version 1 minor
## * v2j    : version 2 major
## * v2m    : version 2 minor
##
## # Example
##
##    _is_verion_at_least $v_maj $v_min 2 1 || _install_pkg $smth
##
function _is_version_at_least() {
  local v1j="$1"; shift
  local v1m="$1"; shift
  local v2j="$1"; shift
  local v2m="$1"; shift
  [[
      $((v1j)) > $((v2j))
    ||
        $((v1j)) == $((v2j))
      &&
      (   $((v1m)) > $((v2m))
        ||
          $((v1m)) == $((v2m)) )
  ]]
}




###
### ## Specific Package Installers ##
###

##
## PyEnv ##
_pyenv_target_dir="$PYENV_ROOT"
function _is_pyenv_installed() { [ -d "$_pyenv_target_dir" ]; }
function _install_pyenv() { git clone "$( _gitmaster )"/pyenv/pyenv.git "$PYENV_ROOT"; }

##
## Python ##
function _is_python_installed() { pyenv which python >/dev/null 2>&1; }
function _install_python() { pyenv install; }

function _pip_get_version () {
  pip show pip |
    sed -r -n -e '/.*Version: ([[:digit:]]+)\.([[:digit:]]+).*/s//\1 \2/p'
}

function _is_pip_installed() { _is_version_at_least $( _pip_get_version ) $( _conf__pip_required_version ); }
function _install_pip() { env PIP_UPGRADE=true pip install pip; }





###
### ## Actions ##
###


##
## Print environment variables, grouped by grep.
##
function _print_envs() {
  for env in PYTHON PYENV PIP AIRFLOW
  do
    echo
    printf '=== %-10s ===\n' "$env"
    printenv | grep "$env"
  done
  echo
  echo === '</>' ===
}

##
## Load the given environment and fail if loading fails.
##
function _configure() {
  local env="$1"; shift

  _load_env "$env" ||
    _err_and_die "$( printf 'No such environemnt: %s' "$env" )"
}

##
## Install a package if not installed and fail if installation fails.
##
function _install_pkg() {
  local pkg="$1"; shift

  _install_unless_installed "$pkg" ||
    _err_and_die "$( printf 'Failed during "installation"-unless-installed of %s' "$pkg" )"
}
