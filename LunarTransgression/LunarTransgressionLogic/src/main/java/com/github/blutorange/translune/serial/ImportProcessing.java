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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.annotation.Nullable;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvParser;
import org.slf4j.Logger;

import com.github.blutorange.common.StringUtil;
import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Resource;
import com.github.blutorange.translune.db.Resource_;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.logic.EStatusCondition;
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

	@Nullable
	private AvailableBgAndBgm availableBgAndBgm;

	@Inject
	public ImportProcessing() {

	}

	@Override
	public AvailableBgAndBgm availableBgAndBgm() throws IOException {
		AvailableBgAndBgm availableBgAndBgm = this.availableBgAndBgm;
		if (availableBgAndBgm != null)
			return availableBgAndBgm;
		synchronized (logger) {
			availableBgAndBgm = this.availableBgAndBgm;
			if (availableBgAndBgm != null)
				return availableBgAndBgm;
			final AvailableBgAndBgm result = databaseManager.withEm(false, em -> {
				final Map<String, Set<String>> bgMenu = retrieveAvailable(em, Constants.FILE_PREFIX_BG_MENU);
				final Map<String, Set<String>> bgmMenu = retrieveAvailable(em, Constants.FILE_PREFIX_BGM_MENU);
				final Map<String, Set<String>> bgBattle = retrieveAvailable(em, Constants.FILE_PREFIX_BG_BATTLE);
				final Map<String, Set<String>> bgmBattle = retrieveAvailable(em, Constants.FILE_PREFIX_BGM_BATTLE);
				return new AvailableBgAndBgm(bgMenu, bgmMenu, bgBattle, bgmBattle);
			});
			if (result == null)
				throw new IOException("failed to load resources");
			return this.availableBgAndBgm = result;
		}
	}

	private Map<String, Set<String>> retrieveAvailable(final EntityManager em, final String prefix) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<String> cq = cb.createQuery(String.class);
		final Path<String> path = cq.from(Resource.class).get(Resource_.name);
		final List<String> list = em.createQuery(cq.where(cb.like(path, prefix + "%")).select(path)).getResultList();
		final Map<String, Set<String>> map = new HashMap<>();
		for (final String name : list) {
			final String key = FilenameUtils.removeExtension(name);
			map.computeIfAbsent(key, k -> new HashSet<>()).add(name);
		}
		return map;
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
		final Map<String, ZipEntry> filesBgMenu = new HashMap<>();
		final Map<String, ZipEntry> filesBgBattle = new HashMap<>();
		final Map<String, ZipEntry> filesBgmMenu = new HashMap<>();
		final Map<String, ZipEntry> filesBgmBattle = new HashMap<>();
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
			switch (parent.toLowerCase(Locale.ROOT)) {
			case Constants.IMPORT_DIR_CHARACTER_CRY:
				logger.debug("found cry sound file " + entry.getName());
				filesCry.put(Constants.FILE_PREFIX_CHARACTER_CRY + FilenameUtils.getName(name), entry);
				break;
			case Constants.IMPORT_DIR_CHARACTER_IMG:
				logger.debug("found image file " + entry.getName());
				filesImg.put(Constants.FILE_PREFIX_CHARACTER_IMG + FilenameUtils.getName(name), entry);
				break;
			case Constants.IMPORT_DIR_CHARACTER_ICON:
				logger.debug("found image file " + entry.getName());
				filesIcon.put(Constants.FILE_PREFIX_CHARACTER_ICON + FilenameUtils.getName(name), entry);
				break;
			case Constants.IMPORT_DIR_BG_MENU:
				logger.debug("found bg menu file " + entry.getName());
				filesBgMenu.put(FilenameUtils.getName(name), entry);
				break;
			case Constants.IMPORT_DIR_BGM_MENU:
				logger.debug("found bgm menu file " + entry.getName());
				filesBgmMenu.put(FilenameUtils.getName(name), entry);
				break;
			case Constants.IMPORT_DIR_BG_BATTLE:
				logger.debug("found bg battle file " + entry.getName());
				filesBgBattle.put(FilenameUtils.getName(name), entry);
				break;
			case Constants.IMPORT_DIR_BGM_BATTLE:
				logger.debug("found bgm battle file " + entry.getName());
				filesBgmBattle.put(FilenameUtils.getName(name), entry);
				break;
			default:
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
			assertCharFile(filesImg, requiredFiles, character.getImgBack(), Constants.FILE_PREFIX_CHARACTER_IMG,
					character);
			assertCharFile(filesImg, requiredFiles, character.getImgFront(), Constants.FILE_PREFIX_CHARACTER_IMG,
					character);
			assertCharFile(filesIcon, requiredFiles, character.getImgIcon(), Constants.FILE_PREFIX_CHARACTER_ICON,
					character);
			assertCharFile(filesCry, requiredFiles, character.getCry(), Constants.FILE_PREFIX_CHARACTER_CRY, character);
		}
		addFiles(requiredFiles, filesImg, Constants.FILE_PREFIX_CHARACTER_IMG);
		addFiles(requiredFiles, filesIcon, Constants.FILE_PREFIX_CHARACTER_ICON);
		addFiles(requiredFiles, filesCry, Constants.FILE_PREFIX_CHARACTER_CRY);
		addFiles(requiredFiles, filesBgBattle, Constants.FILE_PREFIX_BG_BATTLE);
		addFiles(requiredFiles, filesBgmBattle, Constants.FILE_PREFIX_BGM_BATTLE);
		addFiles(requiredFiles, filesBgMenu, Constants.FILE_PREFIX_BG_MENU);
		addFiles(requiredFiles, filesBgmMenu, Constants.FILE_PREFIX_BGM_MENU);
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

	private void assertCharFile(final Map<String, ZipEntry> availableFiles,
			final List<Pair<ZipEntry, String>> requiredFiles, final String name, final String prefix,
			final Character character) throws IOException {
		if (!availableFiles.containsKey(name))
			throw new IOException(
					String.format("Character %s specifies the resource %s, but this file was not found in the archive.",
							character.getName(), name));
		final ZipEntry entry = availableFiles.get(name);
		if (entry != null)
			requiredFiles.add(new ImmutablePair<>(entry, prefix));
	}

	private void addFiles(final List<Pair<ZipEntry, String>> requiredFiles, final Map<String, ZipEntry> availableFiles,
			final String prefix) {
		for (final ZipEntry entry : availableFiles.values())
			requiredFiles.add(new ImmutablePair<>(entry, prefix));
	}

	private Set<CharacterCsvModel> readCharacterCsv(final InputStream inputStream) throws IOException {
		return readTCsv(inputStream, CharacterCsvModel.class);
	}

	private Set<CharacterToSkillCsvModel> readCharacterToSkillCsv(final InputStream inputStream) throws IOException {
		return readTCsv(inputStream, CharacterToSkillCsvModel.class);
	}

	private Set<SkillCsvModel> readSkillCsv(final InputStream inputStream) throws IOException {
		try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
			return CsvParser
					.quote('\"')
					.separator(',')
					.mapTo(SkillCsvModel.class)
					.columnDefinition("condition", CsvColumnDefinition.customReaderDefinition(
							new CsmEnumCellValueReader<>(EStatusCondition.class)))
					.stream(reader)
					.collect(Collectors.toSet());
		}
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
		resource.setName(prefix + FilenameUtils.getName(entry.getName()));
		resource.setData(IOUtils.toByteArray(zipFile.getInputStream(entry)));
		resource.setFilename(FilenameUtils.getName(String.valueOf(entry.getName())));
		if (logger.isDebugEnabled())
			logger.debug("created resource " + resource);
		return resource;
	}

	private void writeImportToDb(final List<Pair<ZipEntry, String>> requiredFiles, final Set<Character> characters,
			final Set<Skill> skills, final ZipFile zipFile) throws IOException {
		databaseManager.flushAndEmpty();
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