package io.github.nolifedev.nlp.client.util;

import io.github.nolifedev.nlp.ThemeColors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ImageLoader {
	public class LoadingImage {
		private volatile BufferedImage loadedImage = null;

		public BufferedImage getImage() {
			if (!isLoaded()) {
				int frame = (int) ((System.currentTimeMillis() % 1000) / 1000f * loadingAnimationImages.length);
				return loadingAnimationImages[frame];
			}

			return loadedImage;
		}

		public boolean isLoaded() {
			return loadedImage != null;
		}
	}

	private static final Color[] loadingAnimationBallColors = {
			ThemeColors.AppleJackOrange, ThemeColors.RarityWhite,
			ThemeColors.TwilightPurple, ThemeColors.PinkiePiePink,
			ThemeColors.FluttershyYellow, ThemeColors.RainbowDashBlue };
	private static final BufferedImage[] loadingAnimationImages = createLoadingAnimationImages(
			100, 30);
	private static final BufferedImage loadingFailedImage = createLoadingFailedImage(100);

	private static final ExecutorService service = Executors
			.newCachedThreadPool();

	private static BufferedImage[] createLoadingAnimationImages(int size,
			int frames) {
		BufferedImage[] images = new BufferedImage[frames];

		for (int i = 0; i < frames; i++) {
			BufferedImage image = new BufferedImage(size, size,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();

			g.setColor(new Color(0, 0, 64, 128));
			g.setStroke(new BasicStroke(1));
			g.fillOval(0, 0, size, size);
			final int bevel = size / 10;
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(bevel));
			g.drawOval(bevel / 2, bevel / 2, size - bevel, size - bevel);

			final int balls = loadingAnimationBallColors.length;
			final int ballSize = 10;
			for (int j = 0; j < balls; j++) {
				final float angle = (float) Math.PI
						* 2f
						* ((float) j / (float) balls + (float) i
								/ (float) frames + 0.1f * (float) Math
								.sin((float) Math.PI * 2 * i / frames));
				final float dist = size / 4f + -(size / 8f)
						* (float) Math.cos((float) Math.PI * 2 * i / frames);

				int x = (int) (Math.cos(angle) * dist + size / 2f);
				int y = (int) (Math.sin(angle) * dist + size / 2f);

				Color color = loadingAnimationBallColors[j
						% loadingAnimationBallColors.length];
				g.setColor(color.darker());
				g.setStroke(new BasicStroke(1));
				g.fillOval(x - ballSize / 2, y - ballSize / 2, ballSize,
						ballSize);
				g.setStroke(new BasicStroke(3));
				g.setColor(color);
				g.drawOval(x - ballSize / 2, y - ballSize / 2, ballSize,
						ballSize);
			}

			g.dispose();
			images[i] = image;
		}

		return images;
	}

	private static BufferedImage createLoadingFailedImage(int size) {
		BufferedImage image = new BufferedImage(size, size,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();

		g.setColor(new Color(128, 0, 0, 128));
		g.setStroke(new BasicStroke(1));
		g.fillRect(0, 0, size, size);
		final int bevel = size / 10;
		g.setColor(Color.red);
		g.setStroke(new BasicStroke(bevel * 2));
		g.drawRect(0, 0, size, size);

		g.setStroke(new BasicStroke(bevel));
		g.setColor(Color.black);
		g.drawLine(size / 4, size / 4, 3 * size / 4, 3 * size / 4);
		g.drawLine(size / 4, 3 * size / 4, 3 * size / 4, size / 4);

		g.dispose();
		return image;
	}

	@Inject
	public ImageLoader() {

	}

	public LoadingImage load(final Callable<BufferedImage> callable) {
		final LoadingImage ret = new LoadingImage();
		service.submit(new Runnable() {
			Throwable cause = new Throwable("Source of Problem!");

			@Override
			public void run() {
				try {
					Thread.sleep(1000); // FIXME When I'm done admiring the
										// animation
					ret.loadedImage = callable.call();
				} catch (Exception e) {
					System.err.println("Failed to load image!");
					e.printStackTrace();
					cause.printStackTrace();
					ret.loadedImage = loadingFailedImage;
				}
			}
		});
		return ret;
	}

	public LoadingImage load(final File file) {
		return load(new Callable<BufferedImage>() {
			@Override
			public BufferedImage call() throws Exception {
				return ImageIO.read(file);
			};
		});
	}

	public LoadingImage load(final String urlString) {
		return load(new Callable<BufferedImage>() {
			@Override
			public BufferedImage call() throws Exception {
				URL url = new URL(urlString);
				return ImageIO.read(url);
			};
		});
	}

	public LoadingImage load(final String classLoaderPath,
			final Class<?> classFromClassLoader) {
		return load(new Callable<BufferedImage>() {
			@Override
			public BufferedImage call() throws Exception {
				InputStream resourceAsStream = classFromClassLoader
						.getResourceAsStream(classLoaderPath);
				if (resourceAsStream == null) {
					throw new IOException("Image resource [ " + classLoaderPath
							+ " ] does not exist!");
				}
				return ImageIO.read(resourceAsStream);
			};
		});
	}

	public LoadingImage load(final URL url) {
		return load(new Callable<BufferedImage>() {
			@Override
			public BufferedImage call() throws Exception {
				return ImageIO.read(url);
			};
		});
	}
}
