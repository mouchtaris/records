##
## Env Diagnostics
##
## Env diagnostics is not really a package, it simply outputs
## diagnostics when "installed". Therefor, it's installed always.
##

function _is_env_diagnostics_installed() { false; } # Never; keep diagnosing

function _install_env_diagnostics() {
  for c in python pip pipenv
  do
    which "$c"
  done
}
