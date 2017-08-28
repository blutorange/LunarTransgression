package com.github.blutorange.translune;

import java.io.IOException;
import java.util.zip.ZipFile;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.ModifiableItem;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.logic.EExperienceGroup;
import com.github.blutorange.translune.message.MessageFetchDataResponse;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.LunarMessage;

public class Sandbox {
	public static void main(final String[] args) throws Exception {
		for (ELunarMessageType type : ELunarMessageType.values())
			System.out.println(String.format("\\textsc{%s} & a \\\\", type.name().toLowerCase().replace('_', ' ')));
System.exit(0);	

		// testing...
		final LunarServletContextListener scl = new LunarServletContextListener();
		scl.initialize();
		try {
			jsoniter();
		}
		finally {
			scl.destroy();
		}
	}

	static void ic() {
		final String initId = ComponentFactory.getLunarComponent().initIdStore().store("abc");
		final boolean assertion = ComponentFactory.getLunarComponent().initIdStore().assertToken("abc", initId);
		System.out.println(initId);
		System.out.println(assertion);
	}

	static void mime() {
		System.out.println(ComponentFactory.getLunarComponent().mimetypesFileTypeMap().getContentType("a.png"));
	}

	static void importing() throws IOException {
		try (ZipFile zipFile = new ZipFile("/tmp/output.zip")) {
			ComponentFactory.getLunarComponent().iImportProcessing().importDataSet(zipFile);
		}
	}

	static void exp() {
		final EExperienceGroup g = EExperienceGroup.SLOW;
		System.err.println(g.getLevelForExperience(6141));
	}

	static void schema() {
		final ILunarDatabaseManager ldm = ComponentFactory.getLunarComponent().iLunarDatabaseManager();
		ldm.createSchema();
	}

	static void ldm() {
		final ILunarDatabaseManager ldm = ComponentFactory.getLunarComponent().iLunarDatabaseManager();
		System.out.println(ldm.find(Player.class, "blutorange"));
		if (true) return;

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
	}

	static void jsoniter() {
		final Player player = ComponentFactory.getLunarComponent().iLunarDatabaseManager().find(Player.class, "blutorange");
		if (player == null)
			throw new RuntimeException("player not found");
		final MessageFetchDataResponse msg = new MessageFetchDataResponse(0, player);
		final String payload = ComponentFactory.getLunarComponent().jsoniter().get().serialize(msg);
		final LunarMessage message = new LunarMessage(4, ELunarMessageType.FETCH_DATA_RESPONSE, ELunarStatusCode.OK, payload);
		final String json = ComponentFactory.getLunarComponent().jsoniter().get().serialize(message);
		System.err.println(json);
	}
}