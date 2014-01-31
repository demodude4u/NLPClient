package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.common.event.net.op.OpPing;
import io.github.nolifedev.nlp.common.event.net.op.OpPong;
import io.github.nolifedev.nlp.common.net.SocketEventAdapter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Client {

	private final EventBus outBus;

	@Inject
	public Client(ClientUI ui, SocketEventAdapter socketEventAdapter,
			@Named("gamebus") EventBus gameBus, @Named("out") EventBus outBus) {
		this.outBus = outBus;

		gameBus.register(this);
	}

	@Subscribe
	public void onPing(OpPing event) {
		outBus.post(new OpPong(event.getId()));
	}

}
