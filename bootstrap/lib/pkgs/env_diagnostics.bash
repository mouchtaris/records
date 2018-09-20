##
## Env Diagnostics
##
## Env diagnostics is not really a package, it simply outputs
## diagnostics when "installed". Therefor, it's installed always.
##

function pkgs::env_diagnostics::is_installed() { false; } # Never; keep diagnosing

function pkgs::env_diagnostics::install() {
  for c in python pip pipenv
  do
    which "$c"
  done
}