package com.github.blutorange.translune.serial;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.sfm.csv.CsvParser;

import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Resource;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.util.Constants;

@Singleton
public final class ImportProcessing {

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	public ImportProcessing() {

	}

	public void importDataSet(final ZipFile zipFile) throws IOException {
		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		final Set<CharacterCsvModel> characterModels = new HashSet<>();
		final Set<SkillCsvModel> skillModel = new HashSet<>();
		final Set<CharacterToSkillCsvModel> characterToSkillModels = new HashSet<>();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			if (entry == null)
				continue;
			final File file = new File(entry.getName());
			// TODO upload files, check if they exist
			if (file.getParent() == Constants.IMPORT_DIR_CHARACTER_CRY) {
				uploadFile(entry, Constants.FILE_PREFIX_CHARACTER_CRY);
			} else if (file.getParent() == Constants.IMPORT_DIR_CHARACTER_IMG) {

			} else {
				switch (file.getName()) {
				case Constants.IMPORT_FILE_CHARACTER:
					characterModels.addAll(readCharacterCsv(zipFile.getInputStream(entry)));
					break;
				case Constants.IMPORT_FILE_CHARACTER_SKILL:
					characterToSkillModels.addAll(readCharacterToSkillCsv(zipFile.getInputStream(entry)));
					break;
				case Constants.IMPORT_FILE_SKILL:
					skillModel.addAll(readSkillCsv(zipFile.getInputStream(entry)));
					break;
				}
			}
		}
		Set<Skill> skills = skillModel.stream().map(SkillCsvModel::toEntity).collect(Collectors.toSet());
		attachSkills(characterModels, skills, characterToSkillModels);
		Set<Character> characters = characterModels.stream().map(CharacterCsvModel::toEntity)
				.collect(Collectors.toSet());
		writeToDb(characters, skills);
	}

	public void uploadFile(ZipEntry entry, String prefix) {
		Resource resource = new Resource();
		File file = new File(entry.getName());
		resource.setMime(mime);
		// TODO strip extension
		resource.setName(prefix + file.getName());
		resource.setData(entry.get);
		databaseManager.persist(resource);
	}

	private void writeToDb(Set<Character> characters, Set<Skill> skills) {
		databaseManager.persist(characters);
		databaseManager.persist(skills);
		databaseManager.flush();
	}

	private Set<CharacterCsvModel> readCharacterCsv(final InputStream inputStream) throws IOException {
		return readTCsv(inputStream, CharacterCsvModel.class);
	}

	private Set<SkillCsvModel> readSkillCsv(final InputStream inputStream) throws IOException {
		return readTCsv(inputStream, SkillCsvModel.class);
	}

	private Set<CharacterToSkillCsvModel> readCharacterToSkillCsv(final InputStream inputStream) throws IOException {
		return readTCsv(inputStream, CharacterToSkillCsvModel.class);
	}

	private <T> Set<T> readTCsv(final InputStream inputStream, Class<T> clazz) throws IOException {
		try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
			return CsvParser.quote('\'').separator(';').mapTo(clazz).stream(reader).collect(Collectors.toSet());
		}
	}

	private static void attachSkills(Set<CharacterCsvModel> characters, Set<Skill> skills,
			Set<CharacterToSkillCsvModel> charSkills) {
		Map<String, CharacterCsvModel> charMap = characters.stream()
				.collect(Collectors.toMap(CharacterCsvModel::getKey, Function.identity()));
		Map<String, Skill> skillMap = skills.stream().collect(Collectors.toMap(Skill::getName, Function.identity()));
		for (CharacterToSkillCsvModel charSkill : charSkills) {
			int level = charSkill.getLevel();
			CharacterCsvModel character = charMap.get(charSkill.getCharacterId());
			Skill skill = skillMap.get(charSkill.getSkillId());
			character.addSkill(level, skill);
		}
	}

}
