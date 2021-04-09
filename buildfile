require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/single_intermediate_layout'
require 'buildr/top_level_generate_dir'
require 'buildr/gwt'
require 'buildr/jacoco'

Buildr::MavenCentral.define_publish_tasks(:profile_name => 'org.realityforge', :username => 'realityforge')

GWT_EXAMPLES =
  {
    'WebSocketExample' => 'web_socket'
  }

SPRITZ_TEST_OPTIONS =
  {
    'braincheck.environment' => 'development',
    'zemeckis.environment' => 'development',
    'spritz.environment' => 'development'
  }

desc 'Spritz: A browser based, reactive event streaming library that is best used when coordinating events'
define 'spritz' do
  project.group = 'org.realityforge.spritz'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all,-processing,-serial'
  project.compile.options.warnings = true
  project.compile.options.other = %w(-Werror -Xmaxerrs 10000 -Xmaxwarns 10000)

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('spritz/spritz')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'Core Event Streaming Library'
  define 'core' do
    project.compile.options[:processor_path] << project('processor').package(:jar)
    project.compile.options[:processor_path] << project('processor').compile.dependencies

    compile.with :javax_annotation,
                 :braincheck_core,
                 :grim_annotations,
                 :zemeckis,
                 :jetbrains_annotations,
                 :jsinterop_annotations,
                 :jsinterop_base,
                 :akasha

    gwt_enhance(project)

    package(:jar)
    package(:sources)
    package(:javadoc)

    test.using :testng
    test.compile.with :braincheck_testng, :jdepend, :javax_json

    test.options[:properties] =
      SPRITZ_TEST_OPTIONS.merge('spritz.core.compile_target' => compile.target.to_s,
                                'spritz.diagnostic_messages_file' => _('src/test/java/spritz/diagnostic_messages.json'))
    test.options[:java_args] = ['-ea']
  end

  desc 'Spritz Examples'
  define 'examples' do
    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 :gwt_user

    gwt_modules = {}
    GWT_EXAMPLES.each_pair do |gwt_module, path|
      gwt_modules["spritz.examples.dom.#{path}.#{gwt_module}"] = false
    end
    iml.add_gwt_facet(gwt_modules,
                      :settings => { :compilerMaxHeapSize => '1024' },
                      :gwt_dev_artifact => :gwt_dev)
    project.jacoco.enabled = false
  end

  desc 'Spritz Support Annotation processor'
  define 'processor' do
    compile.with :autoservice,
                 :autocommon,
                 :javapoet,
                 :guava,
                 :guava_failureaccess,
                 :javax_annotation,
                 :javax_json

    test.using :testng
    test.options[:properties] = { 'spritz.fixture_dir' => _('src/test/resources') }

    test.with :compile_testing,
              :junit,
              :hamcrest_core,
              :truth,
              :errorprone,
              :error_prone_annotations

    test.compile.enhance(['copy-annotations'])
    task 'copy-annotations' do
      source_dir = "#{WORKSPACE_DIR}/core/src/main/java/spritz"
      target_dir = _('src/test/resources/input/spritz')
      FileUtils.mkdir_p target_dir
      FileUtils.cp "#{source_dir}/DocCategory.java", target_dir
      FileUtils.cp "#{source_dir}/GwtIncompatible.java", target_dir
      FileUtils.cp "#{source_dir}/MetaDataSource.java", target_dir
      FileUtils.cp "#{source_dir}/SourceCategory.java", target_dir
    end

    package(:jar)

    iml.test_source_directories << _('src/test/resources/input')
  end

  desc 'Test Spritz in downstream projects'
  define 'downstream-test' do
    compile.with :gir,
                 :javax_annotation

    test.options[:properties] =
      SPRITZ_TEST_OPTIONS.merge(
        'spritz.prev.version' => ENV['PREVIOUS_PRODUCT_VERSION'] || project.version,
        'spritz.next.version' => ENV['PRODUCT_VERSION'] || project.version,
        'spritz.deploy_test.fixture_dir' => _('src/test/resources/fixtures').to_s,
        'spritz.deploy_test.work_dir' => _(:target, 'deploy_test/workdir').to_s
      )
    test.options[:java_args] = ['-ea']

    local_test_repository_url = URI.join('file:///', project._(:target, :local_test_repository)).to_s
    compile.enhance do
      projects_to_upload = projects(%w(core))
      old_release_to = repositories.release_to
      begin
        # First we install them in a local repository so we don't have to access the network during local builds
        repositories.release_to = local_test_repository_url
        projects_to_upload.each do |prj|
          prj.packages.each do |pkg|
            # Uninstall version already present in local maven cache
            pkg.uninstall
            # Install version into local repository
            pkg.upload
          end
        end
        if ENV['STAGE_RELEASE'] == 'true'
          # Then we install it to a remote repository so that TravisCI can access the builds when it attempts
          # to perform a release
          repositories.release_to =
            { :url => 'https://stocksoftware.jfrog.io/stocksoftware/staging', :username => ENV['STAGING_USERNAME'], :password => ENV['STAGING_PASSWORD'] }
          projects_to_upload.each do |prj|
            prj.packages.each(&:upload)
          end
        end
      ensure
        repositories.release_to = old_release_to
      end
    end unless ENV['TEST'] == 'no' # These artifacts only required when running tests.

    test.compile.enhance do
      cp = project.compile.dependencies.map(&:to_s) + [project.compile.target.to_s]

      properties = {}
      # Take the version that we are releasing else fallback to project version
      properties['spritz.prev.version'] = ENV['PREVIOUS_PRODUCT_VERSION'] || project.version
      properties['spritz.next.version'] = ENV['PRODUCT_VERSION'] || project.version
      properties['spritz.deploy_test.work_dir'] = _(:target, 'deploy_test/workdir').to_s
      properties['spritz.deploy_test.fixture_dir'] = _('src/test/resources/fixtures').to_s
      properties['spritz.deploy_test.local_repository_url'] = local_test_repository_url
      properties['spritz.deploy_test.store_statistics'] = ENV['STORE_BUILD_STATISTICS'] == 'true'
      properties['spritz.deploy_test.build_before'] = (ENV['STORE_BUILD_STATISTICS'] != 'true' && ENV['BUILD_BEFORE'] != 'no')

      Java::Commands.java 'spritz.downstream.CollectBuildStats', { :classpath => cp, :properties => properties } unless ENV['BUILD_STATS'] == 'no'
    end

    # Only run this test when preparing for release, never on TravisCI (as produces different byte sizes)
    test.exclude '*BuildStatsTest' if ENV['PRODUCT_VERSION'].nil? || ENV['BUILD_STATS'] == 'no' || !ENV['TRAVIS_BUILD_NUMBER'].nil?
    test.exclude '*BuildOutputTest' if ENV['BUILD_STATS'] == 'no'

    test.using :testng
    test.compile.with :gwt_symbolmap,
                      :jetbrains_annotations

    project.jacoco.enabled = false
  end

  doc.from(projects(%w(core))).
    using(:javadoc,
          :windowtitle => 'Spritz API Documentation',
          :linksource => true,
          :overview => _('generated/javadocs/overview.html'),
          :timestamp => false,
          :link => %w(https://arez.github.io/api https://docs.oracle.com/javase/8/docs/api)
    ).sourcepath << project('core').compile.sources

  generate_overview(project)
  cleanup_javadocs(project, 'spritz')

  ipr.extra_modules << 'support/processor/processor.iml'
  iml.excluded_directories << project._('tmp')

  GWT_EXAMPLES.each_pair do |gwt_module, path|
    ipr.add_gwt_configuration(project,
                              :iml_name => 'examples',
                              :name => gwt_module,
                              :gwt_module => "spritz.examples.dom.#{path}.#{gwt_module}",
                              :start_javascript_debugger => false,
                              :open_in_browser => false,
                              :vm_parameters => '-Xmx2G',
                              :shell_parameters => "-strict -style PRETTY -XmethodNameDisplayMode FULL -nostartServer -incremental -codeServerPort 8889 -bindAddress 0.0.0.0 -deploy #{_(:generated, :gwt, 'deploy')} -extra #{_(:generated, :gwt, 'extra')} -war #{_(:generated, :gwt, 'war')}",
                              :launch_page => "http://127.0.0.1:8889/#{path}/index.html")
  end

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development -Dspritz.environment=development -Dspritz.output_fixture_data=false -Dspritz.fixture_dir=support/processor/src/test/resources -Dspritz.core.compile_target=target/spritz_core/idea/classes -Dspritz.diagnostic_messages_file=core/src/test/java/spritz/diagnostic_messages.json')

  ipr.add_testng_configuration('core',
                               :module => 'core',
                               :jvm_args => '-ea -Dbraincheck.environment=development -Dspritz.environment=development -Dspritz.output_fixture_data=false -Dspritz.core.compile_target=../target/spritz_core/idea/classes -Dspritz.check_diagnostic_messages=false -Dspritz.diagnostic_messages_file=src/test/java/spritz/diagnostic_messages.json')
  ipr.add_testng_configuration('core - update invariant messages',
                               :module => 'core',
                               :jvm_args => '-ea -Dbraincheck.environment=development -Dspritz.environment=development -Dspritz.output_fixture_data=true -Dspritz.core.compile_target=../target/spritz_core/idea/classes -Dspritz.check_diagnostic_messages=true -Dspritz.diagnostic_messages_file=src/test/java/spritz/diagnostic_messages.json')

  ipr.add_component_from_artifact(:idea_codestyle)
  ipr.add_code_insight_settings
  ipr.add_nullable_manager
  ipr.add_javac_settings('-Xlint:all,-processing,-serial')
end

# Avoid uploading any packages except those we explicitly allow
Buildr.projects.each do |project|
  unless %w(spritz:core).include?(project.name)
    project.task('upload').actions.clear
  end
end
