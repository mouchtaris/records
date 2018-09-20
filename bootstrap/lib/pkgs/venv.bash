##
## VEnv

##
## Venv is "installed" if activating it is successful.
##
function pkgs::venv::is_installed() {
  ( venv::activate 2>&1 1>/dev/null ) 2>&1 1>/dev/null
}

function pkgs::venv::install() {
  python -m venv "$_VENV_ROOT"
}
