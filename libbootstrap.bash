relative_source__push libbootstrap
  ###
  ### ## Utility ##
  ###
  relative_source err_and_die.bash
  ###
  ### ## Enviornment / Configuration ##
  ###
  relative_source__push conf
    _envs_dir="$PROJECT_DIR/envs"
    relative_source load.bash
    relative_source gitmaster.bash
    relative_source python_build_mirror_url.bash
    relative_source pkg_ver.bash
    relative_source at.bash
    relative_source configure.bash
  relative_source__pop
  ###
  ### ## Local mirroring
  ###
  relative_source local_mirrors.bash
  ###
  ### ## "Package Management" ##
  ###
  relative_source__push pkg
    relative_source is_installed.bash
    relative_source install.bash
    relative_source install_unless_installed.bash
  relative_source__pop

  relative_source__push ver
    relative_source is_at_least.bash
    relative_source get_pip_version.bash
  relative_source__pop
  ###
  ### ## Specific Package Installers ##
  ###
  relative_source__push pkgs
    relative_source directories.bash
    relative_source pyenv.bash
    relative_source venv.bash
    relative_source python.bash
    relative_source pip.bash
    relative_source pipenv.bash
    relative_source env_diagnostics.bash
    relative_source pipenv_install.bash
  relative_source__pop
  ###
  ### ## Actions ##
  ###
  relative_source__push actions
    relative_source print_envs.bash
    relative_source configure.bash
    relative_source install_pkg.bash
  relative_source__pop
relative_source__pop
