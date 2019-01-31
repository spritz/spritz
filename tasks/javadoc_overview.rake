def generate_overview(project)
  projects = %w(core elemental2)
  project.doc.enhance(['generate-overview'])
  desc 'Generate overview.html page'
  project.task 'generate-overview' => projects.collect {|project_name| project(project_name).compile.target} do
    require 'json'

    operators_by_name = {}
    operators_by_category = {}
    categories = []
    categories_by_name = {}
    projects.each do |project_name|
      Dir["#{WORKSPACE_DIR}/#{project_name}/generated/processors/main/java/**/*.doc.json"].each do |file|
        data = JSON.parse(IO.read(file, :encoding => 'UTF-8'))
        data['operators'].each do |operator|
          operator = operator.merge('class' => data['class'])
          operators_by_name[operator['name']] = operator
          operator['categories'].each do |category|
            (operators_by_category[category] ||= []) << operator
          end
        end if data['operators']

        categories = data['categories'] if data['categories']
      end
    end
    categories.each do |category|
      categories_by_name[category['name']] = category
    end

    operators_content = ''

    operators_content += <<-HTML
<ul>
    HTML
    categories.each do |category|
      operators_content += <<-HTML
  <li><a href="##{category['name'].downcase}">#{titleize(category['name'])}</a>: #{category['description']}</li>
      HTML
    end
    operators_content += <<-HTML
</ul>

    HTML

    categories.each do |category|
      operators = operators_by_category[category['name']]
      operators_content += <<-HTML
<h3><a name="#{category['name'].downcase}">#{titleize(category['name'])}</a></h3>

<p>#{categories_by_name[category['name']]['description']}</p>

<ul>
      HTML
      operators.each do |operator|
        operators_content += <<-HTML
  <li>{@link #{operator["link"]} #{operator["name"]}}: #{operator["description"]}</li>
        HTML
      end
      operators_content += <<-HTML
</ul>

      HTML
    end

    file_content = IO.read("#{WORKSPACE_DIR}/core/src/main/java/overview.html", :encoding => 'UTF-8')
    file_content.gsub!(/@@OPERATORS@@/, operators_content)

    target_dir = "#{WORKSPACE_DIR}/generated/javadocs"
    FileUtils.mkdir_p target_dir
    IO.write("#{target_dir}/overview.html", file_content, :encoding => 'UTF-8')
  end
end

def titleize(input_word)
  split_into_words(input_word).collect {|part| part[0...1].upcase + part[1..-1].downcase}.join(' ')
end

def split_into_words(word)
  word = word.to_s.dup
  word.gsub!(/^[_-]/, '')
  word.gsub!(/([A-Z]+)([A-Z][a-z])/, '\1_\2')
  word.gsub!(/([a-z\d])([A-Z])/, '\1_\2')
  word.tr!('-', '_')
  word.split('_')
end
