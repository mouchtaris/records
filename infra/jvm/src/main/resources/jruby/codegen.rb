# frozen-string-literal: true
require 'erb'
require 'fileutils'
require 'java'
require 'pathname'
require 'pp'
require 'yaml'

Codegen = Java::gv.codegen.Codegen

def lazy_plus(*lazies)
  lazies
    .to_enum
    .lazy
    .map(&:to_enum)
    .map(&:lazy)
    .flat_map(&:itself)
end

def manifest_pathname
  Pathname(ARGV[0])
end

def destination_dir_pathname
  Pathname(ARGV[1])
end

def resource(path)
  Java::gv.codegen.Codegen.resource(path.to_s)
    &.to_io
end

def make_path(path, key, value)
  Pathname((path.dup << key << value).join("/"))
end

def visit_manifest(path, key, value)
  case value
    when Array
      value.each do |v|
        yield make_path(path, key, v)
      end
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
  attr_reader :n, :template

  def root
    manifest_pathname.dirname
  end

  def resource_pathname
    root + @template
  end

  def collector_source_pathname
    root + "#@template-collector"
  end

  def numbered_name
    "#{@template.basename(@template.extname)}#@n#{@template.extname}"
  end

  def instantiation_destination
    destination_dir_pathname + @template.dirname + numbered_name
  end

  def collector_destination_pathname
    destination_dir_pathname + @template
  end

  def source
    resource(resource_pathname.to_s)&.read
  rescue
    raise resource_pathname.to_s
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

  def all
    (1..Facade::N).map { |n| TemplateHandler.new(@template, n) }
  end

  def context
    binding
  end

  def rendered
    ERB.new(source).result(context)
  end

  def handle!
    instantiation_destination.tap do |dst|
      FileUtils::Verbose.mkdir_p dst.dirname
      dst.open('w') { |fout| fout.write(rendered) }
    end
  end

end

class CollectorTemplateHandler < TemplateHandler

  def initialize(template, n)
    super(template, n)
  end

  alias_method :'template_handler::handle!', :handle!

  def collector_context
    binding
  end

  def handle!
    source = resource(collector_source_pathname)&.read
    if source
      engine = ERB.new(source)
      rendition = engine.result(collector_context)
      collector_destination_pathname.open('w') { |fout| fout.write(rendition) }
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
    infestations = (1..N).to_enum.lazy.map { |n| TemplateHandler.new(template, n) }
    collector = [CollectorTemplateHandler.new(template, 0)]
    lazy_plus(
      infestations,
      collector,
    )
  end

  def self.handlers_for_java(template)
    handlers_for(template).to_a.to_java
  end

end

Facade
