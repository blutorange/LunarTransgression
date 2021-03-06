package com.github.blutorange.translune;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.ModifiableItem;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.logic.EExperienceGroup;
import com.github.blutorange.translune.logic.Pageable;
import com.github.blutorange.translune.media.IAtlasImage;
import com.github.blutorange.translune.media.IImageProcessing;
import com.github.blutorange.translune.message.MessageFetchData;
import com.github.blutorange.translune.message.MessageFetchDataResponse;
import com.github.blutorange.translune.message.MessageUpdateData;
import com.github.blutorange.translune.serial.AvailableBgAndBgm;

public class Sandbox {
	public static void main(final String[] args) throws Exception {
		// testing...
//		final LunarServletContextListener scl = new LunarServletContextListener();
//		scl.initialize();
		try {
			queue();
		}
		finally {
//			scl.destroy();
		}
	}

	private static void queue() {
		final Queue<Integer> q = new PriorityQueue<>(10, (m1, m2) -> Integer.compare(m1, m2));
		q.add(5);
		q.add(3);
		q.add(4);
		Integer a;
		while ((a = q.poll())!=null)
			System.out.println(a);
	}

	static void fetch() throws Exception {
		final String message = "{\"fetch\":\"OPPONENT\",\"details\":\"{\\\"offset\\\":0,\\\"count\\\":5,\\\"orderBy\\\":[{\\\"name\\\":\\\"nickname\\\",\\\"direction\\\":\\\"ASC\\\"}]}\"}";
		final MessageFetchData msg = ComponentFactory.getLunarComponent().jsoniter().get().deserialize(message, MessageFetchData.class);
		if (msg == null)
			throw new Exception();
		System.out.println(msg.getDetails());
		final Pageable pg = ComponentFactory.getLunarComponent().jsoniter().get().deserialize(msg.getDetails(), Pageable.class);
		if (pg == null)
			throw new Exception();
		System.out.println(pg.getCount());
//		final PageableResult r = (PageableResult)EFetchDataType.OPPONENT.fetch(null, "{\"count\":0, \"offset\": 0}");
//		System.out.println(r.getTotal());
//		System.out.println(r.getList().length);
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

	static void avatar() throws IOException {
		// startup
		final BufferedImage  img = ComponentFactory.getLunarComponent().imageProcessing().generateDisabledAvatar();
		ImageIO.write(img, "png", new File(String.format("/tmp/avatar.png")));
		final int n = 100;
		final BufferedImage[] array = new BufferedImage[n];
		final long t1 = System.currentTimeMillis();
		for (int i = n; i --> 0;)
			array[i] = ComponentFactory.getLunarComponent().imageProcessing().generateRandomAvatar();
		final long t2 = System.currentTimeMillis();
		System.out.println("Took " + (t2-t1)/1000.0f + "millis");
		for (int i = n; i --> 0;)
			ImageIO.write(array[i], "png", new File(String.format("/tmp/avatar_%05d.png", i)));
	}

	static void jsoniter() {
//		final Player player = ComponentFactory.getLunarComponent().iLunarDatabaseManager().find(Player.class, "blutorange");
//		if (player == null)
//			throw new RuntimeException("player not found");
//		final MessageFetchDataResponse msg = new MessageFetchDataResponse(0, player);
//		final String payload = ComponentFactory.getLunarComponent().jsoniter().get().serialize(msg);
//		final LunarMessage message = new LunarMessage(4, ELunarMessageType.FETCH_DATA_RESPONSE, ELunarStatusCode.OK, payload);
//		final String json = ComponentFactory.getLunarComponent().jsoniter().get().serialize(message);
		final Object o = ComponentFactory.getLunarComponent().jsoniter().get().deserialize("{\"update\":\"character-nickname\",\"details\":\"{\\\"id\\\":\\\"3861729c-67f0-4c9a-b838-b9b300bd4d3d\\\",\\\"nickname\\\":\\\"qwm\\\"}\"}", MessageUpdateData.class);
		System.out.println(o);
	}
}