##
## Evaluate an expression in the given environment
##
## Given a specific ENV (arg0), evaluate the rest of arguments
## as an expression under that ENV.
##
## # Arguments
##   * env: an environment name -- see conf::configure() and friends
##
## # Example
##
##      #
##      # Evaluate _conf__python_builf_mittor_url
##      # in the context of local ENV
##      #
##      conf::at local conf::python_build_mirror_url
##
function conf::at() {
  local env="$1"; shift;

  (
    conf::configure "$env" &&
      eval "$@"
  )
}
