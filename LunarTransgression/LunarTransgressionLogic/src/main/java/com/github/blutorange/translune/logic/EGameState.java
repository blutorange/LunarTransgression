package com.github.blutorange.translune.logic;

public enum EGameState {
	/**
	 * As long as the session is not authorized and no user
	 * was identified. When not in this state, a user
	 * is available.
	 */
	WAITING_FOR_AUTHORIZATION,
	/**
	 * Idle state. The use is in the menu and not doing
	 * anything in particular.
	 */
	IN_MENU,
	/**
	 * After the user sent an invitation to another
	 * user, asking for a battle.
	 */
	WAITING_FOR_INVITATION_RESPONSE,
	/**
	 * User is battling another user.
	 */
	IN_BATTLE,
	;
}
