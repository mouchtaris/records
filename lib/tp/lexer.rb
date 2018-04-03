require 'pp'

#
# Hello
#
Token = Struct.new(:name, :value)

# Hello
#
class Lexer
  module Tokens
    ID      = /\A\w+/
    DOT     = /\A\./
    LBRACE  = /\A\[/
    RBRACE  = /\A\]/
    LPAR    = /\A\(/
    RPAR    = /\A\)/
    SPACE   = /\A[\s]+/
    CONS    = /\A::/
    COMMA   = /\A,/

    TOKENS = %i[ID DOT LBRACE RBRACE LPAR RPAR SPACE CONS COMMA].freeze
  end
  TOKENS = Tokens::TOKENS

  def initialize(ins)
    @ins = ins.read
  end

  def token_value(tok)
    Lexer::Tokens.const_get(tok)
  end

  def parse_error!
    raise "Parse error at #{@ins.inspect[0..30]} ..."
  end

  def consume!(md)
    @ins = @ins[md.end(0)..-1]
  end

  def try_match!(tok)
    token_value(tok).match(@ins).tap { |md| consume!(md) if md }
  end

  def parse!
    return to_enum :parse! unless block_given?
    until @ins.empty?
      md = nil
      token = TOKENS.find { |tok| md = try_match!(tok) }
      parse_error! unless token && md
      yield Token.new(token, md[0])
    end
  end
end
