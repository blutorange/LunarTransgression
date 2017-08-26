require 'csv'
require 'json'

def render_desc(field_map, desc, row)
    desc.gsub!(/\$([a-z_]+)/) do
        row[field_map[$1]] || $1
    end
    desc.gsub!(/\[([^\]]*)\]\{([^:\}]+):([^:\}]+)\}/) do
        $1.size > 0 ? $1 : "#{$2} #{$3}"
    end
    desc.gsub(/\s+/, ' ')
end

puts "Checking for duplicate pokemon skills"
enum = CSV.foreach('output/character_skill.csv', :headers => true)
headers = enum.first.map(&:first)
map = {}
enum.drop(1).each do |row|
    name = row["characterId"]
    poke = map[name] || (map[name]={})
    skill = row["skillId"]
    level = row["level"]
    if (poke[skill])
        if (poke[skill].to_i < 2)
            poke[skill] = level            
        end
    else
        poke[skill] = level
    end
end
CSV.open('output/character_skill.csv', "wb", :write_headers => true,
        :col_sep => ',', :quote_char => '"',
        :headers => headers) do |csv|
    map.each_pair do |poke, skills|
        skills.each_pair do |skill, level|
            csv << [poke, skill, level]
        end
    end
end

puts "Converting stage changes to JSON"
enum = CSV.foreach('output/skill.csv', :headers => true, :return_headers => true)
headers = enum.first.map(&:first)
field_map_skill =  {
    "effect_chance" => "stageChance"
}
data=enum.drop(1).each_with_index.map do |row,i|
    raw = row["stageChanges"]
    if (raw)
        stages,changes = *raw.split(?:).map{|x|x.split(?,)}
        row["stageChanges"] = JSON.generate(Hash[*stages.zip(changes).flatten])
    end
    row["description"] = render_desc(field_map_skill, row["description"], row)
    row
end
CSV.open('output/skill.csv', "wb", :write_headers => true,
        :col_sep => ',', :quote_char => '"', :headers => headers) do |csv|
    data.each do |row|
        csv << row
    end
end


"Done"
