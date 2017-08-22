package com.github.blutorange.translune;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.ModifiableItem;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.ComponentFactory;

public class Sandbox {
	public static void main(final String[] args) {
		// testing...
		jsoniter();
	}

	static void ldm() {
		final ServletContextListener scl = new LunarServletContextListener();
		scl.contextInitialized(new ServletContextEvent(null));
		final ILunarDatabaseManager ldm = ComponentFactory.getDatabaseComponent().iLunarDatabaseManager();

//		ldm.createSchema();

//		final Player player = new Player("player2", "123", "im a me", new HashSet<>(), new HashSet<>());
//		final Item item = new Item("item2", player, EItemEffect.HEAL, 20);

		final Item item = ldm.find(Item.class, "item");
		final Player player = ldm.find(Player.class, "player");

		//		ldm.persist(player);
//		ldm.persist(item);

//		ldm.delete(item);
//		ldm.delete(player);

		if (item != null)
			ldm.modify(item, ModifiableItem.class, s -> s.setPower(43));

		System.out.println("OK");
		scl.contextDestroyed(new ServletContextEvent(null));
	}

	static void jsoniter() {
//		new JsoniterConfig().setup();
//		final LunarMessage msg = new LunarMessage(2, ELunarMessageType.AUTHORIZE, ELunarStatusCode.OK, "test");
//		final ILunarPayload m = new MessageBattleStepped(new BattleAction[] {
//				new BattleAction(new String[] { "Hello world" }, "baz", new String[] { "foo", "bar" }) });
//		msg.setPayload(JsonStream.serialize(m));
//		final String json = JsonStream.serialize(msg);
//		System.out.println(json);
//		final LunarMessage de = JsonIterator.deserialize(json, LunarMessage.class);
//		System.out.println(de);
//		System.out.println(de.getId());
//		System.out.println(de.getType());
//		System.out.println(de.getPayload());
//		System.exit(0);
	}
}