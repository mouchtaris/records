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

  def initialize(gem_home:)
    @gem_home = gem_home
    @logger = Logger.new(STDERR)
    prelude!
    diagnose
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
end

puts 'Ruby layer loaded.'