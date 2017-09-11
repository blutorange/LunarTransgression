package com.github.blutorange.translune.serial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.Skill;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;

public final class SkillEncoder implements Encoder {
	@Override
	public void encode(@Nullable final Object obj, @NonNull final JsonStream stream) throws IOException {
		if (obj == null)
			stream.writeEmptyObject();
		else {
			final Map<Integer, List<Skill>> remap = new HashMap<>();
			((Map<Skill, Integer>)obj).forEach((skill,level) -> remap.computeIfAbsent(level, x -> new ArrayList<>()).add(skill));
			stream.writeObjectStart();
			for (final Iterator<Entry<Integer,List<Skill>>> it = remap.entrySet().iterator(); it.hasNext();) {
				final Entry<Integer,List<Skill>> entry = it.next();
				stream.writeObjectField(entry.getKey().toString());
				stream.writeArrayStart();
				for (final Iterator<Skill> listIterator = entry.getValue().iterator(); listIterator.hasNext();) {
					stream.writeVal(listIterator.next());
					if (listIterator.hasNext())
						stream.writeMore();
				}
				stream.writeArrayEnd();
				if (it.hasNext())
					stream.writeMore();
			}
			stream.writeObjectEnd();
		}
	}
}