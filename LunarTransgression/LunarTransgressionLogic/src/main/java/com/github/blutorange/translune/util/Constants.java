package com.github.blutorange.translune.util;

import org.apache.commons.math3.fraction.BigFraction;

public final class Constants {
	public static final int BATTLE_BASIC_ATTACK_POWER = 50;
	public static final int BATTLE_DEFEND_STAGE_INCREASE = 3;
	public static final int BATTLE_PRIORITY_BASIC_ATTACK = 0;
	public static final int BATTLE_PRIORITY_DEFEND = 5;
	public static final int BATTLE_BASIC_ATTACK_CONDITION_CHANCE = 10;
	public static final int BATTLE_BASIC_ATTACK_FLINCH_CHANCE = 10;
	public static final int BATTLE_PRIORITY_ITEM = 2;
	public static final int BATTLE_STAGE_CHANGE_TURNS = 4;
	public static final int BATTLE_BASIC_POWER_CONFUSION = 50;

	public static final String CUSTOM_KEY_BATTLE_PREPARATION_TIMEOUT = "translune.battle.preparation.timeout";
	public static final String CUSTOM_KEY_BATTLE_ROUND_TIMEOUT = "translune.battle.round.timeout";
	public static final String CUSTOM_KEY_BATTLE_FIRST_TIMEOUT = "translune.battle.first.timeout";
	public static final String CUSTOM_KEY_DATABASE_SAVE_MINUTES = "translune.db.save";
	public static final String CUSTOM_KEY_MAX_THREAD_COUNT = "translune.threads.maxcount";
	public static final String CUSTOM_KEY_SADMIN_PASS = "translune.sadmin.pass";
	public static final String CUSTOM_KEY_TIMEOUT_INITID_MILLIS = "translune.initid.timeout";
	public static final String CUSTOM_KEY_TIMEOUT_MESSAGEQUEUE_MILLIS = "translune.messages.timeout";

	public static final String FILE_PREFIX_AVATAR = "ava/";
	public static final String FILE_PREFIX_DEFAULT = "def/";
	public static final String FILE_PREFIX_CHARACTER_CRY = "cry/";
	public static final String FILE_PREFIX_CHARACTER_ICON = "ico/";
	public static final String FILE_PREFIX_CHARACTER_IMG = "img/";
	public static final String FILE_PREFIX_BG_MENU = "bgm/";
	public static final String FILE_PREFIX_BGM_MENU = "bmm/";
	public static final String FILE_PREFIX_BG_BATTLE = "bgb/";
	public static final String FILE_PREFIX_BGM_BATTLE = "bmb/";

	public static final BigFraction FRACTION_THREE_HALFS = BigFraction.getReducedFraction(3, 2);
	public static final BigFraction FRACTION_TWO = BigFraction.getReducedFraction(2, 1);

	public static final String HEADER_ACCESS_CONTROL = "Access-Control-Allow-Origin";
	public static final String HEADER_ACCESS_CONTROL_ALL = "*";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";

	public static final String IMPORT_DIR_CHARACTER_CRY = "character-cry";
	public static final String IMPORT_DIR_CHARACTER_ICON = "character-icon";
	public static final String IMPORT_DIR_CHARACTER_IMG = "character-img";
	public static final String IMPORT_DIR_BG_MENU= "bg-menu";
	public static final String IMPORT_DIR_BGM_MENU= "bgm-menu";
	public static final String IMPORT_DIR_BG_BATTLE= "bg-battle";
	public static final String IMPORT_DIR_BGM_BATTLE= "bgm-battle";
	public static final String IMPORT_DIR_DEFAULT = "default";

	public static final String IMPORT_FILE_CHARACTER = "character.csv";
	public static final String IMPORT_FILE_CHARACTER_SKILL = "character_skill.csv";
	public static final String IMPORT_FILE_SKILL = "skill.csv";

	public static final int MAX_ACCURACY = 200;
	public static final int MAX_CHARACTERS = 28;
	public static final int MAX_EVASION = 200;
	public static final int MAX_EXP = 99999;
	public static final int MAX_HP = 9999;
	public static final long MAX_ITEM_POWER = 999;
	public static final int MAX_ITEMS = 28;
	public static final int MAX_IV = 31;
	public static final int MAX_LEVEL = 100;
	public static final int MAX_MAGICAL_ATTACK = 9999;
	public static final int MAX_MAGICAL_DEFENSE = 9999;
	public static final int MAX_MP = 9999;
	public static final int MAX_PHYSICAL_ATTACK = 9999;
	public static final int MAX_PHYSICAL_DEFENSE = 9999;
	public static final int MAX_PRIORITY = 5;
	public static final int MAX_RELATIVE_HP = 999999;
	public static final int MAX_RELATIVE_MP = 999999;
	public static final int MAX_SKILL_POWER = 999;
	public static final int MAX_SPEED = 999;
	public static final int MAX_STAGE = 6;
	public static final int MAX_LENGTH_PLAYER_NICKNAME = 16;
	public static final int MAX_LENGTH_PLAYER_DESCRIPTION = 50;

	public static final int MIN_RELEASE_LEVEL = 80;
	public static final int MIN_CHARACTERS = 4;
	public static final int MIN_ITEMS = 0;
	public static final int MIN_LEVEL = 1;
	public static final int MIN_PRIORITY = -7;
	public static final int MIN_STAGE = -6;

	public static final String SESSION_KEY_AUTHORIZED = "auth";
	public static final String SESSION_KEY_CLIENT_MESSAGE_QUEUE = "msq";
	public static final String SESSION_KEY_CLIENT_TIME = "ctime";
	public static final String SESSION_KEY_GAME_STATE = "state";
	public static final String SESSION_KEY_NICKNAME = "nick";
	public static final String SESSION_KEY_SERVER_TIME = "stime";
	public static final String SESSION_KEY_STARTED = "start";

	public static final String SERVLET_KEY_GZIP = "gzip";
	public static final String SERVLET_KEY_RESOURCES = "resources";

	public static final String USERNAME_SADMIN = "sadmin";

	public static final String DEFAULT_PLAYER_AVATAR = FILE_PREFIX_DEFAULT + "pavatar.png";
}