package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.client.ServerGameList;
import io.github.nolifedev.nlp.client.ServerPlayerList;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.Map;

import javax.swing.JPanel;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

@Singleton
public class SceneContainer extends JPanel {
	private static final long serialVersionUID = 5850736722102628969L;

	private final Canvas canvas;

	private Scene loadedScene;

	private final Injector injector;
	private final Module module;

	private final Map<Class<? extends Scene>, Injector> builtInjectors = Maps
			.newHashMap();

	@Inject
	public SceneContainer(@Named("init") Class<? extends Scene> initialScene,
			@Named("gamebus") final EventBus gameBus,
			@Named("out") final EventBus outBus,
			final ServerPlayerList serverPlayerList,
			final ServerGameList serverGameList, Injector injector) {

		this.injector = injector;

		module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(SceneContainer.class).annotatedWith(
						Names.named("instance"))
						.toInstance(SceneContainer.this);
			}
		};

		setLayout(new BorderLayout());

		canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);

		loadScene(initialScene);
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void loadScene(Class<? extends Scene> initialScene) {
		loadScene(initialScene, null);
	}

	public void loadScene(Class<? extends Scene> sceneClass, Module sceneModule) {
		Injector injector = builtInjectors.get(sceneClass);
		if (injector == null) {
			if (sceneModule != null) {
				injector = this.injector.createChildInjector(module,
						sceneModule);
			} else {
				injector = this.injector.createChildInjector(module);
			}
			builtInjectors.put(sceneClass, injector);
		}

		if (loadedScene != null) {
			loadedScene.onUnload();
		}
		Scene newScene = injector.getInstance(sceneClass);
		newScene.onLoad();
		loadedScene = newScene;
		System.out.println("##### Loaded Scene: "
				+ loadedScene.getClass().getSimpleName());
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
