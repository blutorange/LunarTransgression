package com.github.blutorange.translune.db;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

@Entity
@Table(name = "resource")
public class Resource extends AbstractUnstoredEntity {

	@Lob
	@Basic(optional = false, fetch = FetchType.EAGER)
	@Column(name = "data", updatable = false, insertable = true, nullable = false, unique = false)
	private byte[] data;

	@NotEmpty
	@Column(name = "filename", updatable = false, insertable = true, nullable = false, unique = false, length = 64)
	private String filename;

	@NotNull
	@Column(name = "mime", updatable = false, insertable = true, nullable = false, unique = false, length = 64)
	private String mime;

	@NonNull
	@Id
	@Column(name = "name", updatable = false, insertable = true, nullable = false, unique = false, length = 32)
	private String name = StringUtils.EMPTY;

	/**
	 * @return the blob
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the mime
	 */
	public String getMime() {
		return mime;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@NonNull
	@Override
	public Serializable getPrimaryKey() {
		return name;
	}

	/**
	 * @param blob
	 *            the blob to set
	 */
	public void setData(final byte[] data) {
		this.data = data;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	/**
	 * @param mime
	 *            the mime to set
	 */
	public void setMime(final String mime) {
		this.mime = mime;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(@NonNull final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("Resource(%s,%s,%dbytes,%s)", name, mime, Integer.valueOf(data.length), filename);
	}
}