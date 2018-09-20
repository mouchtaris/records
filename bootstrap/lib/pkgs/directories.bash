##
## Directories
_directories=(
  "$PYTHON_BUILD_CACHE_PATH"
  "$PIPENV_CACHE_DIR"
  "$PIP_CACHE_DIR"
)

function pkgs::directories::is_installed() { false; } # Omnipotent and fast installation

function pkgs::directories::install() {
  for f in "${_directories[@]}"
  do
    if [ -n "$f" ]
    then
      mkdir -pv "$f"
    else
      printf 'Skipping empty name ...\n'
    fi
  done
}
