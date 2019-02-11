require 'logger'
require 'pp'

class RubyLayer

  def gem_paths!
    gem_paths = { 'GEM_HOME' => @gem_home }.freeze

    msg = PP.pp(gem_paths, 'Setting Gem.paths = ')
    @logger.info(msg)
    require 'rubygems'
    Gem.paths = gem_paths
  end

  def prelude!
    gem_paths!
  end

  def diagnose
    require 'pp'
    pp(
      RUBY_VERSION: RUBY_VERSION,
      ARGV: ARGV,
      GEM_PATHS: Gem.paths,
    )    
  end

  def initialize(gem_home:, **opts)
    @gem_home = gem_home
    parse_opts!(opts)
    @logger = Logger.new(STDERR)
    prelude!
    diagnose
  end

  def bundler_env!
  end

  def install_gem(name:, version: nil)
    opts = %W[
      --no-document
      --no-wrappers
      --no-user-install
      --post-install-message
      --remote
      --install-dir=#{@gem_home}
    ]
    opts.append("--version=#{version}") if version
    opts.append(name)

    require 'rubygems/commands/install_command'
    
    cmd = Gem::Commands::InstallCommand.new
    cmd.handle_options opts

    begin
      cmd.execute
    rescue Gem::SystemExitException => e
      puts "DONE: #{e.exit_code}"
    end
  end

  def irb_session
    require 'irb'
    IRB::Irb.new
  end

  def gem_bin_stub(gem_name:, bin:, argv:)
    require 'rubygems'

    version = ">= 0.a"

    if argv.first
      str = argv.first
      str = str.dup.force_encoding("BINARY") if str.respond_to? :force_encoding
      if md = /\A_(.*)_\z/.match(str) and Gem::Version.correct?(md[1]) then
        version = md[1]
        argv.shift
      end
    end

    if Gem.respond_to?(:activate_bin_path)
      load Gem.activate_bin_path(gem_name, bin, version)
    else
      gem gem_name, version
      load Gem.bin_path(gem_name, bin, version)
    end
  end
end

puts 'Ruby layer loaded.'