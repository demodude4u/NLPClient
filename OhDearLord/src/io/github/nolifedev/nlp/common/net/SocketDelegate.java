package io.github.nolifedev.nlp.common.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SocketDelegate {

	public void connect() throws IOException;

	public void disconnect() throws IOException;

	public InputStream getInputStream();

	public OutputStream getOutputStream();

	public boolean isConnected();

}