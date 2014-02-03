package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.common.event.net.op.Op0009LeftJoinedGame;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class MyGameLocator {

	private Game game = null;
	private final ServerGameList serverGameList;

	@Inject
	public MyGameLocator(@Named("gamebus") EventBus gameBus,
			ServerGameList serverGameList) {
		this.serverGameList = serverGameList;
		gameBus.register(this);
	}

	public Game getGame() {
		return game;
	}

	public boolean isInGame() {
		return game != null;
	}

	@Subscribe
	public void onLeftJoinedGame(Op0009LeftJoinedGame e) {
		game = serverGameList.getMapGames().get(e.getGameID());
	}

}
