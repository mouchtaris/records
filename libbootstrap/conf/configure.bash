##
## Configure according to a given ENV
##
## This will load '_common' and "$ENV" from
## with _conf__load, and set the current
## environment to $ENV.
##
## # Arguments
##   * env: the environment name -- see _conf__load()
##
## # Example
##
##     _conf__configure 'local'
##
function _conf__configure() {
  local env="$1"; shift

  _conf__load '_common' &&
    _conf__load "$env" &&
    export _CONF__ENV="$env"
}
