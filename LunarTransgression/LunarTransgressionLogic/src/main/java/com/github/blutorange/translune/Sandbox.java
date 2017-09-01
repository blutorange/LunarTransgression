package com.github.blutorange.translune;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.ModifiableItem;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.logic.EExperienceGroup;
import com.github.blutorange.translune.media.IAtlasImage;
import com.github.blutorange.translune.media.IImageProcessing;
import com.github.blutorange.translune.message.MessageFetchDataResponse;
import com.github.blutorange.translune.serial.AvailableBgAndBgm;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.LunarMessage;

public class Sandbox {
	public static void main(final String[] args) throws Exception {
		// testing...
		final LunarServletContextListener scl = new LunarServletContextListener();
		scl.initialize();
		try {
			importing();
		}
		finally {
			scl.destroy();
		}
	}

	static void availableBgAndBgm() throws IOException {

		final AvailableBgAndBgm a = ComponentFactory.getLunarComponent().iImportProcessing().availableBgAndBgm();
		System.out.println("\n\nbg-menu");
		System.out.println(a.getBgMenu());
		System.out.println("\n\nbgm-menu");
		System.out.println(a.getBgmMenu());
		System.out.println("\n\nbg-battle");
		System.out.println(a.getBgBattle());
		System.out.println("\n\nbgm-battle");
		System.out.println(a.getBgmBattle());
		final MessageFetchDataResponse msg = new MessageFetchDataResponse(9);
		System.err.println(ComponentFactory.getLunarComponent().jsoniter().get().serialize(new String[0]));
	}

	static void image() throws IOException {
		final IImageProcessing imageProcessing = ComponentFactory.getLunarComponent().imageProcessing();
		final String[] res = new String[50];
		for (int i = 50; i-->0;)
			res[i] = String.format("ico/%d.png", i+1);
		final IAtlasImage img = imageProcessing.packResources("spritesheet.png?gzip&resources=1.png,2p.png", res);
		FileUtils.write(new File("/tmp/packed.json"), img.getAtlasJson());
		FileUtils.writeByteArrayToFile(new File("/tmp/packed.png"), img.getImageBytes());
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
		try (ZipFile zipFile = new ZipFile("/home/madgaksha/Resources/LunarTransgression/data/output.zip")) {
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
		final String json = ComponentFactory.getLunarComponent().jsoniter().get().serialize(new String[0]);
		System.err.println(json);
	}
}