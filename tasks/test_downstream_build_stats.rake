desc 'Update the statistics stored for next version'
task 'update_downstream_build_stats' do
  Buildr::ReleaseTool.derive_versions_from_changelog

  sh "buildr clean spritz:downstream-test:test:compile TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']} STORE_BUILD_STATISTICS=true"
end

desc 'Test the statistics stored for next version'
task 'test_downstream_build_stats' do
  Buildr::ReleaseTool.derive_versions_from_changelog

  sh "buildr clean spritz:downstream-test:test TEST=only GWT=no PRODUCT_VERSION=#{ENV['PRODUCT_VERSION']} PREVIOUS_PRODUCT_VERSION=#{ENV['PREVIOUS_PRODUCT_VERSION']}"
end
