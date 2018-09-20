##
## Configure according to a given ENV
##
## This will load '_common' and "$ENV" from
## with conf::load, and set the current
## environment to $ENV.
##
## # Arguments
##   * env: the environment name -- see conf::load()
##
## # Example
##
##     conf::configure 'local'
##
function conf::configure() {
  local env="$1"; shift

  conf::load '_common' &&
    conf::load "$env" &&
    export _CONF__ENV="$env"
}
