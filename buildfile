require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/top_level_generate_dir'
require 'buildr/gwt'

GWT_EXAMPLES = %w()

desc 'Spritz: A browser based, reactive event streaming library that is best used when coordinating events'
define 'spritz' do
  project.group = 'org.realityforge.spritz'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('spritz/spritz')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'Core Event Streaming Library'
  define 'core' do
    project.enable_annotation_processor = true

    compile.with :javax_annotation,
                 :braincheck,
                 :jsinterop_annotations,
                 :jsinterop_base,
                 project('processor').package(:jar),
                 project('processor').compile.dependencies,
                 :elemental2_core,
                 :elemental2_dom,
                 :elemental2_promise
    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
  end

  desc 'Spritz Examples'
  define 'examples' do
    compile.with project('core').package(:jar),
                 project('core').compile.dependencies

    gwt_modules = {}
    GWT_EXAMPLES.each do |gwt_module|
      gwt_modules[gwt_module] = false
    end
    iml.add_gwt_facet(gwt_modules,
                      :settings => { :compilerMaxHeapSize => '1024' },
                      :gwt_dev_artifact => :gwt_dev)
  end

  desc 'Spritz Support Annotation processor'
  define 'processor' do

    compile.with :autoservice,
                 :autocommon,
                 :javapoet,
                 :guava,
                 :javax_annotation,
                 :javax_json

    test.using :testng
    test.options[:properties] = { 'spritz.fixture_dir' => _('src/test/resources') }

    test.with :compile_testing,
              :junit,
              :hamcrest_core,
              :truth

    test.compile.enhance(['copy-annotations'])
    task 'copy-annotations' do
      target_dir = _('src/test/resources/input/spritz/internal/annotations')
      FileUtils.mkdir_p target_dir
      FileUtils.cp Dir["#{WORKSPACE_DIR}/core/src/main/java/spritz/internal/annotations/*.java"], target_dir
    end

    package(:jar)

    iml.test_source_directories << _('src/test/resources/input')
  end

  doc.from(projects(%w(core))).
    using(:javadoc,
          :windowtitle => 'Spritz API Documentation',
          :linksource => true,
          :overview => _('generated/javadocs/overview.html'),
          :timestamp => false,
          :exclude => 'spritz.internal',
          :subpackages => 'spritz',
          :link => %w(https://arez.github.io/api https://docs.oracle.com/javase/8/docs/api)
    ).sourcepath << project('core').compile.sources

  generate_overview(project)
  cleanup_javadocs(project, 'spritz')

  ipr.extra_modules << 'support/processor/processor.iml'
  iml.excluded_directories << project._('tmp')

  GWT_EXAMPLES.each do |gwt_module|
    short_name = gwt_module.gsub(/.*\./, '')
    ipr.add_gwt_configuration(project,
                              :iml_name => 'example',
                              :name => short_name,
                              :gwt_module => gwt_module,
                              :start_javascript_debugger => false,
                              :open_in_browser => false,
                              :vm_parameters => "-Xmx3G -Djava.io.tmpdir=#{_("tmp/gwt/#{short_name}")}",
                              :shell_parameters => "-port 8888 -codeServerPort 8889 -bindAddress 0.0.0.0 -war #{_(:generated, 'gwt-export', short_name)}/",
                              :launch_page => "http://127.0.0.1:8888/#{gwt_module}/")
  end

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development -Dspritz.environment=development -Dspritz.output_fixture_data=false -Dspritz.fixture_dir=support/processor/src/test/resources')

  ipr.add_component_from_artifact(:idea_codestyle)
end
