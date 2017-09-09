package com.github.blutorange.translune.media;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.ic.ComponentFactory;

/**
 * Entity image.
 *
 * All files enclosed are licensed under CC-BY-3.0
 * (http://creativecommons.org/licenses/by/3.0/) You are free to use the
 * graphics/source both commercially and non-commercially as long as the
 * following credits are included:
 *
 * 1. Credit Noble Master Games as follows (linking is optional): "Avatar
 * graphics created by Noble Master Games" and link to
 * http://www.noblemaster.com 2. Credit the artist "Liea" as follows (optional):
 * "Avatar graphics designed by Mei-Li Nieuwland" and link to
 * http://liea.deviantart.com
 *
 * I hope the graphics come in handy for someone. Oh and yes, there are over 15
 * Trillion avatar combinations if you layer them correctly :-D
 *
 * Thanks, Christoph Aschwanden Twitter: http://twitter.com/noblemaster Website:
 * http://www.noblemaster.com
 *
 * @author Christoph Aschwanden
 * @since May 20, 2008
 */
public enum AvatarGenerator {
	INSTANCE;

	/** The default avatar. */
	private final Avatar defaultAvatar = new Avatar();

	/** The image for disabled avatar. */
	private final Image disabledImage;

	/** The patterns. */
	private final Image[][] patterns;
	/** The heads. */
	private final Image[][] heads;
	/** The eyes. */
	private final Image[][] eyes;
	/** The mouths. */
	private final Image[][] mouths;
	/** The scars. */
	private final Image[][] scars;
	/** The beards. */
	private final Image[][] beards;
	/** The mustaches. */
	private final Image[][] mustaches;
	/** The sideburns. */
	private final Image[][] sideburns;
	/** The glasses. */
	private final Image[][] glasses;
	/** The eye patches. */
	private final Image[][] eyePatches;
	/** The necklaces. */
	private final Image[][] necklaces;
	/** The hairs. */
	private final Image[][] hairs;
	/** The hair accessories. */
	private final Image[][] hairAccessories;
	/** The boas. */
	private final Image[][] boas;

