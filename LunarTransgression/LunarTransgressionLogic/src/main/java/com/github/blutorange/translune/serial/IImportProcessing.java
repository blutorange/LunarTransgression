package com.github.blutorange.translune.serial;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public interface IImportProcessing {

	int importDataSet(ZipFile zipFile) throws IOException;

	void uploadFile(ZipFile zipFile, ZipEntry entry, String prefix) throws IOException;
}