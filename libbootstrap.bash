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
## Get a version requirement for a package
## from configuration.
##
## Version is described by a package name
## and a version level.
##
## Package name + version level respond to a
## specific variable set through config.
##
##    _${package}_REQUIRED_${level}
##
function _conf__pkg_ver() {
  local name="$1"; shift;
  local level="$1"; shift;

  local varname='$_'"$name"'_REQUIRED_'"$level"
  local result="$( eval "$varname" )"

  if [ -z "$result" ]
  then
    _err_and_die "$( printf 'No version requirement found for %s @%s (%s)' "$name" "$level" "$varname" )"
  fi
}
##
## Alias for version level MAJOR
function _conf__pkg_ver_major() { _conf__pkg_ver "$1" 'MAJOR'; }
##
## Alias for version level MINOR
function _conf__pkg_ver_minor() { _conf__pkg_ver "$1" 'MINOR'; }
##
## Alias for version "MAJOR MINOR"
function _conf__pkg_ver_major_minor() {
  local name="$1"; shift;

  echo \
    "$( _conf__pkg_ver_major "$name" )" \
    "$( _conf__pkg_ver_minor "$name" )"
}

##
## Evaluate an expression in the given environment
function _conf_at() {
  local env="$1"; shift;

  (
    _configure "$env" &&
      eval "$@"
  )
}






###
### ## Local mirroring
###

##
## Initialize local mirrors (git repoes, PIP mirrors, etc)
##
function _local_mirrors() {
  git clone \
    --mirror \
    "$( _conf_at deploy _pyenv_git_source )" \
    "$( _conf_at local _pyenv_git_source )"
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
    echo "Installing $pkg ..."
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

##
## Extract the version from a pip package
##
## The version is echoed as
##
##    MAJOR MINOR
##
function _pip_get_version () {
  local pkg="$1"; shift;

  pip show "$pkg" |
    sed -r -n -e '/.*Version: ([[:digit:]]+)\.([[:digit:]]+).*/s//\1 \2/p'
}





###
### ## Specific Package Installers ##
###

##
## Directories
function _is_directories_installed() { false; } # Omnipotent and fast installation
function _install_directories() {
  for f in \
    "$PYTHON_BUILD_CACHE_PATH" \
    "$PIPENV_CACHE_DIR" \
    "$PIP_CACHE_DIR"
  do
    if [ -n "$f" ]
    then
      mkdir -pv "$f"
    else
      printf 'Skipping empty name ...\n'
    fi
  done
}

##
## PyEnv ##
_pyenv_target_dir="$PYENV_ROOT"
function _pyenv_git_source() { echo "$( _gitmaster )"/pyenv/pyenv.git; }
function _is_pyenv_installed() { [ -d "$_pyenv_target_dir" ]; }
function _install_pyenv() { git clone "$( _pyenv_git_source )" "$PYENV_ROOT"; }

##
## VEnv
function _venv_activate() { source "$_VENV_ROOT"/bin/activate; which python; which pip; }
function _is_venv_installed() { ( _venv_activate 2>&1 1>/dev/null ); }
function _install_venv() { python -m venv "$_VENV_ROOT"; }


##
## Python ##
function _is_python_installed() { pyenv which python >/dev/null 2>&1; }
function _install_python() { pyenv install; }

##
## Pip
function _is_pip_installed() { _is_version_at_least $( _pip_get_version pip ) $( _conf__pkg_ver_major_minor 'PIP' ); }
function _install_pip() { env PIP_UPGRADE=true pip install pip; }

##
## Pipenv
function _is_pipenv_installed() { _is_version_at_least $( _pip_get_version pipenv ) $( _conf__pkg_ver_major_minor 'PIPENV' ); }
function _install_pipenv() { pip install pipenv; }

##
## PsycoPG
function _is_psycopg_installed() { false; } # Let pip figure this out
function _install_psycopg() { pip install psycopg2-binary; }

##
## Apache Airflow
function _is_airflow_installed() { false; } # Let pip figure this out
function _install_airflow() {
  env AIRFLOW_GPL_UNIDECODE=yes \
    pip install apache-airflow'[celery, crypto, s3, kubernetes]'
}





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

  {
    _load_env '_common' &&
    _load_env "$env" &&
    export _CURRENT_ENV="$env"
  } ||
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
