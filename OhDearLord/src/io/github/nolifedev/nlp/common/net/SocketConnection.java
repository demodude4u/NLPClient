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
public class SocketConnection {

	private volatile Socket socket = null;
	private final EventBus incomingBus;
	private final EventBus outgoingBus;
	private final String host;
	private final int port;

	@Inject
	public SocketConnection(@Named("gamebus") EventBus incomingBus,
			@Named("out") EventBus outgoingBus, @Named("host") String host,
			@Named("port") int port) {
		this.incomingBus = incomingBus;
		this.outgoingBus = outgoingBus;
		this.host = host;
		this.port = port;
	}

	public void connect() throws IOException {
		disconnect();
		socket = new Socket(InetAddress.getByName(host), port);
	}

	public void disconnect() throws IOException {
		if (socket != null) {
			socket.close();
		}
		socket = null;
	}

	public String getHost() {
		return host;
	}

	public EventBus getIncomingBus() {
		return incomingBus;
	}

	public InputStream getInputStream() {
		return new InputStream() {
			Socket socket = null;
			private InputStream inputStream;

			@Override
			public int read() throws IOException {
				while (true) {
					try {
						if (socket != SocketConnection.this.socket) {
							socket = SocketConnection.this.socket;
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
					} catch (InterruptedException e) {
						throw new IOException("Interrupted", e);
					}
				}
			}
		};
	}

	public EventBus getOutgoingBus() {
		return outgoingBus;
	}

	public OutputStream getOutputStream() {
		return new OutputStream() {
			Socket socket = null;
			private OutputStream outputStream;

			@Override
			public void write(int b) throws IOException {
				while (true) {
					try {
						if (socket != SocketConnection.this.socket
								|| SocketConnection.this.socket == null) {
							socket = SocketConnection.this.socket;
							if (socket == null) {
								Thread.sleep(100);
								continue;
							}
							outputStream = socket.getOutputStream();
						}
						outputStream.write(b);
						return;
					} catch (InterruptedException e) {
						throw new IOException("Interrupted", e);
					}
				}
			}
		};
	}

	public int getPort() {
		return port;
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

}
