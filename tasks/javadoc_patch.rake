class Buildr::Doc::Javadoc
  def generate(sources, target, options = {})
    cmd_args = ['-d', target, trace?(:javadoc) ? '-verbose' : '-quiet']
    options.reject {|key, value| [:sourcepath, :classpath].include?(key)}.
      each {|key, value| value.invoke if value.respond_to?(:invoke)}.
      each do |key, value|
      case value
      when true, nil
        cmd_args << "-#{key}"
      when false
        cmd_args << "-no#{key}"
      when Hash
        value.each {|k, v| cmd_args << "-#{key}" << k.to_s << v.to_s}
      else
        cmd_args += Array(value).map {|item| ["-#{key}", item.to_s]}.flatten
      end
    end
    [:sourcepath, :classpath].each do |option|
      Array(options[option]).flatten.tap do |paths|
        paths = project.compile.sources if :sourcepath == option && options[option].nil?
        cmd_args << "-#{option}" << paths.flatten.map(&:to_s).join(File::PATH_SEPARATOR) unless paths.empty?
      end
    end
    if (options[:sourcepath].nil? || 0 == options[:sourcepath].length) && options[:subpackages].nil?
      cmd_args += sources.flatten.uniq
    end
    unless Buildr.application.options.dryrun
      info "Generating Javadoc for #{project.name}"
      trace (['javadoc'] + cmd_args).join(' ')
      Java.load
      Java.com.sun.tools.javadoc.Main.execute(cmd_args.to_java(Java.java.lang.String)) == 0 or
        fail 'Failed to generate Javadocs, see errors above'
    end
  end
end
