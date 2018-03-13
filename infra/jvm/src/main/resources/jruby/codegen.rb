# frozen-string-literal: true
require 'erb'
require 'fileutils'
require 'java'
require 'pathname'
require 'pp'
require 'yaml'

class Iterable

  def initialize(enumerable)
    @source = enumerable
    @next_values = []
  end

  def next?
    if @next_values.empty?
      @next_values << @source.next
    end
    true
  rescue StopIteration
    false
  end

  def next!
    @next_values.first.tap do
      @next_values.clear
    end
  end

end

#
# Utilities for runtime-type-safety of arguments and return
# values.
#
module Runsafe

  #
  # Fetch `name`'s value from the given `block`'s binding,
  # and make sure it's of type `clazz`, which is the result
  # of evaluating the given block.
  #
  # So you can write fancy stuff like
  #
  #     def f(x)
  #       argtype :x { Integer }
  #       x + 1
  #     end
  #
  def argtype(name, &block)
    clazz = block.call()
    value = block.binding.eval(name.to_s)
    raise ArgumentError, "#{name} needs to be a #{clazz}" unless value.is_a?(clazz)
  end

  extend self
end

module Utilities
  include Runsafe

  #
  # Concatenate lazy enumerables into a lazy enumerable.
  #
  def lazy_plus(*lazies)
    lazies
      .to_enum
      .lazy
      .map(&:to_enum)
      .map(&:lazy)
      .flat_map(&:itself)
  end

  #
  # Get a JVM resource stream as a Ruby IO.
  #
  def resource(path)
    argtype(:path) { Pathname }
    Java::gv.codegen.Codegen.resource(path.to_s)
        &.to_io
  end

  extend self
end

module Parameters

  #
  # The pathname for the manifest, describing templates to expand.
  #
  def manifest_pathname
    Pathname(ARGV[0])
  end

  #
  # The pathname for the destination root directory.
  #
  def destination_dir_pathname
    Pathname(ARGV[1])
  end

  extend self
end

#
# Facade over reading and accessing the template manifest.
#
class Manifest
  include Runsafe
  include Utilities

  INFESTATIONS = :infestations
  PLAIN = :plain

  def initialize(manifest_pathname)
    @manifest_pathname = manifest_pathname.dup
    load_manifest
    load_templates
  end
  attr_reader :manifest_pathname,
              :manifest,
              INFESTATIONS,
              PLAIN

  def load_manifest
    @manifest = YAML.load(resource(manifest_pathname))
  end

  def load_templates
    root = Pathname('')
    [INFESTATIONS, PLAIN].map(&:to_s).each do |typ|
      enum = enum_for :visit_manifest, root, @manifest[typ]
      instance_variable_set :"@#{typ}", enum.lazy
    end
  end

  def visit_manifest_hash(parent, hash)
    hash.each do |key, value|
      visit_manifest(parent + key, value) do |path|
        yield path
      end
    end
  end

  def visit_manifest_array(parent, array)
    array.each do |value|
      yield parent + value
    end
  end

  def visit_manifest(parent, value, &block)
    argtype(:parent) { Pathname }
    case value
      when Array
        visit_manifest_array(parent, value, &block)
      when Hash
        visit_manifest_hash(parent, value, &block)
      else
        raise "Wat #{value.inspect}"
    end
  end

end

module Facade
  include Parameters
  include Utilities

  N = 22
  INFEST_RANGE = (1..N).to_enum.lazy

  def infestations
    Manifest
      .new(manifest_pathname)
      .infestations
      .flat_map(&method(:infestation_handlers_for))
  end

  def infestations_iterable
    Iterable.new(infestations)
  end

  def infestation_handlers_for(template)
    infestations = INFEST_RANGE.map { |n| InfestationHandler.new(template, n) }
    collector = [CollectorTemplateHandler.new(template, 0)]
    lazy_plus(
        infestations,
        collector,
        )
  end

  def plain
    Manifest
      .new(manifest_pathname)
      .plain
      .flat_map(&method(:plain_handlers_for))
  end

  def plain_iterable
    Iterable.new(plain)
  end

  def plain_handlers_for(template)
    PlainTemplateHandler.new(template)
  end

  extend self
end


class TemplateHandler
  include Parameters
  include Utilities

  def initialize(template)
    @template = template.dup.freeze
  end
  attr_reader :template

  def root
    manifest_pathname.dirname
  end

  def resource_pathname
    root + @template
  end

  def source
    resource(resource_pathname)&.read
  end

  def context
    binding
  end

  def rendered
    ERB.new(source).result(context)
  end

end

class InfestationHandler < TemplateHandler

  def initialize(template, n)
    super(template)
    @n = n
  end
  attr_reader :n

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
    (1..Facade::N).map { |n| InfestationHandler.new(@template, n) }
  end

  def handle!
    instantiation_destination.tap do |dst|
      FileUtils::Verbose.mkdir_p dst.dirname
      dst.open('w') { |fout| fout.write(rendered) }
    end
  end

end

class CollectorTemplateHandler < InfestationHandler

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

class PlainTemplateHandler < TemplateHandler
  def plain_destination
    destination_dir_pathname + @template
  end

  def handle!
    plain_destination.tap do |dst|
      FileUtils::Verbose.mkdir_p dst.dirname
      dst.open('w') { |fout| fout.write(rendered) }
    end
  end
end

Facade
