package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.client.event.HaveMyPlayer;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class MyPlayerLocator {

	private Player player = null;
	private final MyPlayerIDLocator myPlayerIDLocator;

	@Inject
	public MyPlayerLocator(@Named("gamebus") EventBus gameBus,
			MyPlayerIDLocator myPlayerIDLocator) {
		this.myPlayerIDLocator = myPlayerIDLocator;
		gameBus.register(this);
	}

	public Player getPlayer() {
		if (player == null) {
			player = new Player(myPlayerIDLocator.getPlayerID(), "???");
		}
		return player;
	}

	@Subscribe
	public void onHaveMyPlayer(HaveMyPlayer e) {
		player = e.getPlayer();
	}

}
