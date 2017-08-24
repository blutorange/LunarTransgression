package com.github.blutorange.translune.db;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "resource")
public class Resource {

	@Lob
	@Basic(optional = false, fetch = FetchType.EAGER)
	@Column(name = "data", updatable = false, insertable = true, nullable = false, unique = false)
	private byte[] data;

	@NotNull
	@Column(name = "mime", updatable = false, insertable = true, nullable = false, unique = false)
	private String mime;

	@Id
	@Column(name = "name", updatable = false, insertable = true, nullable = false, unique = false)
	private String name;

	/**
	 * @return the blob
	 */
	public byte[] getData() {
		return data;
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

	/**
	 * @param blob
	 *            the blob to set
	 */
	public void setData(final byte[] data) {
		this.data = data;
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
	public void setName(final String name) {
		this.name = name;
	}
}