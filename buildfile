require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/top_level_generate_dir'

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
                 project('processor').compile.dependencies

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
  end

  desc 'Elemental2 Event Streaming Library Integration'
  define 'elemental2' do
    project.enable_annotation_processor = true

    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 :elemental2_core,
                 :elemental2_dom,
                 :elemental2_promise

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
  end

  desc 'Spritz Examples'
  define 'examples' do
    compile.with project('core').package(:jar),
                 project('core').compile.dependencies
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
              Java.tools_jar,
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

  doc.from(projects(%w(core elemental2))).
    using(:javadoc,
          :windowtitle => 'Spritz API Documentation',
          :linksource => true,
          :overview => _('generated/javadocs/overview.html'),
          :timestamp => false,
          :exclude => 'spritz.internal',
          :subpackages => 'spritz',
          :link => %w(https://arez.github.io/api https://docs.oracle.com/javase/8/docs/api)
    ).sourcepath << project('core').compile.sources << project('elemental2').compile.sources

  generate_overview(project)
  cleanup_javadocs(project, 'spritz')
  #gwt_enhance(project)

  ipr.extra_modules << 'support/processor/processor.iml'
  iml.excluded_directories << project._('tmp')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development -Dspritz.output_fixture_data=false -Dspritz.fixture_dir=support/processor/src/test/resources')

  ipr.add_component_from_artifact(:idea_codestyle)
end
