WORKSPACE_DIR = File.expand_path(File.dirname(__FILE__) + '/..')

# Project -> [Branch1, Branch2, ...]
DOWNSTREAM_EXAMPLES =
  {
    'react4j-todomvc' => %w(spritz)
  }

def in_dir(dir)
  current = Dir.pwd
  begin
    Dir.chdir(dir)
    yield
  ensure
    Dir.chdir(current)
  end
end
