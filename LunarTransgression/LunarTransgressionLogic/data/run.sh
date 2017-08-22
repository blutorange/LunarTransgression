#! /bin/bash

rm -f dexnumber_name.csv
rm -f status_skills.csv
rm -f output/character.csv
rm -f output/skill.csv
rm -f output/character_skill.csv

sqlite3 veekun-pokedex.sqlite ".read sql.sql"

ruby post.rb
