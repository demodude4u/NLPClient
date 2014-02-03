package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.client.event.HaveMyPlayer;
import io.github.nolifedev.nlp.client.scene.SceneContainer;
import io.github.nolifedev.nlp.client.scene.SceneGame;
import io.github.nolifedev.nlp.client.scene.SceneLobby;
import io.github.nolifedev.nlp.common.event.net.op.Op0001Ping;
import io.github.nolifedev.nlp.common.event.net.op.Op0002Pong;
import io.github.nolifedev.nlp.common.event.net.op.Op0009LeftJoinedGame;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class ServerResponder {

	private final SceneContainer c;
	private final EventBus outBus;
	private final ServerGameList serverGameList;

	@Inject
	public ServerResponder(@Named("gamebus") final EventBus gameBus,
			@Named("out") final EventBus outBus, SceneContainer sceneContainer,
			ServerGameList serverGameList) {
		this.outBus = outBus;
		this.c = sceneContainer;
		this.serverGameList = serverGameList;

		gameBus.register(this);
	}

	@Subscribe
	public void onHaveMyPlayer(HaveMyPlayer e) {
		c.loadScene(SceneLobby.class);
	}

	@Subscribe
	public void onLeftJoinedGame(Op0009LeftJoinedGame e) {
		int gameID = e.getGameID();
		if (gameID != 0) {
			Game game = serverGameList.getMapGames().get(gameID);
			if (game == null) {
				System.err
						.println("Game I've joined is not known by the server game list!");
				game = new Game(gameID, "??? Unknown Game ???");
			}
			final Game finalGame = game;// Silly java...
			c.loadScene(SceneGame.class, new AbstractModule() {
				@Override
				protected void configure() {
					bind(Game.class).toInstance(finalGame);
				}
			});
		} else {
			c.loadScene(SceneLobby.class);
		}
	}

	@Subscribe
	public void onPing(Op0001Ping e) {
		outBus.post(new Op0002Pong(e.getID()));
	}
}
