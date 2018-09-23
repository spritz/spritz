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
               :braincheck

  test.using :testng

  package(:jar)
  package(:sources)
  package(:javadoc)

  iml.excluded_directories << project._('tmp')

  ipr.add_component_from_artifact(:idea_codestyle)
end
