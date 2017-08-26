#! /bin/bash

# This needs the database file veekun-pokedex.sqlite
# Download it from https://veekun.com/dex/downloads
# Also, you need to place sprites and cries in
# character-img, character-icon and character-cry

rm -f dexnumber_name.csv
rm -f status_skills.csv
rm -f output/character.csv
rm -f output/skill.csv
rm -f output/character_skill.csv

sqlite3 veekun-pokedex.sqlite ".read sql.sql"

ruby post.rb
