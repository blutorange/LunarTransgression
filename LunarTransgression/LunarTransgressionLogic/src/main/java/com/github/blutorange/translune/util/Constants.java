package com.github.blutorange.translune.util;

public final class Constants {
	public static final String CUSTOM_KEY_SADMIN_PASS = "translune.sadmin.pass";
	public static final String CUSTOM_KEY_TIMEOUT_INITID_MILLIS = "translune.initid.timeout";
	public static final String CUSTOM_KEY_TIMEOUT_MESSAGEQUEUE_MILLIS = "translune.messages.timeout";
	public static final String CUSTOM_KEY_MAX_THREAD_COUNT = "translune.threads.maxcount";
	public static final String CUSTOM_KEY_BATTLE_PREPARATION_TIMEOUT = "translune.battle.preparation.timeout";
	public static final String CUSTOM_KEY_BATTLE_ROUND_TIMEOUT = "translune.battle.round.timeout";

	public static final String HEADER_ACCESS_CONTROL = "Access-Control-Allow-Origin";
	public static final Object HEADER_ACCESS_CONTROL_ALL = "*";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";

	public static final String SESSION_KEY_AUTHORIZED = "auth";
	public static final String SESSION_KEY_CLIENT_TIME = "ctime";
	public static final String SESSION_KEY_GAME_STATE = "state";
	public static final String SESSION_KEY_NICKNAME = "nick";
	public static final String SESSION_KEY_SERVER_TIME = "stime";
	public static final String SESSION_KEY_STARTED = "start";
	public static final String SESSION_KEY_CLIENT_MESSAGE_QUEUE = "msq";

	public static final String USERNAME_SADMIN = "sadmin";

	public static final int MIN_PRIORITY = -7;
	public static final int MIN_LEVEL = 1;
	public static final int MIN_ITEMS = 0;
	public static final int MIN_CHARACTERS = 4;
	public static final int MIN_STAGE = -6;

	public static final int MAX_PRIORITY = 5;
	public static final int MAX_SKILL_POWER = 999;
	public static final long MAX_ITEM_POWER = 999;
	public static final int MAX_STAGE = 6;
	public static final int MAX_RELATIVE_HP = 999999;
	public static final int MAX_RELATIVE_MP = 999999;
	public static final int MAX_ACCURACY = 200;
	public static final int MAX_EVASION = 200;
	public static final int MAX_IV = 31;
	public static final int MAX_ITEMS = 30;
	public static final int MAX_CHARACTERS = 20;
	public static final int MAX_LEVEL = 100;
	public static final int MAX_HP = 9999;
	public static final int MAX_MP = 9999;
	public static final int MAX_EXP = 99999;
	public static final int MAX_PHYSICAL_ATTACK = 9999;
	public static final int MAX_PHYSICAL_DEFENSE = 9999;
	public static final int MAX_MAGICAL_ATTACK = 9999;
	public static final int MAX_MAGICAL_DEFENSE = 9999;
	public static final int MAX_SPEED = 999;

	public static final int BATTLE_DEFEND_STAGE_INCREASE = 3;

	public static final int PRIORITY_BASIC_ATTACK = 0;
	public static final int PRIORITY_ITEM = 2;
	public static final int PRIORITY_DEFEND = MAX_PRIORITY;

	// TODO move this to the config file (CustomProperties)
	public static final int CONFIG_DATABASE_SAVE_MINUTES = 10;

	public static final String QUARTZ_KEY_LDM = "ldm";


}