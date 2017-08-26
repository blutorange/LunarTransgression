package com.github.blutorange.translune.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.sfm.csv.CsvParser;
import org.slf4j.Logger;

import com.github.blutorange.common.StringUtil;
import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Resource;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.util.Constants;

@Singleton
public final class ImportProcessing implements IImportProcessing {

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	@Classed(ImportProcessing.class)
	Logger logger;

	@Inject
	MimetypesFileTypeMap mimetypesFileTypeMap;

	@Inject
	public ImportProcessing() {

	}

	@Override
	public int importDataSet(final ZipFile zipFile) throws IOException {
		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		final Set<CharacterCsvModel> characterModels = new HashSet<>();
		final Set<SkillCsvModel> skillModel = new HashSet<>();
		final Set<CharacterToSkillCsvModel> characterToSkillModels = new HashSet<>();
		final Map<String, ZipEntry> filesCry = new HashMap<>();
		final Map<String, ZipEntry> filesImg = new HashMap<>();
		final Map<String, ZipEntry> filesIcon = new HashMap<>();
		// Read data from the ZIP file.
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			if (entry == null)
				continue;
			final String name = entry.getName();
			if (name == null)
				continue;
			final String parent = FilenameUtils.getBaseName(FilenameUtils.getFullPathNoEndSeparator(entry.getName()));
			if (entry.isDirectory())
				continue;
			if (Constants.IMPORT_DIR_CHARACTER_CRY.equals(parent)) {
				logger.debug("found cry sound file " + entry.getName());
				filesCry.put(FilenameUtils.getName(name), entry);
			}
			else if (Constants.IMPORT_DIR_CHARACTER_IMG.equals(parent)) {
				logger.debug("found image file " + entry.getName());
				filesImg.put(FilenameUtils.getName(name), entry);
			}
			else if (Constants.IMPORT_DIR_CHARACTER_ICON.equals(parent)) {
				logger.debug("found image file " + entry.getName());
				filesIcon.put(FilenameUtils.getName(name), entry);
			}
			else {
				switch (StringUtil.toRootLowerCase(FilenameUtils.getName(name))) {
				case Constants.IMPORT_FILE_CHARACTER:
					logger.debug("found character data %s" + entry.getName());
					characterModels.addAll(readCharacterCsv(zipFile.getInputStream(entry)));
					break;
				case Constants.IMPORT_FILE_CHARACTER_SKILL:
					logger.debug("found character skill data %s" + entry.getName());
					characterToSkillModels.addAll(readCharacterToSkillCsv(zipFile.getInputStream(entry)));
					break;
				case Constants.IMPORT_FILE_SKILL:
					logger.debug("found skill data %s" + entry.getName());
					skillModel.addAll(readSkillCsv(zipFile.getInputStream(entry)));
					break;
				}
			}
		}
		// Add associations and write entities to the database.
		final Set<Skill> skills = skillModel.stream().map(SkillCsvModel::toEntity).collect(Collectors.toSet());
		attachSkills(characterModels, skills, characterToSkillModels);
		final Set<Character> characters = characterModels.stream().map(CharacterCsvModel::toEntity)
				.collect(Collectors.toSet());
		final List<Pair<ZipEntry, String>> requiredFiles = new ArrayList<>();
		for (final Character character : characters) {
			filterCharFile(filesImg, requiredFiles, character.getImgBack(), Constants.FILE_PREFIX_CHARACTER_IMG,
					character);
			filterCharFile(filesImg, requiredFiles, character.getImgFront(), Constants.FILE_PREFIX_CHARACTER_IMG,
					character);
			filterCharFile(filesIcon, requiredFiles, character.getImgIcon(), Constants.FILE_PREFIX_CHARACTER_ICON,
					character);
			filterCharFile(filesCry, requiredFiles, character.getCry(), Constants.FILE_PREFIX_CHARACTER_CRY, character);
		}
		logger.debug("writing imported files to database");
		writeImportToDb(requiredFiles, characters, skills, zipFile);
		return characters.size() + skills.size() + requiredFiles.size();
	}

	@Override
	public void uploadFile(final ZipFile zipFile, final ZipEntry entry, final String prefix) throws IOException {
		final Resource resource = resourceFromZipFile(zipFile, entry, prefix);
		final Boolean result = databaseManager.withEm(true, em -> {
			em.persist(resource);
			return Boolean.TRUE;
		});
		if (!Boolean.TRUE.equals(result))
			throw new IOException("failed to persist resource: " + entry.getName());
	}

	private void attachSkills(final Set<CharacterCsvModel> characters, final Set<Skill> skills,
			final Set<CharacterToSkillCsvModel> charSkills) {
		final Map<String, CharacterCsvModel> charMap = characters.stream()
				.collect(Collectors.toMap(CharacterCsvModel::getKey, Function.identity()));
		final Map<String, Skill> skillMap = skills.stream()
				.collect(Collectors.toMap(Skill::getName, Function.identity()));
		for (final CharacterToSkillCsvModel charSkill : charSkills) {
			final int level = charSkill.getLevel();
			final CharacterCsvModel character = charMap.get(charSkill.getCharacterId());
			final Skill skill = skillMap.get(charSkill.getSkillId());
			if (logger.isDebugEnabled()) {
				logger.debug("attempting to add character-skill association " + charSkill);
				logger.debug("found character " + (character == null ? "null" : character.getKey()));
				logger.debug("found skill " + (skill == null ? "null" : skill.getName()));
			}
			if (character == null)
				throw new IllegalArgumentException("Cannot add skill, specified character not found.");
			if (skill == null)
				throw new IllegalArgumentException("Cannot add skill, specified skill not found.");
			character.addSkill(level, skill);
		}
	}

	private void filterCharFile(final Map<String, ZipEntry> availableFiles,
			final List<Pair<ZipEntry, String>> requiredFiles, final String name, final String prefix,
			final Character character) throws IOException {
		if (!availableFiles.containsKey(name))
			throw new IOException(
					String.format("Character %s specifies the resource %s, but this file was not found in the archive.",
							character.getName(), name));
		requiredFiles.add(new ImmutablePair<>(availableFiles.get(name), prefix));
	}

	private Set<CharacterCsvModel> readCharacterCsv(final InputStream inputStream) throws IOException {
		return readTCsv(inputStream, CharacterCsvModel.class);
	}

	private Set<CharacterToSkillCsvModel> readCharacterToSkillCsv(final InputStream inputStream) throws IOException {
		return readTCsv(inputStream, CharacterToSkillCsvModel.class);
	}

	private Set<SkillCsvModel> readSkillCsv(final InputStream inputStream) throws IOException {
		return readTCsv(inputStream, SkillCsvModel.class);
	}

	private <T> Set<T> readTCsv(final InputStream inputStream, final Class<T> clazz) throws IOException {
		try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
			return CsvParser.quote('\"').separator(',').mapTo(clazz).stream(reader).collect(Collectors.toSet());
		}
	}

	private Resource resourceFromZipFile(final ZipFile zipFile, final ZipEntry entry, final String prefix)
			throws IOException {
		final Resource resource = new Resource();
		final String mime = mimetypesFileTypeMap.getContentType(entry.getName());
		resource.setMime(mime);
		resource.setName(prefix + FilenameUtils.getBaseName(entry.getName()));
		resource.setData(IOUtils.toByteArray(zipFile.getInputStream(entry)));
		resource.setFilename(FilenameUtils.getName(String.valueOf(entry.getName())));
		if (logger.isDebugEnabled())
			logger.debug("created resource " + resource);
		return resource;
	}

	private void writeImportToDb(final List<Pair<ZipEntry, String>> requiredFiles, final Set<Character> characters,
			final Set<Skill> skills, final ZipFile zipFile) throws IOException {
		final Throwable result = databaseManager.withEm(true, em -> {
			try {
				characters.forEach(character -> {
					if (em.find(Character.class, character.getPrimaryKey()) != null)
						em.merge(character);
					else
						em.persist(character);
				});
				skills.forEach(skill -> {
					if (em.find(Skill.class, skill.getPrimaryKey()) != null)
						em.merge(skill);
					else
						em.persist(skill);
				});
				em.flush();
				em.clear();
				for (final Pair<ZipEntry, String> pair : requiredFiles) {
					Resource resource = resourceFromZipFile(zipFile, pair.getLeft(), pair.getRight());
					if (em.find(Resource.class, resource.getPrimaryKey()) != null) {
						resource = em.merge(resource);
					}
					else
						em.persist(resource);
					// Make sure we do not load all files into RAM.
					em.flush();
					em.detach(resource);
				}
				return null;
			}
			catch (final Exception e) {
				return e;
			}
		});
		if (result != null)
			throw new IOException("Error while persisting data: " + result.getMessage(), result);
	}
}