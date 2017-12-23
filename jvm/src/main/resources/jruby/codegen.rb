# frozen-string-literal: true
require 'erb'
require 'fileutils'
require 'java'
require 'pathname'
require 'pp'
require 'yaml'

Codegen = Java::gv.codegen.Codegen

def manifest_pathname
  Pathname(ARGV[0])
end

def destination_dir_pathname
  Pathname(ARGV[1])
end

def resource(path)
  Java::gv.codegen.Codegen.resource(path.to_s)
    .tap { |rc| raise "Resource not found: #{path}" unless rc }
    .to_io
end

def make_path(path, key, value)
  Pathname((path.dup << key << value).join("/"))
end

def visit_manifest(path, key, value)
  case value
    when String
      yield make_path(path, key, value)
    else
      path2 = path.dup << key
      value.each do |k2, v2|
        visit_manifest(path2, k2, v2) { |*a| yield *a }
      end
  end
end

def manifest
  YAML.load(resource(manifest_pathname))
end

class TemplateHandler

  def initialize(template, n)
    @n = n
    @template = template.dup.freeze
  end
  attr_reader :template

  def resource_pathname
    manifest_pathname.dirname + @template
  end

  def source
    resource(resource_pathname.to_s).read
  end

  def each_i
    return enum_for :each_i unless block_given?
    (1..@n).each { |*a| yield *a }
  end

  def type_params
    each_i.map(&:to_s).map(&'T'.method(:+))
  end

  def tuple_type
    "Tuple#@n[#{type_params.join ', '}]"
  end

  def list_type
    type_params.join(" :: ") + " :: Nil"
  end

  def context
    binding
  end

  def rendered
    ERB.new(source).result(context)
  end

  def numbered_name
    "#{@template.basename(@template.extname)}#@n#{@template.extname}"
  end

  def destination_pathname
    destination_dir_pathname + @template.dirname + numbered_name
  end

  def handle!
    destination_pathname.tap do |dst|
      FileUtils::Verbose.mkdir_p dst.dirname
      dst.open('w') { |fout| fout.write(rendered) }
    end
  end

end

module Facade
  N = 22

  def self.templates
    manifest
      .map { |k, v| enum_for :visit_manifest, [], k, v }
      .flat_map(&:to_a)
  end

  def self.templates_java
    templates.to_a.to_java
  end

  def self.handlers_for(template)
    (1..N).map do |n|
      TemplateHandler.new(template, n)
    end
  end

  def self.handlers_for_java(template)
    handlers_for(template).to_a.to_java
  end

end

Facade
