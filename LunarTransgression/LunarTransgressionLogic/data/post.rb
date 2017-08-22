require 'csv'

puts "Checking for duplicate pokemon skills"

map = {}
CSV.foreach('output/character_skill.csv', :headers => true) do |row|
    name = row[0]
    poke = map[name] || (map[name]={})
    skill = row[1]
    level = row[2]
    if (poke[skill])
        if (poke[skill].to_i < 2)
            poke[skill] = level            
        end
    else
        poke[skill] = level
    end
end

CSV.open('output/character_skill.csv', "wb", :write_headers => true, :headers => ['characterId','skillId','level']) do |csv|
    map.each_pair do |poke, skills|
        skills.each_pair do |skill, level|
            csv << [poke, skill, level]
        end
    end
end

"Done"
