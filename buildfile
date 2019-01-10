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

  compile.with :javax_annotation,
               :braincheck,
               :jsinterop_annotations,
               :jsinterop_base

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

  iml.excluded_directories << project._('tmp')

  ipr.add_component_from_artifact(:idea_codestyle)
end
