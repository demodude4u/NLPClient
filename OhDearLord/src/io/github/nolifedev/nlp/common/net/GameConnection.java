package io.github.nolifedev.nlp.common.net;

import java.io.IOException;

import com.google.common.eventbus.EventBus;

public interface GameConnection {

	public EventBus getIncomingBus();

	public EventBus getOutgoingBus();

	public boolean isConnected();

	public void connect() throws IOException;

	public void disconnect() throws IOException;

}