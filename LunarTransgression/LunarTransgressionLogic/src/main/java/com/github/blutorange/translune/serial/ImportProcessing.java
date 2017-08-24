package com.github.blutorange.translune.serial;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.sfm.csv.CsvParser;

import com.github.blutorange.translune.util.Constants;

@Singleton
public final class ImportProcessing {
	@Inject
	public ImportProcessing() {

	}

	public void importDataSet(final ZipFile zipFile) throws IOException {
		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		final Set<CharacterCsvModel> characterModel = new HashSet<>();
		final Set<SkillCsvModel> skillModel = new HashSet<>();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			if (entry == null)
				continue;
			final File file = new File(entry.getName());
			if (file.getParent() == Constants.IMPORT_DIR_CHARACTER_CRY) {

			}
			else if (file.getParent() == Constants.IMPORT_DIR_CHARACTER_IMG) {

			}
			else {
				switch (file.getName()) {
				case Constants.IMPORT_FILE_CHARACTER:
					characterModel.addAll(readCharacterCsv(zipFile.getInputStream(entry)));
					break;
				case Constants.IMPORT_FILE_CHARACTER_SKILL:
					break;
				case Constants.IMPORT_FILE_SKILL:
					skillModel.addAll(readSkillCsv(zipFile.getInputStream(entry)));
					break;
				}
			}
		}
	}

	private Set<CharacterCsvModel> readCharacterCsv(final InputStream inputStream) throws IOException {
		try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
			return CsvParser.quote('\'').separator(';').mapTo(CharacterCsvModel.class).stream(reader).collect(Collectors.toSet());
		}
	}

	private Set<SkillCsvModel> readSkillCsv(final InputStream inputStream) throws IOException {
		try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
			return CsvParser.quote('\'').separator(';').mapTo(SkillCsvModel.class).stream(reader).collect(Collectors.toSet());
		}
	}
}
