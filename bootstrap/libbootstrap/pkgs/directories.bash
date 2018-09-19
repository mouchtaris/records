##
## Directories
_directories=(
  "$PYTHON_BUILD_CACHE_PATH"
  "$PIPENV_CACHE_DIR"
  "$PIP_CACHE_DIR"
)

function _is_directories_installed() { false; } # Omnipotent and fast installation

function _install_directories() {
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
