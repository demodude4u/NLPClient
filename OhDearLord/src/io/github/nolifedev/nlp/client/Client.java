package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.common.event.net.op.Op0001Ping;
import io.github.nolifedev.nlp.common.event.net.op.Op0002Pong;
import io.github.nolifedev.nlp.common.net.SocketEventAdapter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Client {

	private final EventBus outBus;

	@Inject
	public Client(ClientUI ui, SocketEventAdapter socketEventAdapter,
			@Named("gamebus") EventBus gameBus, @Named("out") EventBus outBus,
			PingPongMonitor pingPongMonitor) {
		this.outBus = outBus;

		gameBus.register(this);
	}

	@Subscribe
	public void onPing(Op0001Ping e) {
		outBus.post(new Op0002Pong(e.getID()));
	}

}
