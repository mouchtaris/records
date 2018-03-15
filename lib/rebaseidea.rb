ideas = []
wips = []

STDIN.each_line do |line|
  case line
  when /idea/ then ideas << line
  when /wip/ then wips << line
  else raise "What is this shit #{line.inspect}"
  end
end

def lay(wat)
  puts wat.shift
  wat.each do |w|
    puts w.gsub(/^pick/, 'f')
  end
end

lay(wips)
lay(ideas)
