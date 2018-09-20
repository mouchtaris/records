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
##    conf::load 'local' || ::err_and_die 'No such env: local'
##
function conf::load() {
  local env="$1"; shift;

  local src="$_envs_dir/$env.bash"
  [ -f "$src" ] && source "$src"
}
