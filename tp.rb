require 'pp'

#
# Hello
#
class Type
  def initialize(parent, name)
    @parent = parent
    @name = name.dup
    @args = []
  end
  attr_reader :parent, :name

  def last_arg
    @args.last
  end

  def each_arg
    return @args.each unless block_given?
    @args.each(&Proc.new)
  end

  def has_args?
    !@args.empty?
  end

  def to_s
    @name.dup
  end

  def add_arg!(arg)
    @args << arg
    true
  end
end

#
# Hello
#
class PreviousJob
  def initialize
    @job = nil
  end

  def dispatch(*args, &new_job)
    @job&.call(*args).tap do
      @job = new_job
    end
  end

  def flush(*args)
    dispatch(*args) { raise 'must not run' }
  end
end
#
# Hello
#
class TypePrinter
  def initialize(outs, ind, t)
    @outs = outs
    @ind = ind
    @t = t
  end

  def print(line)
    @outs.print(line)
  end

  def println
    print("\n")
  end

  def ind!
    print('  ' * @ind)
  end

  def print!(is_last_arg)
    ind!
    print @t.name
    if @t.has_args?
      printjobs = PreviousJob.new
      print '['
      println
      @t.each_arg do |arg|
        printjobs.dispatch(false) do |is_last|
          TypePrinter.new(@outs, @ind + 1, arg).print!(is_last)
        end
      end
      printjobs.flush(true)
      ind!
      print ']'
      print ',' unless is_last_arg
    end
    println
  end
end

#
# Hello
#
class Parser
  module Token
    SPACE = /\A\s+/
    ID = /\A[a-zA-Z0-9_. :()]+/
    ARG_BEGIN = /\A\[/
    COMMA = /\A,/
    ARG_END = /\A\]/
  end

  def initialize(ins)
    @ins = ins.read
    @root = Type.new(nil, '<_root_>')
    @parent = @root
  end
  attr_reader :root

  def md_clean?
    !@md
  end

  def clean_md!
    raise 'md already clean' if md_clean?
    log("cleaning previous md: #{@md.inspect}")
    @md[0].tap do
      @md = nil
    end
  end

  def match(rx)
    log("matching rx = #{rx.inspect} =~ #{@ins.inspect[0..20]} ...")
    raise "md not consumed: #{@md}" unless md_clean?
    @md = rx.match(@ins)
  end

  def if_match(rx)
    match rx
    yield if @md
  end

  def consume!
    @ins = @ins[@md.end(0)..-1]
    log("consumed line to #{@ins.inspect[0..20]} ...")
    clean_md!
  end

  def eat_space!
    if_match(Token::SPACE) do
      consume!
      true
    end
  end

  def done?
    @ins.empty?
  end

  def parse!
    until done?
      try_parse_id! ||
        try_parse_args! ||
        try_parse_closing_args! ||
        try_parse_comma! ||
        parse_error!
    end
  end

  def try_parse_comma!
    eat_space!
    if_match(Token::COMMA) do
      consume!
    end
  end

  def try_parse_id!
    log('trying parse_id...')
    eat_space!
    if_match(Token::ID) do
      name = consume!.rstrip.lstrip
      type = Type.new(@parent, name)
      @parent.add_arg!(type)
      log("#{@root} << #{type}")
      true
    end
  end

  def try_parse_args!
    log('trying parse_args...')
    eat_space!
    if_match(Token::ARG_BEGIN) do
      consume!
      @parent = @parent.last_arg
    end
  end

  def try_parse_closing_args!
    log('trying to close args...')
    eat_space!
    if_match(Token::ARG_END) do
      consume!
      @parent = @parent.parent
    end
  end

  def parse_error!
    log("Parse error:\n  md = #{@md.inspect}\n  inp = #{@ins.inspect}\n")
    raise "parse error at #{@ins[0..20]} ..."
  end

  def log(msg)
    STDERR.puts " *** #{msg}"
  end
end

par = Parser.new(STDIN)
begin
  par.parse!
rescue => e
  TypePrinter.new(STDOUT, 0, par.root).print!(true)
  raise e
end
