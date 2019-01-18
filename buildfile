require 'buildr/git_auto_version'
require 'buildr/gpg'
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

  project.enable_annotation_processor = true

  compile.with :javax_annotation,
               :braincheck,
               :jsinterop_annotations,
               :jsinterop_base,
               project('processor').package(:jar),
               project('processor').compile.dependencies

  test.using :testng

  doc.using(:javadoc,
            :windowtitle => 'Spritz API Documentation',
            :linksource => true,
            :overview => _('generated/javadocs/overview.html'),
            :timestamp => false,
            :exclude => 'spritz.internal:spritz.examples',
            :subpackages => 'spritz',
            :link => %w(https://arez.github.io/api https://docs.oracle.com/javase/8/docs/api)
  ).exclude(*Dir["#{_(:source, :main, :java, 'spritz/examples')}/*.java"]).sourcepath << project.compile.sources

  generate_overview(project)

  cleanup_javadocs(project, 'spritz')
  #gwt_enhance(project)

  package(:jar)
  package(:sources)
  package(:javadoc)

  ipr.extra_modules << 'support/processor/processor.iml'
  iml.excluded_directories << project._('tmp')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development -Dspritz.output_fixture_data=false -Dspritz.fixture_dir=support/processor/src/test/resources')

  ipr.add_component_from_artifact(:idea_codestyle)
end

desc 'Spritz Support Annotation processor'
define 'processor', :base_dir => "#{WORKSPACE_DIR}/support/processor" do
  project.group = 'org.realityforge.spritz.support'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  pom.dependency_filter = Proc.new {|_| false}

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
    FileUtils.cp Dir["#{WORKSPACE_DIR}/src/main/java/spritz/internal/annotations/*.java"], target_dir
  end

  package(:jar)

  iml.test_source_directories << _('src/test/resources/input')
  project.no_ipr
end

task('spritz:idea' => 'processor:idea')
