package io.github.nolifedev.nlp.client.scene;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

@Singleton
public class SceneContainer extends JPanel {
	private static final long serialVersionUID = 5850736722102628969L;

	private final Canvas canvas;

	private static final Scene NO_SCENE = new Scene() {
		@Override
		public void render(Graphics2D g) {
			g.setColor(Color.black);
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
		}

		@Override
		public void tick(float timeSeconds) {
		}
	};

	private Scene loadedScene = NO_SCENE;

	private final EventBus gameBus;
	private final EventBus outBus;

	@Inject
	public SceneContainer(@Named("init") Class<? extends Scene> initialScene,
			@Named("gamebus") EventBus gameBus, @Named("out") EventBus outBus) {
		this.gameBus = gameBus;
		this.outBus = outBus;
		setLayout(new BorderLayout());

		canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);

		loadScene(initialScene);
	}

	public EventBus getGameBus() {
		return gameBus;
	}

	public EventBus getOutBus() {
		return outBus;
	}

	public void loadScene(Class<? extends Scene> initialScene) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(EventBus.class).annotatedWith(Names.named("gamebus"))
						.toInstance(gameBus);
				bind(EventBus.class).annotatedWith(Names.named("out"))
						.toInstance(outBus);

				bind(SceneContainer.class).toInstance(SceneContainer.this);
			}
		});

		loadedScene.onUnload();
		Scene newScene = injector.getInstance(initialScene);
		newScene.onLoad();
		loadedScene = newScene;
	}

	public void renderScene() {
		BufferStrategy strategy = canvas.getBufferStrategy();
		if (strategy == null) {
			canvas.createBufferStrategy(2);
			strategy = canvas.getBufferStrategy();
			if (strategy == null) {
				Thread.yield();
				return;
			}
		}

		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		loadedScene.render(g);
		strategy.show();
	}

	public void tickScene(float timeSeconds) {
		loadedScene.tick(timeSeconds);
	}

}
