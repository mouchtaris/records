##
## Print the root location of git-master
##
## This value is sourced from Configuration and
## this function inserts a default-value guard.
##
function conf::gitmaster() {
  local default_gitmaster=https://github.com
  echo "${GITMASTER:-$default_gitmaster}"
}
