package io.github.nolifedev.nlp.client.scene;

import io.github.nolifedev.nlp.client.Game;
import io.github.nolifedev.nlp.client.ServerGameList;
import io.github.nolifedev.nlp.client.ServerPlayerList;
import io.github.nolifedev.nlp.client.event.HaveMyPlayer;
import io.github.nolifedev.nlp.common.event.net.op.Op0009LeftJoinedGame;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
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

	private final AbstractModule module;

	private final ServerGameList serverGameList;

	@Inject
	public SceneContainer(@Named("init") Class<? extends Scene> initialScene,
			@Named("gamebus") final EventBus gameBus,
			@Named("out") final EventBus outBus,
			final ServerPlayerList serverPlayerList,
			final ServerGameList serverGameList) {
		this.serverGameList = serverGameList;

		gameBus.register(this);

		setLayout(new BorderLayout());

		canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);

		module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(EventBus.class).annotatedWith(Names.named("gamebus"))
						.toInstance(gameBus);
				bind(EventBus.class).annotatedWith(Names.named("out"))
						.toInstance(outBus);

				bind(SceneContainer.class).toInstance(SceneContainer.this);
				bind(ServerPlayerList.class).toInstance(serverPlayerList);
				bind(ServerGameList.class).toInstance(serverGameList);
			}
		};

		loadScene(initialScene);
	}

	public void loadScene(Class<? extends Scene> initialScene) {
		loadScene(initialScene, null);
	}

	public void loadScene(Class<? extends Scene> initialScene,
			Module sceneModule) {
		Injector injector;
		if (sceneModule != null) {
			injector = Guice.createInjector(module, sceneModule);
		} else {
			injector = Guice.createInjector(module);
		}

		if (loadedScene != null) {
			loadedScene.onUnload();
		}
		Scene newScene = injector.getInstance(initialScene);
		newScene.onLoad();
		loadedScene = newScene;
	}

	@Subscribe
	public void onHaveMyPlayer(HaveMyPlayer e) {
		loadScene(SceneLobby.class);
	}

	@Subscribe
	public void onLeftJoinedGame(Op0009LeftJoinedGame e) {
		int gameID = e.getGameID();
		if (gameID > 0) {
			Game game = serverGameList.getMapGames().get(gameID);
			if (game == null) {
				System.err
						.println("Game I've joined is not known by the server game list!");
				game = new Game(gameID, "??? Unknown Game ???");
			}
			final Game finalGame = game;// Silly java...
			loadScene(SceneGame.class, new AbstractModule() {
				@Override
				protected void configure() {
					bind(Game.class).toInstance(finalGame);
				}
			});
		} else {
			loadScene(SceneLobby.class);
		}
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
