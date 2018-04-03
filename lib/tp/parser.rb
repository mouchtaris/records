require_relative 'lexer'

#
# Hello
#
class Parser
  # Hello
  class TokenStream
    def log(msg)
      STDERR.puts " --< TokenStream >-- #{msg}"
    end

    def initialize(lexer)
      @lex = lexer.parse!
      @marks = []
      @buf = []
      @bufi = 0
      @callbacks = {}
    end

    def register(evt, cb)
      (@callbacks[evt] ||= []) << cb
    end

    def emmit(evt, *args)
      @callbacks[evt]&.call(*args)
    end

    def clear!
      raise 'assert false' unless @marks.empty?
      @buf.clear
      @bufi = 0
    end

    def mark!
      log 'marking'
      @marks.push(@bufi)
    end

    def bnext!
      (@buf[@bufi] if @bufi < @buf.length)
        .tap { |r| log "bnext = #{r}" }
    end

    def lnext!
      @lex.next!.tap do |tok|
        @buf << tok
        @bufi += 1
        log "lnext = #{tok}"
      end
    rescue StopIteration => e
      nil
    end

    def next!
      bnext! || lnex!
    end

    def reset!(success)
      raise 'assert false' if @marks.empty?
      pop!.tap do |r|
        @bufi = r unless success
      end
      success
    end

    def pop!
      @marks.pop.tap do
        clear! if @marks.empty?
      end
    end
  end

  def initialize(lexer)
    @stream = TokenStream.new(lexer)
  end

  def parse!
    parse_type!
  end

  def parse?
    @stream.mark!
    yield.tap(&@stream.method(:reset!))
  end

  def parse_type!
    parse_id! &&
      parse? { parse_args }
  end

  def parse_id!
    @stream.mark!
    push_token @stream.next
    @stream.reset!(tok.name == :ID)
  end

  def parse_args
    false
  end
end
