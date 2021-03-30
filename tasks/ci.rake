desc 'Continuous Integration task'
task 'ci' do
  Buildr::ReleaseTool.derive_versions_from_changelog
  sh "bundle exec buildr package doc jacoco:report PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']}"
end
