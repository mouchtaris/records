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
  local name="$1"; shift;

  if ! $"_is_${name}_installed"
  then
    $"_install_$name"
  else
    echo "Skipping installing $name ..."
  fi
}





###
### ## Specific Package Installers ##
###

##
## PyEnv ##
_pyenv_target_dir="$PYENV_ROOT"
function _is_pyenv_installed() { [ -d "$_pyenv_target_dir" ]; }
function _install_pyenv() { git clone "$GITMASTER"/pyenv/pyenv.git "$PYENV_ROOT"; }

##
## Python ##
function _is_python_installed() { false; }
function _install_python() { pyenv install; }





###
### ## Actions ##
###


##
## Print environment variables, grouped by grep.
##
function _print_envs() {
  for env in PYTHON PYENV PIP AIRFLOW
  do
    echo ===
    printenv | grep "$env"
  done
  echo ===
}

##
## Load the given environment and fail if loading fails.
##
function _configure() {
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

##
## 
##
exit 0

for pkg in \
  pyenv \
  python
do
  _install_unless_installed "$pkg" || _err_and_die "$( printf 'Failed while installing unless installed: %s' "$pkg" )"
done &&
exit 0 ||
exit 1

if ! _is_pyenv_installed; then _install_pyenv; fi
_install_python &&
_upgrade_pip &&
exit 0
exit 1

pip install --user psycopg2-binary &&
env AIRFLOW_GPL_UNIDECODE=yes pip install --user apache-airflow'[celery, crypto, s3, kubernetes]' &&
sudo mkdir -p /var/postgresql &&
sudo chmod 777 /var/postgresql &&
pg_ctl initdb &&
pg_ctl start &&
createuser airf &&
createdatabase airf &&
true
