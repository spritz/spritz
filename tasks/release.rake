require File.expand_path(File.dirname(__FILE__) + '/util')
require File.expand_path(File.dirname(__FILE__) + '/release_tool.rb')

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
  t.stage('PatchChangelog', 'Patch the changelog to update from previous release') do
    changelog = IO.read('CHANGELOG.md')
    from = '0.00' == ENV['PREVIOUS_PRODUCT_VERSION'] ? `git rev-list --max-parents=0 HEAD`.strip : "v#{ENV['PREVIOUS_PRODUCT_VERSION']}"

    header = "### [v#{ENV['PRODUCT_VERSION']}](https://github.com/spritz/spritz/tree/v#{ENV['PRODUCT_VERSION']}) (#{ENV['RELEASE_DATE']}) · [Full Changelog](https://github.com/spritz/spritz/compare/#{from}...v#{ENV['PRODUCT_VERSION']})"

    api_diff_filename = "#{WORKSPACE_DIR}/api-test/src/test/resources/fixtures/#{ENV['PREVIOUS_PRODUCT_VERSION']}-#{ENV['PRODUCT_VERSION']}.json"
    if File.exist?(api_diff_filename)
      # TODO: When we actually have a website we can add the next section back in
      #header += " · [API Differences](https://spritz.github.io/api-diff?key=spritz&old=#{ENV['PREVIOUS_PRODUCT_VERSION']}&new=#{ENV['PRODUCT_VERSION']})"

      changes = JSON.parse(IO.read(api_diff_filename))
      non_breaking_changes = changes.select { |j| j['classification']['SOURCE'] == 'NON_BREAKING' }.size
      potentially_breaking_changes = changes.select { |j| j['classification']['SOURCE'] == 'POTENTIALLY_BREAKING' }.size
      breaking_changes = changes.select { |j| j['classification']['SOURCE'] == 'BREAKING' }.size
      change_descriptions = []
      change_descriptions << "#{non_breaking_changes} non breaking API change#{1 == non_breaking_changes ? '' : 's'}" unless 0 == non_breaking_changes
      change_descriptions << "#{potentially_breaking_changes} potentially breaking API change#{1 == potentially_breaking_changes ? '' : 's'}" unless 0 == potentially_breaking_changes
      change_descriptions << "#{breaking_changes} breaking API change#{1 == breaking_changes ? '' : 's'}" unless 0 == breaking_changes

      if change_descriptions.size > 0
        description = "The release includes "
        if 1 == change_descriptions.size
          description += "#{change_descriptions[0]}"
        elsif 2 == change_descriptions.size
          description += "#{change_descriptions[0]} and #{change_descriptions[1]}"
        else
          description += "#{change_descriptions[0]}, #{change_descriptions[1]} and #{change_descriptions[2]}"
        end

        header += "\n\n#{description}."
      end
    end
    header += "\n"

    header += <<CONTENT

Changes in this release:
CONTENT

    IO.write('CHANGELOG.md', changelog.gsub("### Unreleased\n", header))
    sh 'git reset 2>&1 1> /dev/null'
    sh 'git add CHANGELOG.md'
    sh 'git commit -m "Update CHANGELOG.md in preparation for release"'
  end
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
