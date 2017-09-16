package com.github.blutorange.translune.serial;

import java.util.List;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;

public class LootableStuff {
	ItemViewLoot[] items;
	CharacterStateViewLoot[] characterStates;

	@Deprecated
	public LootableStuff() {
		items = new ItemViewLoot[0];
		characterStates = new CharacterStateViewLoot[0];
	}

	public LootableStuff(final List<CharacterStateViewLoot> characterStates, final List<ItemViewLoot> items) {
		this.characterStates = characterStates.toArray(new CharacterStateViewLoot[characterStates.size()]);
		this.items = items.toArray(new ItemViewLoot[items.size()]);
	}

	/**
	 * @return the items
	 */
	@JsonProperty(required=true)
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public ItemViewLoot[] getItems() {
		return items;
	}

	/**
	 * @return the characters
	 */
	@JsonProperty(required=true)
	@JsonIgnore(ignoreDecoding = true, ignoreEncoding = false)
	public CharacterStateViewLoot[] getCharacterStates() {
		return characterStates;
	}
}