	/**
	 * Constructor.
	 */
	private AvatarGenerator() {
		// parameters
		final String path = "avatar/avatar_";

		final String png = "image/png";
		final IImageProcessing loader = ComponentFactory.getLunarComponent().imageProcessing();

		try {
			// load avatar images
			disabledImage = loader.openFromResource(png, path + "disabled.png");

			// load heads
			heads = new Image[2][defaultAvatar.getHeadCombinations()];
			for (int i = 0; i < heads[0].length; i++) {
				final int id = i + 1;
				heads[0][i] = loader.openFromResource(png, path + "head_m" + id + ".png");
				heads[1][i] = loader.openFromResource(png, path + "head_f" + id + ".png");
			}

			// load eyes
			eyes = new Image[2][defaultAvatar.getEyeCombinations()];
			for (int i = 0; i < eyes[0].length; i++) {
				final int id = i + 1;
				eyes[0][i] = loader.openFromResource(png, path + "eye_m" + id + ".png");
				eyes[1][i] = loader.openFromResource(png, path + "eye_f" + id + ".png");
			}

			// load mouths
			mouths = new Image[2][defaultAvatar.getMouthCombinations()];
			for (int i = 0; i < mouths[0].length; i++) {
				final int id = i + 1;
				mouths[0][i] = loader.openFromResource(png, path + "mouth_m" + id + ".png");
				mouths[1][i] = loader.openFromResource(png, path + "mouth_f" + id + ".png");
			}

			// load scars
			scars = new Image[2][defaultAvatar.getScarCombinations() - 1];
			for (int i = 0; i < scars[0].length; i++) {
				final int id = i + 1;
				scars[0][i] = loader.openFromResource(png, path + "scar_" + id + ".png");
				scars[1][i] = scars[0][i];
			}

			// load beards
			beards = new Image[2][defaultAvatar.getBeardCombinations() - 1];
			for (int i = 0; i < beards[0].length; i++) {
				final int id = i + 1;
				beards[0][i] = loader.openFromResource(png, path + "beard_" + id + ".png");
				beards[1][i] = beards[0][i];
			}

			// load mustaches
			mustaches = new Image[2][defaultAvatar.getMustacheCombinations() - 1];
			for (int i = 0; i < mustaches[0].length; i++) {
				final int id = i + 1;
				mustaches[0][i] = loader.openFromResource(png, path + "mustache_" + id + ".png");
				mustaches[1][i] = mustaches[0][i];
			}

			// load sideburns
			sideburns = new Image[2][defaultAvatar.getSideburnCombinations() - 1];
			for (int i = 0; i < sideburns[0].length; i++) {
				final int id = i + 1;
				sideburns[0][i] = loader.openFromResource(png, path + "sideburn_" + id + ".png");
				sideburns[1][i] = sideburns[0][i];
			}

			// load glasses
			glasses = new Image[2][defaultAvatar.getGlassesCombinations() - 1];
			for (int i = 0; i < glasses[0].length; i++) {
				final int id = i + 1;
				glasses[0][i] = loader.openFromResource(png, path + "glasses_" + id + ".png");
				glasses[1][i] = glasses[0][i];
			}

			// load eye patches
			eyePatches = new Image[2][defaultAvatar.getEyePatchCombinations() - 1];
			for (int i = 0; i < eyePatches[0].length; i++) {
				final int id = i + 1;
				eyePatches[0][i] = loader.openFromResource(png, path + "eyepatch_" + id + ".png");
				eyePatches[1][i] = eyePatches[0][i];
			}

			// load necklaces
			necklaces = new Image[2][defaultAvatar.getNecklaceCombinations() - 1];
			for (int i = 0; i < necklaces[0].length; i++) {
				final int id = i + 1;
				if (id < 7) {
					necklaces[0][i] = loader.openFromResource(png, path + "necklace_" + id + ".png");
					necklaces[1][i] = necklaces[0][i];
				}
				else {
					necklaces[0][i] = loader.openFromResource(png, path + "necklace_m" + id + ".png");
					necklaces[1][i] = loader.openFromResource(png, path + "necklace_f" + id + ".png");
				}
			}

			// load hairs
			hairs = new Image[2][defaultAvatar.getHairCombinations() - 1];
			for (int i = 0; i < hairs[0].length; i++) {
				final int id = i + 1;
				hairs[0][i] = loader.openFromResource(png, path + "hair_m" + id + ".png");
				hairs[1][i] = loader.openFromResource(png, path + "hair_f" + id + ".png");
			}

			// load hair accessories
			hairAccessories = new Image[2][defaultAvatar.getHairAccessoryCombinations() - 1];
			for (int i = 0; i < hairAccessories[0].length; i++) {
				final int id = i + 1;
				if (id < 21) {
					hairAccessories[0][i] = loader.openFromResource(png, path + "accessory_" + id + ".png");
					hairAccessories[1][i] = hairAccessories[0][i];
				}
				else {
					hairAccessories[0][i] = loader.openFromResource(png, path + "accessory_m" + id + ".png");
					hairAccessories[1][i] = loader.openFromResource(png, path + "accessory_f" + id + ".png");
				}
			}

			// load feather boas
			boas = new Image[2][defaultAvatar.getFeatherBoaCombinations() - 1];
			for (int i = 0; i < boas[0].length; i++) {
				final int id = i + 1;
				boas[0][i] = loader.openFromResource(png, path + "boa_" + id + ".png");
				boas[1][i] = boas[0][i];
			}

			// load patterns
			patterns = new Image[2][defaultAvatar.getPatternCombinations() - 1];
			for (int i = 0; i < patterns[0].length; i++) {
				final int id = i + 1;
				patterns[0][i] = loader.openFromResource(png, path + "pattern_" + id + ".png");
				patterns[1][i] = patterns[0][i];
			}
		}
		catch (final IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * Returns the preferred size.
	 *
	 * @return The preferred size.
	 */
	public Dimension getPreferredSize() {
		return new Dimension(disabledImage.getWidth(null), disabledImage.getHeight(null));
	}

	/**
	 * Returns the image for the given avatar.
	 *
	 * @param avatar
	 *            The avatar.
	 * @return The image.
	 */
	public BufferedImage get(final Avatar avatar) {
		// create new image
		final Dimension size = getPreferredSize();
		final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		final Graphics g = image.getGraphics();

		// draw avatar onto image
		if (avatar.isEnabled()) {
			final Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// paramters
			final int width = disabledImage.getWidth(null);
			final int height = disabledImage.getHeight(null);
			final boolean male = avatar.isMale();
			final int genderIndex = male ? 0 : 1;

			// draw background
			int background = avatar.getBackground();
			if (background > 0) {
				background--;
				g.setColor(Avatar.COLORS[background]);
				g.fillRect(0, 0, width, height);
			}

			// draw pattern with foreground color
			int pattern = avatar.getPattern();
			if (pattern > 0) {
				pattern--;
				final int foreground = avatar.getForeground();

				// draw with selected color
				final Composite oldComposite = g2.getComposite();
				g2.setComposite(new ColorComposite(1.0f, Avatar.COLORS[foreground]));
				g2.drawImage(patterns[genderIndex][pattern], 0, 0, null);
				g2.setComposite(oldComposite);
			}

			// draw head
			final int head = avatar.getHead();
			g.drawImage(heads[genderIndex][head], 0, 0, null);

			// draw eye
			final int eye = avatar.getEye();
			g.drawImage(eyes[genderIndex][eye], 0, 0, null);

			// draw mouth
			final int mouth = avatar.getMouth();
			g.drawImage(mouths[genderIndex][mouth], 0, 0, null);

			// draw scar
			int scar = avatar.getScar();
			if (scar > 0) {
				scar--;
				g.drawImage(scars[genderIndex][scar], 0, 0, null);
			}

			// facial hair for males
			if (male) {
				// draw beard
				int beard = avatar.getBeard();
				if (beard > 0) {
					beard--;
					g.drawImage(beards[genderIndex][beard], 0, 0, null);
				}

				// draw mustache
				int mustache = avatar.getMustache();
				if (mustache > 0) {
					mustache--;
					g.drawImage(mustaches[genderIndex][mustache], 0, 0, null);
				}

				// draw sideburn
				int sideburn = avatar.getSideburn();
				if (sideburn > 0) {
					sideburn--;
					g.drawImage(sideburns[genderIndex][sideburn], 0, 0, null);
				}
			}

			// draw glasses
			int glass = avatar.getGlasses();
			if (glass > 0) {
				glass--;
				g.drawImage(glasses[genderIndex][glass], 0, 0, null);
			}

			// draw eye patch
			int eyePatch = avatar.getEyePatch();
			if (eyePatch > 0) {
				eyePatch--;
				g.drawImage(eyePatches[genderIndex][eyePatch], 0, 0, null);
			}

			// draw necklace
			int necklace = avatar.getNecklace();
			if (necklace > 0) {
				necklace--;
				g.drawImage(necklaces[genderIndex][necklace], 0, 0, null);
			}

			// draw hair
			int hair = avatar.getHair();
			if (hair > 0) {
				hair--;
				g.drawImage(hairs[genderIndex][hair], 0, 0, null);
			}

			// draw hair accessory
			int hairAccessory = avatar.getHairAccessory();
			if (hairAccessory > 0) {
				hairAccessory--;
				g.drawImage(hairAccessories[genderIndex][hairAccessory], 0, 0, null);
			}

			// draw feather boa
			int boa = avatar.getFeatherBoa();
			if (boa > 0) {
				boa--;
				g.drawImage(boas[genderIndex][boa], 0, 0, null);
			}
		}
		else {
			// draw disabled image
			g.drawImage(disabledImage, 0, 0, null);
		}

		// and return the completed image
		return image;
	}

	/**
	 * Draws an avatar.
	 *
	 * @param g
	 *            Where to draw to.
	 * @param avatar
	 *            The avatar to draw.
	 */
	public void draw(final Graphics g, final Avatar avatar) {
		g.drawImage(get(avatar), 0, 0, null);
	}

	/**
	 * Returns the patterns.
	 *
	 * @return The images.
	 */
	public Image[][] getPatterns() {
		return patterns;
	}

	/**
	 * Returns the heads.
	 *
	 * @return The images.
	 */
	public Image[][] getHeads() {
		return heads;
	}

	/**
	 * Returns the eyes.
	 *
	 * @return The images.
	 */
	public Image[][] getEyes() {
		return eyes;
	}

	/**
	 * Returns the mouths.
	 *
	 * @return The images.
	 */
	public Image[][] getMouths() {
		return mouths;
	}

	/**
	 * Returns the scars.
	 *
	 * @return The images.
	 */
	public Image[][] getScars() {
		return scars;
	}

	/**
	 * Returns the beards.
	 *
	 * @return The images.
	 */
	public Image[][] getBeards() {
		return beards;
	}

	/**
	 * Returns the mustaches.
	 *
	 * @return The images.
	 */
	public Image[][] getMustaches() {
		return mustaches;
	}

	/**
	 * Returns the sideburns.
	 *
	 * @return The images.
	 */
	public Image[][] getSideburns() {
		return sideburns;
	}

	/**
	 * Returns the glasses.
	 *
	 * @return The images.
	 */
	public Image[][] getGlasses() {
		return glasses;
	}

	/**
	 * Returns the eye patches.
	 *
	 * @return The images.
	 */
	public Image[][] getEyePatches() {
		return eyePatches;
	}

	/**
	 * Returns the necklaces.
	 *
	 * @return The images.
	 */
	public Image[][] getNecklaces() {
		return necklaces;
	}

	/**
	 * Returns the hairs.
	 *
	 * @return The images.
	 */
	public Image[][] getHairs() {
		return hairs;
	}

	/**
	 * Returns the hair accessories.
	 *
	 * @return The images.
	 */
	public Image[][] getHairAccessories() {
		return hairAccessories;
	}

	/**
	 * Returns the boas.
	 *
	 * @return The images.
	 */
	public Image[][] getBoas() {
		return boas;
	}

	/**
	 * RGB Composite from
	 * http://www.koders.com/java/fid4FA09DE7FFB0427DC50EEE998278F4DB28F523A9.aspx.
	 *
	 * @author Christoph Aschwanden
	 * @since December 17, 2007
	 */
	private static abstract class RGBComposite implements Composite {

		/** The extra alpha value. */
		protected float extraAlpha;

		/**
		 * Constructor.
		 */
		@SuppressWarnings("unused")
		public RGBComposite() {
			this(1.0f);
		}

		/**
		 * Constructor.
		 *
		 * @param alpha
		 *            The alpha level.
		 */
		public RGBComposite(final float alpha) {
			if (alpha < 0.0f || alpha > 1.0f) {
				throw new IllegalArgumentException("RGBComposite: alpha must be between 0 and 1");
			}
			this.extraAlpha = alpha;
		}

		/**
		 * Returns alpha.
		 *
		 * @return Alpha level.
		 */
		@SuppressWarnings("unused")
		public float getAlpha() {
			return extraAlpha;
		}

		/**
		 * Returns the hash code.
		 *
		 * @return The hash code.
		 */
		@Override
		public int hashCode() {
			return Float.floatToIntBits(extraAlpha);
		}

		/**
		 * True if equal.
		 *
		 * @param object
		 *            The other object to compare this to.
		 * @return True for equal.
		 */
		@Override
		public boolean equals(@Nullable final Object object) {
			if (!(object instanceof RGBComposite)) {
				return false;
			}
			final RGBComposite c = (RGBComposite) object;

			if (extraAlpha != c.extraAlpha) {
				return false;
			}
			return true;
		}

		/**
		 * The context.
		 */
		public abstract static class RGBCompositeContext implements CompositeContext {

			/** The alpha level. */
			private final float alpha;
			/** The source model. */
			private final ColorModel srcColorModel;
			/** The destination model. */
			private final ColorModel dstColorModel;

			/**
			 * Constructor for context.
			 *
			 * @param alpha
			 *            The alpha level.
			 * @param srcColorModel
			 *            The source color model.
			 * @param dstColorModel
			 *            The destination color model.
			 */
			public RGBCompositeContext(final float alpha, final ColorModel srcColorModel,
					final ColorModel dstColorModel) {
				this.alpha = alpha;
				this.srcColorModel = srcColorModel;
				this.dstColorModel = dstColorModel;
			}

			/**
			 * Dispose function.
			 */
			@Override
			public void dispose() {
				// not used
			}

			/**
			 * The composing function.
			 *
			 * @param srcRGB
			 *            The source RGB.
			 * @param dstRGB
			 *            The pre-destination RGB.
			 * @param alpha
			 *            The alpha level.
			 * @return The compbined destination RGB.
			 */
			public abstract int composeRGB(int srcRGB, int dstRGB, float alpha);

			/**
			 * Composer.
			 *
			 * @param src
			 *            The source.
			 * @param dstIn
			 *            The destination in.
			 * @param dstOut
			 *            The destination out.
			 */
			@Override
			public void compose(final Raster src, final Raster dstIn, final WritableRaster dstOut) {
				final float alpha = this.alpha;

				final int x0 = dstOut.getMinX();
				final int x1 = x0 + dstOut.getWidth();
				final int y0 = dstOut.getMinY();
				final int y1 = y0 + dstOut.getHeight();

				for (int x = x0; x < x1; x++) {
					for (int y = y0; y < y1; y++) {
						final int srcRGB = srcColorModel.getRGB(src.getDataElements(x, y, null));
						final int dstInRGB = dstColorModel.getRGB(dstIn.getDataElements(x, y, null));
						final int dstOutRGB = composeRGB(srcRGB, dstInRGB, alpha);
						dstOut.setDataElements(x, y, dstColorModel.getDataElements(dstOutRGB, null));
					}
				}
			}
		}
	}

	/**
	 * Color Composite.
	 *
	 * @author Christoph Aschwanden
	 * @since June 9, 2008
	 */
	private static final class ColorComposite extends RGBComposite {

		/** The color. */
		private final Color color;

		/**
		 * Constructor.
		 *
		 * @param alpha
		 *            The alpha level.
		 * @param color
		 *            The color.
		 */
		public ColorComposite(final float alpha, final Color color) {
			super(alpha);
			this.color = color;
		}

		/**
		 * Creates the context.
		 *
		 * @param srcColorModel
		 *            The source color model.
		 * @param dstColorModel
		 *            The destination color model.
		 * @param hints
		 *            The rendering hints.
		 * @return The context.
		 */
		@Override
		public CompositeContext createContext(final ColorModel srcColorModel, final ColorModel dstColorModel,
				@Nullable final RenderingHints hints) {
			return new Context(extraAlpha, color, srcColorModel, dstColorModel);
		}

		/**
		 * The context.
		 */
		private static class Context extends RGBCompositeContext {

			/** The color. */
			private final int color;

			/**
			 * Constructor for context.
			 *
			 * @param alpha
			 *            The alpha level.
			 * @param color
			 *            The color.
			 * @param srcColorModel
			 *            The source color model.
			 * @param dstColorModel
			 *            The destination color model.
			 */
			public Context(final float alpha, final Color color, final ColorModel srcColorModel,
					final ColorModel dstColorModel) {
				super(alpha, srcColorModel, dstColorModel);

				// save color
				this.color = color.getRGB() & 0x00ffffff;
			}

			/**
			 * The composing function.
			 *
			 * @param srcRGB
			 *            The source RGB.
			 * @param dstRGB
			 *            The pre-destination RGB.
			 * @param alpha
			 *            The alpha level in the range [0, 1].
			 * @return The compbined destination RGB.
			 */
			@Override
			public int composeRGB(final int srcRGB, final int dstRGB, final float alpha) {
				final int t0 = (srcRGB >> 24) & 0xff;
				if (t0 > 0) {
					int r0 = (this.color >> 16) & 0xff;
					int g0 = (this.color >> 8) & 0xff;
					int b0 = (this.color) & 0xff;

					final int t1 = (dstRGB >> 24) & 0xff;
					if (t1 > 0) {
						r0 = r0 * t0 / 255;
						g0 = g0 * t0 / 255;
						b0 = b0 * t0 / 255;
						int r1 = (dstRGB >> 16) & 0xff;
						int g1 = (dstRGB >> 8) & 0xff;
						int b1 = (dstRGB) & 0xff;
						final int revertOpacity = 255 - t0;
						r1 = r1 * revertOpacity / 255;
						g1 = g1 * revertOpacity / 255;
						b1 = b1 * revertOpacity / 255;
						return 0xff000000 | ((r0 + r1) << 16) | ((g0 + g1) << 8) | (b0 + b1);
					}
					return (t0 << 24) | (r0 << 16) | (g0 << 8) | (b0);
				}
				// was fully transparent...
				return dstRGB;
			}
		}
	}
}