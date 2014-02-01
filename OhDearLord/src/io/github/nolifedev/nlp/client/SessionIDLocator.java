package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.common.event.net.op.Op0004SessionID;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class SessionIDLocator {

	private final EventBus gameBus;

	private int sessionID = -1;

	@Inject
	public SessionIDLocator(@Named("gamebus") EventBus gameBus) {
		this.gameBus = gameBus;
		gameBus.register(this);
	}

	public int getSessionID() {
		return sessionID;
	}

	@Subscribe
	public void onSessionID(Op0004SessionID e) {
		sessionID = e.getSessionID();
	}

}
