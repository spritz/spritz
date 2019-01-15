def generate_overview(project)
  project.doc.enhance(['generate-overview'])
  project.task 'generate-overview' => [project.compile.target] do
    require 'json'

    operators_by_name = {}
    operators_by_category = {}

    Dir["#{WORKSPACE_DIR}/generated/processors/main/java/**/*.doc.json"].each do |file|
      data = JSON.parse(IO.read(file, :encoding => 'UTF-8'))
      data['operators'].each do |operator|
        operator = operator.merge('class' => data['class'])
        operators_by_name[operator['name']] = operator
        operator['categories'].each do |category|
          (operators_by_category[category] ||= []) << operator
        end
      end if data['operators']
    end

    operators_content = ''

    operators_by_category.each_pair do |category_name, operators|
      operators_content += <<-HTML
<h3><a name="#{category_name.downcase}">#{titleize(category_name)}</a></h3>

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

    file_content = IO.read("#{WORKSPACE_DIR}/src/main/java/overview.html", :encoding => 'UTF-8')
    file_content.gsub!(/@@OPERATORS@@/, operators_content)

    target_dir = project._('generated/javadocs')
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
