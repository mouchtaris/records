###
# Zero-print all sources for Bootstrap
#
function bootstrap::sources
  find bootstrap/ \
    \( \
      -iname '*.bash' \
      -o \
      \( \
        -iname bootstrap \
        -a -type f \
      \) \
    \) \
    -print0
end

###
# Global substitute all REGX with REPL
#
function gs
  set -l regx $argv[1]
  set -l repl $argv[2]

  bootstrap::sources | \
    xargs -0 sed -r -i -e /"$regx"/s//$repl/g
end

###
# Old-to-new function name style substitute
#
function fgs
  set -l ns $argv[1]
  set -l name $argv[2]

  gs _{$ns}__$name $ns::$name
end

###
# Old-to-new function name style for all NAMES
# under NS
#
function nfgs
  set -l ns $argv[1]
  set -l names $argv[2..-1]

  for n in $names 
    fgs $ns $n
  end
end
