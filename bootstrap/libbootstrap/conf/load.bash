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
##    _conf__load 'local' || _err_and_die 'No such env: local'
##
function _conf__load() {
  local env="$1"; shift;

  local src="$_envs_dir/$env.bash"
  [ -f "$src" ] && source "$src"
}
