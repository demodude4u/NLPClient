package io.github.nolifedev.nlp.common.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class SocketGameConnection implements GameConnection, SocketDelegate {

	private volatile Socket socket = null;
	private final EventBus incomingBus;
	private final EventBus outgoingBus;
	private final String host;
	private final int port;

	@Inject
	public SocketGameConnection(@Named("gamebus") EventBus incomingBus,
			@Named("out") EventBus outgoingBus, @Named("host") String host,
			@Named("port") int port,
			OpCodeSerialization.Factory opCodeSerializationFactory) {
		this.incomingBus = incomingBus;
		this.outgoingBus = outgoingBus;
		this.host = host;
		this.port = port;

		opCodeSerializationFactory.create(this, this);
	}

	@Override
	public void connect() throws IOException {
		disconnect();
		socket = new Socket(InetAddress.getByName(host), port);
	}

	@Override
	public void disconnect() throws IOException {
		if (socket != null) {
			socket.close();
		}
		socket = null;
	}

	@Override
	public EventBus getIncomingBus() {
		return incomingBus;
	}

	@Override
	public InputStream getInputStream() {
		return new InputStream() {
			Socket socket = null;
			private InputStream inputStream;

			@Override
			public int read() {
				while (true) {
					try {
						if (socket != SocketGameConnection.this.socket) {
							socket = SocketGameConnection.this.socket;
							if (socket == null) {
								Thread.sleep(100);
								continue;
							}
							inputStream = socket.getInputStream();
						}
						if (socket == null) {
							Thread.sleep(100);
							continue;
						}
						int ret = inputStream.read();
						if (ret == -1) {
							Thread.yield();
							continue;
						}
						return ret;
					} catch (Exception e) {
						e.printStackTrace();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		};
	}

	@Override
	public EventBus getOutgoingBus() {
		return outgoingBus;
	}

	@Override
	public OutputStream getOutputStream() {
		return new OutputStream() {
			Socket socket = null;
			private OutputStream outputStream;

			@Override
			public void write(int b) {
				while (true) {
					try {
						if (socket != SocketGameConnection.this.socket) {
							socket = SocketGameConnection.this.socket;
							if (socket == null) {
								Thread.sleep(100);
								continue;
							}
							outputStream = socket.getOutputStream();
						}
						outputStream.write(b);
						return;
					} catch (Exception e) {
						// e.printStackTrace();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		};
	}

	@Override
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

}
