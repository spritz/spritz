require File.expand_path(File.dirname(__FILE__) + '/util')
require 'buildr/release_tool.rb'

Buildr::ReleaseTool.define_release_task do |t|
  t.extract_version_from_changelog
  t.zapwhite
  t.ensure_git_clean
  t.cleanup_staging
  t.build
  t.stage('ArchiveDownstream', 'Archive downstream projects that may need changes pushed') do
    FileUtils.rm_rf 'archive'
    FileUtils.mkdir_p 'archive'
    mv 'target/spritz_downstream-test/deploy_test/workdir', 'archive/downstream'
  end
  t.patch_changelog('spritz/spritz', :api_diff_directory => "#{WORKSPACE_DIR}/api-test")
  t.tag_project
  t.stage_release(:release_to => { :url => 'https://stocksoftware.jfrog.io/stocksoftware/staging', :username => ENV['STAGING_USERNAME'], :password => ENV['STAGING_PASSWORD'] })
  t.maven_central_publish(:additional_tasks => 'site:publish_if_tagged')
  t.patch_changelog_post_release
  t.push_changes
  t.stage('PushDownstreamChanges', 'Push changes to downstream examples git repository') do
    # Push the changes that have been made locally in downstream projects.
    # Artifacts have been pushed to staging repository by this time so they should build
    # even if it has not made it through the Maven release process
    DOWNSTREAM_EXAMPLES.each_pair do |downstream_example, branches|
      sh "cd archive/downstream/#{downstream_example} && git push --all"
      branches.each do |branch|
        full_branch = "#{branch}-SpritzUpgrade-#{ENV['PRODUCT_VERSION']}"
        `cd archive/downstream/#{downstream_example} && git push origin :#{full_branch} 2>&1`
        puts "Completed remote branch #{downstream_example}/#{full_branch}. Removed." if 0 == $?.exitstatus
      end
    end

    FileUtils.rm_rf 'archive'
  end
  t.github_release('spritz/spritz')
end
