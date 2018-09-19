##
## Evaluate an expression in the given environment
##
## Given a specific ENV (arg0), evaluate the rest of arguments
## as an expression under that ENV.
##
## # Arguments
##   * env: an environment name -- see _conf__configure() and friends
##
## # Example
##
##      #
##      # Evaluate _conf__python_builf_mittor_url
##      # in the context of local ENV
##      #
##      _conf__at local _conf__python_build_mirror_url
##
function _conf__at() {
  local env="$1"; shift;

  (
    _conf__configure "$env" &&
      eval "$@"
  )
}
