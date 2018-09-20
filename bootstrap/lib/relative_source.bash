###
### ## Relatively sourcing bash scripts
###

__relative_source__paths=()
__relative_source__root="$PROJECT_DIR"

function relative_source::inspect() {
  echo '    ' ... relative_source:root="$( relative_source::root )"
  echo '    ' ... relative_source:paths="${__relative_source__paths[@]}"
}

function relative_source::root() {
  echo "$__relative_source__root"
}

function relative_source::paths() {
  echo "${__relative_source__paths[@]}"
}

function relative_source::push() {
  local path="$1"; shift;

  local previous="$( relative_source::root )"
  local next="$previous/$path"

  # Store previous root
  __relative_source__paths+=("$previous")
  __relative_source__root="$next"

  # Print diagnostics
  printf '+++ RelativeSource:push %s [%s]\n' \
    "$( relative_source::root )" \
    "$( relative_source::paths )"
}

function relative_source::pop() {
  local last="${#__relative_source__paths[@]}-1"
  local value="${__relative_source__paths[last]}"

  # Unset the last element in paths
  unset '__relative_source__paths[last]'
  # Restore last element as the current root
  __relative_source__root="$value"

  # Print diagnostics
  printf -- '--- RelativeSource:pop %s [%s]\n' \
    "$( relative_source::root )" \
    "$( relative_source::paths )"
}

function relative_source() {
  local name="$1"; shift;

  source "$( relative_source::root )/$name"
}
