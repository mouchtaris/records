##
## Print environment variables, grouped by grep.
##
function _print_envs() {
  for env in PYTHON PYENV PIP AIRFLOW
  do
    echo
    printf '=== %-10s ===\n' "$env"
    printenv | grep "$env"
  done
  echo
  echo === '</>' ===
}
