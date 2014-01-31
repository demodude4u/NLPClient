package io.github.nolifedev.nlp.common.net;

import io.github.nolifedev.nlp.common.ServiceHandler;
import io.github.nolifedev.nlp.common.event.net.op.OpNickname;
import io.github.nolifedev.nlp.common.event.net.op.OpPing;
import io.github.nolifedev.nlp.common.event.net.op.OpPong;
import io.github.nolifedev.nlp.common.event.net.op.OpSessionID;
import io.github.nolifedev.nlp.common.util.Unsigned;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class OpCodeSerialization extends AbstractExecutionThreadService {

	public interface Factory {
		public OpCodeSerialization create(SocketDelegate socket,
				GameConnection gameConnection);
	}

	private static String readString(DataInputStream dataIn) throws IOException {
		byte[] buf = new byte[dataIn.readInt()];
		dataIn.readFully(buf);
		return null;
	}

	private static void skipFully(DataInputStream dataIn, int count)
			throws IOException {
		while (count > 0) {
			count -= dataIn.skipBytes(count);
		}
	}

	private static void writeString(DataOutputStream dataOut, String name)
			throws IOException {
		dataOut.writeInt(name.length());
		dataOut.write(name.getBytes(StandardCharsets.UTF_8));
	}

	private final SocketDelegate socket;
	private final DataInputStream dataIn;
	private final DataOutputStream dataOut;

	private final EventBus busIn;

	private final EventBus busOut;

	@Inject
	public OpCodeSerialization(@Assisted SocketDelegate socket,
			@Assisted GameConnection gameConnection,
			ServiceHandler serviceHandler) {
		this.socket = socket;
		dataIn = new DataInputStream(socket.getInputStream());
		dataOut = new DataOutputStream(socket.getOutputStream());

		busIn = gameConnection.getIncomingBus();
		busOut = gameConnection.getOutgoingBus();

		serviceHandler.add(this);
		busOut.register(this);
	}

	private void handlePacket() throws IOException {
		long packetLength = Unsigned.uInt(dataIn.readInt());
		int opCode = Unsigned.uShort(dataIn.readShort());
		switch (opCode) {
		case OpCodes.Ping: {
			int id = dataIn.readInt();
			busIn.post(new OpPing(id));
			break;
		}
		case OpCodes.Pong: {
			int id = dataIn.readInt();
			busIn.post(new OpPong(id));
			break;
		}
		case OpCodes.Nickname: {
			String name = readString(dataIn);
			busIn.post(new OpNickname(name));
			break;
		}
		case OpCodes.SessionID: {
			int sessionID = dataIn.readInt();
			busIn.post(new OpSessionID(sessionID));
			break;
		}
		// TODO other opcodes, fix opcodes above
		default:
			skipFully(dataIn, (int) (packetLength - 2));
			break;
		}
		dataOut.flush();
	}

	@Override
	protected void run() throws Exception {
		while (true) {
			while (!socket.isConnected()) {
				try {
					socket.connect();
				} catch (Exception e) {
					System.err.println(e.getMessage());
					Thread.yield();
				}
			}
			try {
				while (true) {
					handlePacket();
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				Thread.yield();
			}
		}
	}

	@Subscribe
	public void sendOpNickname(OpNickname e) throws IOException {
		dataOut.writeInt(2 + 4 + e.getName().length());
		dataOut.writeShort(e.getOpCode());
		writeString(dataOut, e.getName());
		dataOut.flush();
	}

	@Subscribe
	public void sendOpPing(OpPing e) throws IOException {
		dataOut.writeInt(2 + 4);
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getId());
		dataOut.flush();
	}

	@Subscribe
	public void sendOpPong(OpPong e) throws IOException {
		dataOut.writeInt(2 + 4);
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getId());
		dataOut.flush();
	}

	@Subscribe
	public void sendOpSessionID(OpSessionID e) throws IOException {
		dataOut.writeInt(2 + 4);
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getSessionID());
		dataOut.flush();
	}
}