# Bootstrap

This project aims to set up a local Python environment, completely from scratch,
and without relying on external package managers and OSes.

## Motivation

Setting up Python environments can be confusing and tedious. There is a number of tools,
many of which resemble or reuse one another.

The details and quirks of every project/method are too many to leave to the human brain.

This project creates a clear recipe for setting up a specific, version locked
Python environment, without relying on specific environments in order to do so.

### Why version locking

Relying on random versions in the fast changing environment of scripted languages and
projects is risk. A strong indication of the necessity of reproducable environments
and version locking is found in the fact that there are so many version locking
tools coming out for these environments.

### Troubleshooted

* `Airflow` does not install in Python 3.7. That's because in python 3.7 `async` becomes
  a keyword. `Airflow` has committed a fix for this change but it has not made it
  to Pip as of the writing of this text.

### Principles

The principles that this methodology follows are:

* _No package managers_: package managers are extremely opinionated and specific.
  Additionally, there is very little guarantee that required versions will be present
  in each manager's repository. Finally, it is not a given that package managers will
  provide sufficient version locking.
* _Minimum system interference_: installing tools in the global, or even user-local
  environment is unstable, because these environments are specific to the OS and
  other user profiling choices. Additionally, one can be certain that the environment
  can be disposed simply be removing one directory.
* _Minimum additional installs_: the environment can be bootstrap with simple tools
  that can be found in almost every system.

## Background

This project uses a set of Python tools which come with their own specialty. Here is
minimal background knowledge, required to understand the recipe introduced.

### PyEnv

PyEnv is a collection of bash scripts, that can fetch and install any Python version
(and implementation), in a target directory (`PYENV_ROOT`).

The PyEnv environment can be activated by injecting hooks in the current shell
(`pyenv init`).

The `pyenv` executable itself is inserted in the `PATH` manually by the user.

### VEnv

`VEnv` is a Python module introduced in Python 3.3. VEnv creates "virtual", "isolated"
python environments, in a target directory. It is distributed by default with any
Python installation after 3.3 and it's in the standard module library.

VEnv does not build, rebuild, compile, or fetch any different Python version than the
one that runs it.

VEnv creates an environment by `python -m venv $TARGET` and is activated by sourcing the
appropriate shell script found under `$TARGET/bin/activate{,.csh,.fish}`.

When activated, every invocation to anything Python related will happen in the TARGET
directory (running `python` itself, `pip`, etc -- read further for `pip`).

### Pip

`Pip` is the de-facto package manager for Python. It fetches and installs Python
pacakges from a repository. It provides some very basic control over installed
package versions and version locking through `requirements.txt`. This feature is not
used in this project.

`Pip` can be kind-of-configured about the target directory, but this feature is also not
used in this project (we rely on VEnv -- read further).

### PipEnv

`PipEnv` is the latest and hippest addition in the Python Packaging ecosystem.

PipEnv is a fancy front-end for managing package dependencies. On the back, it
uses `PyEnv`, `VEnv`, and `Pip` in order to set up a environment.

PipEnv aims to nail the problem of fine control over version locking and
the resolution of dependency versions. To achieve this, it borrows wisdom from
similar but more mature sister projects (`Bundler` / Ruby).

For this, it uses a `Pipfile`, which describes dependencies and versions in detail.
After an installation, it creates and maintains also the `Pipfile.lock`,
which locks dependency versions. This way, when software is deployed, Pipenv
can re-install and re-create the exact desired environment, which has been used
also for QA testing.

## High Level Overview

The recipe introduced by this project is the following:

* _Git-Clone PyEnv_: this is a simple git-clone operation. PyEnv is cloned
  from its official github repository.
* _Install Python 3.6_: using PyEnv, we create a local installation of Python 3.6.
* _Use PyEnv Python_: we activate PyEnv and the local PyEnv environment (Python 3.6
  exactly) is used for the rest of the process.
* _Create a VEnv environment_: Using Python 3.6 from PyEnv, create a virtual Python
  environment. This creates a clean installation, free of packages and modifications.
  In the clean installation, we have a clean `pip` environment included.
* _Activate VEnv_: we activate the VEnv environment and everything python
  related now happens in the VEnv directory.
* _Upgrade pip_: the `pip` that comes with VEnv is probably outdated so we
  self-upgrade it -- joy! This happens *contained* in the VEnv directory.
* _Install PipEnv_: we install `pipenv` using `pip`. This still happens contained
  in the VEnv directory.
* _Install project dependencies with PipEnv_: we are finally able to use PipEnv
  to do the rest of the work for us. PipEnv will create an additional, separate
  VEnv, and install our desired packages and dependencies contained in that.
  
In script, one could write this as:

    # Fetch pyenv
    git clone $GIT_MASTER/pyenv/pyenv.git &&
    
    # Install python 3.6
    echo 3.6.6 | tee .python-version &&
    pyenv/bin/pyenv install &&
    
    # Install PyEnv hooks
    eval "$( pyenv/bin/pyenv init - )" &&
    
    # Create + Activate VEnv
    python3.6 -m venv &&
    source venv/bin/activate &&
    
    # Upgrade pip
    pip install --upgrade pip
    
    # Install pipenv
    pip install pipenv
    
    # Install dependencies with pipenv
    pipenv install --skip-lock
    
