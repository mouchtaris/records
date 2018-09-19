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

