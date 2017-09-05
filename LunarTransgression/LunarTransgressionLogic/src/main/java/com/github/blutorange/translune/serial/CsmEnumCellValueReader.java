package com.github.blutorange.translune.serial;

import org.eclipse.jdt.annotation.Nullable;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.cellreader.EnumCellValueReader;

public class CsmEnumCellValueReader<E extends Enum<E>> extends EnumCellValueReader<E> {
	public CsmEnumCellValueReader(final Class<E> enumClass) {
		super(enumClass);
	}

	@Nullable
	@Override
	public E read(final char[] chars, final int offset, final int length, final ParsingContext parsingContext) {
		if (length == 0)
			return null;
		return super.read(chars, offset, length, parsingContext);
	}
}
