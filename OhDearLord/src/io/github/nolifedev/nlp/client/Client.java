package io.github.nolifedev.nlp.client;

import io.github.nolifedev.nlp.common.net.SocketEventAdapter;

import com.google.inject.Inject;

public class Client {

	@Inject
	public Client(ClientUI ui, SocketEventAdapter socketEventAdapter) {
	}

}
