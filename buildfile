require 'buildr/git_auto_version'
require 'buildr/gpg'

desc 'streak: Reactive Stream Experiments'
define 'streak' do
  project.group = 'org.realityforge.streak'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/streak')
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
            :windowtitle => 'Streak API Documentation',
            :linksource => true,
            :timestamp => false,
            :link => %w(https://arez.github.io/api https://docs.oracle.com/javase/8/docs/api)
  )

  cleanup_javadocs(project, 'streak')

  package(:jar)
  package(:sources)
  package(:javadoc)

  ipr.extra_modules << 'support/processor/processor.iml'
  iml.excluded_directories << project._('tmp')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development')

  ipr.add_component_from_artifact(:idea_codestyle)
end

desc 'Streak Support Annotation processor'
define 'processor', :base_dir => "#{WORKSPACE_DIR}/support/processor" do
  project.group = 'org.realityforge.streak.support'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  pom.dependency_filter = Proc.new {|_| false}

  compile.with :autoservice,
               :autocommon,
               :javapoet,
               :guava,
               :javax_annotation

  test.with :compile_testing,
            :junit,
            :hamcrest_core,
            Java.tools_jar,
            :truth

  package(:jar)

  project.no_ipr
end

task('streak:idea' => 'processor:idea')
