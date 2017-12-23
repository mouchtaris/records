# frozen-string-literal: true
require 'pathname'
require 'yaml'
require 'java'
require 'pp'

Codegen = Java::gv.codegen.Codegen

TEMPLATES = Pathname '/templates'
MANIFEST = TEMPLATES + 'manifest.yaml'

def resource path
    Codegen.resource(path).to_io
end

def visit(key, value)
  case value
  when String
    handleTemplate(value)
  end
end

PP.pp YAML.load(resource MANIFEST), String.new
