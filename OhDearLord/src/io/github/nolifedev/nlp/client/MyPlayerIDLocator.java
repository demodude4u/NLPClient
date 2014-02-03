package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.common.event.net.op.Op0004PlayerID;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class MyPlayerIDLocator {

	private int playerID = -1;

	@Inject
	public MyPlayerIDLocator(@Named("gamebus") EventBus gameBus) {
		gameBus.register(this);
	}

	public int getPlayerID() {
		return playerID;
	}

	@Subscribe
	public void onPlayerID(Op0004PlayerID e) {
		playerID = e.getPlayerID();
	}

}
