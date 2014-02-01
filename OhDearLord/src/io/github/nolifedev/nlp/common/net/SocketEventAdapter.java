package io.github.nolifedev.nlp.common.net;

import io.github.nolifedev.nlp.common.ServiceHandler;
import io.github.nolifedev.nlp.common.event.net.NetConnect;
import io.github.nolifedev.nlp.common.event.net.NetDisconnect;
import io.github.nolifedev.nlp.common.event.net.op.Op0001Ping;
import io.github.nolifedev.nlp.common.event.net.op.Op0002Pong;
import io.github.nolifedev.nlp.common.event.net.op.Op0003Nickname;
import io.github.nolifedev.nlp.common.event.net.op.Op0004SessionID;
import io.github.nolifedev.nlp.common.event.net.op.Op0005DeletedGames;
import io.github.nolifedev.nlp.common.event.net.op.Op0006CreatedGames;
import io.github.nolifedev.nlp.common.event.net.op.Op0007CreateJoinGame;
import io.github.nolifedev.nlp.common.event.net.op.Op0008LeaveJoinGame;
import io.github.nolifedev.nlp.common.event.net.op.Op0009LeftJoinedGame;
import io.github.nolifedev.nlp.common.event.net.op.Op000BPlayersJoinedServer;
import io.github.nolifedev.nlp.common.event.net.op.Op000CPlayersJoinedGame;
import io.github.nolifedev.nlp.common.event.net.op.Op000DPlayersLeftGame;
import io.github.nolifedev.nlp.common.event.net.op.Op000FPlayersLeftServer;
import io.github.nolifedev.nlp.common.event.net.op.OpUnknown;
import io.github.nolifedev.nlp.common.util.Unsigned;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Inject;

public class SocketEventAdapter extends AbstractExecutionThreadService {

	private interface IOReader<T> {
		public T read(DataInput dataIn) throws IOException;
	}

	private interface IOWriter<T> {
		public void write(DataOutput dataOut, T value) throws IOException;
	}

	private static IOReader<Integer> intReader = new IOReader<Integer>() {
		@Override
		public Integer read(DataInput dataIn) throws IOException {
			return dataIn.readInt();
		}
	};

	private static IOWriter<Integer> intWriter = new IOWriter<Integer>() {
		@Override
		public void write(DataOutput dataOut, Integer value) throws IOException {
			dataOut.writeInt(value);
		}
	};

	private static IOReader<String> stringReader = new IOReader<String>() {
		@Override
		public String read(DataInput dataIn) throws IOException {
			return readString(dataIn);
		}
	};

	private static IOWriter<String> stringWriter = new IOWriter<String>() {
		@Override
		public void write(DataOutput dataOut, String value) throws IOException {
			writeString(dataOut, value);
		}
	};

	private static <K, V> Map<K, V> readMap(DataInput dataIn,
			IOReader<K> keyReader, IOReader<V> valueReader) throws IOException {
		int count = dataIn.readInt();
		Map<K, V> map = Maps.newLinkedHashMap();
		for (int i = 0; i < count; i++) {
			map.put(keyReader.read(dataIn), valueReader.read(dataIn));
		}
		return map;
	}

	private static <T> Set<T> readSet(DataInput dataIn, IOReader<T> reader)
			throws IOException {
		int count = dataIn.readInt();
		Set<T> set = Sets.newLinkedHashSet();
		for (int i = 0; i < count; i++) {
			set.add(reader.read(dataIn));
		}
		return set;
	}

	private static String readString(DataInput dataIn) throws IOException {
		byte[] buf = new byte[dataIn.readInt()];
		dataIn.readFully(buf);
		return new String(buf, StandardCharsets.UTF_8);
	}

	private static <K, V> void writeMap(DataOutput dataOut,
			IOWriter<K> keyWriter, IOWriter<V> valueWriter, Map<K, V> map)
			throws IOException {
		dataOut.writeInt(map.size());
		for (Entry<K, V> entry : map.entrySet()) {
			keyWriter.write(dataOut, entry.getKey());
			valueWriter.write(dataOut, entry.getValue());
		}
	}

	private static <T> void writeSet(DataOutput dataOut, IOWriter<T> writer,
			Set<T> set) throws IOException {
		dataOut.writeInt(set.size());
		for (T value : set) {
			writer.write(dataOut, value);
		}
	}

	private static void writeString(DataOutput dataOut, String string)
			throws IOException {
		dataOut.writeInt(string.length());
		dataOut.write(string.getBytes(StandardCharsets.UTF_8));
	}

	private final SocketConnection socketConnection;

	private final PacketInputStream dataIn;
	private final PacketOutputStream dataOut;

	private final EventBus busIn;
	private final EventBus busOut;

	@Inject
	public SocketEventAdapter(SocketConnection socketConnection,
			ServiceHandler serviceHandler) {
		this.socketConnection = socketConnection;
		dataIn = new PacketInputStream(socketConnection.getInputStream());
		dataOut = new PacketOutputStream(socketConnection.getOutputStream());

		busIn = socketConnection.getIncomingBus();
		busOut = socketConnection.getOutgoingBus();

		serviceHandler.add(this);
		busOut.register(this);
	}

	private void handlePacket() throws IOException {
		int packetLength = dataIn.next();
		int opCode = Unsigned.uShort(dataIn.readShort());
		try {
			switch (opCode) {
			case OpCodes.Ping: {
				int id = dataIn.readInt();
				busIn.post(new Op0001Ping(id));
				break;
			}
			case OpCodes.Pong: {
				int id = dataIn.readInt();
				busIn.post(new Op0002Pong(id));
				break;
			}
			case OpCodes.Nickname: {
				String name = readString(dataIn);
				busIn.post(new Op0003Nickname(name));
				break;
			}
			case OpCodes.SessionID: {
				int sessionID = dataIn.readInt();
				busIn.post(new Op0004SessionID(sessionID));
				break;
			}
			case OpCodes.DeletedGames: {
				Set<Integer> gameIDs = readSet(dataIn, intReader);
				busIn.post(new Op0005DeletedGames(gameIDs));
				break;
			}
			case OpCodes.CreatedGames: {
				Map<Integer, String> gameIDNames = readMap(dataIn, intReader,
						stringReader);
				busIn.post(new Op0006CreatedGames(gameIDNames));
				break;
			}
			case OpCodes.CreateJoinGame: {
				String name = readString(dataIn);
				busIn.post(new Op0007CreateJoinGame(name));
				break;
			}
			case OpCodes.LeaveJoinGame: {
				int gameIDValue = dataIn.readInt();
				Optional<Integer> gameID = gameIDValue == 0 ? Optional
						.<Integer> absent() : Optional.of(gameIDValue);
				busIn.post(new Op0008LeaveJoinGame(gameID));
				break;
			}
			case OpCodes.LeftJoinedGame: {
				int gameID = dataIn.readInt();
				busIn.post(new Op0009LeftJoinedGame(gameID));
				break;
			}
			case OpCodes.PlayersJoinedServer: {
				Map<Integer, String> playerIDNames = readMap(dataIn, intReader,
						stringReader);
				busIn.post(new Op000BPlayersJoinedServer(playerIDNames));
				break;
			}
			case OpCodes.PlayersJoinedGame: {
				Set<Integer> playerIDs = readSet(dataIn, intReader);
				busIn.post(new Op000CPlayersJoinedGame(playerIDs));
				break;
			}
			case OpCodes.PlayersLeftGame: {
				Set<Integer> playerIDs = readSet(dataIn, intReader);
				busIn.post(new Op000DPlayersLeftGame(playerIDs));
				break;
			}
			case OpCodes.PlayersLeftServer: {
				Set<Integer> playerIDs = readSet(dataIn, intReader);
				busIn.post(new Op000FPlayersLeftServer(playerIDs));
				break;
			}
			default:
				busIn.post(new OpUnknown(opCode));
				break;
			}
		} catch (EOFException e) {
			System.err.println("Hit end of packet frame! OpCode=" + opCode
					+ " Size=" + packetLength);
		}
	}

	@Override
	protected void run() throws Exception {
		while (true) {
			while (!socketConnection.isConnected()) {
				try {
					socketConnection.connect();
					busIn.post(new NetConnect(socketConnection.getHost(),
							socketConnection.getPort()));
				} catch (Exception e) {
					System.err.println(e.getMessage());
					Thread.yield();
				}
			}
			try {
				while (true) {
					handlePacket();
				}
			} catch (IOException e) {
				socketConnection.disconnect();
				busIn.post(new NetDisconnect(true, "["
						+ e.getClass().getSimpleName() + "] " + e.getMessage()));
			}
		}
	}

	@Subscribe
	public void sendOp0001Ping(Op0001Ping e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getID());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0002Pong(Op0002Pong e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getID());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0003Nickname(Op0003Nickname e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeString(dataOut, e.getName());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0004SessionID(Op0004SessionID e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getSessionID());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0005DeletedGames(Op0005DeletedGames e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeSet(dataOut, intWriter, e.getGameIDs());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0006CreatedGames(Op0006CreatedGames e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeMap(dataOut, intWriter, stringWriter, e.getGameIDNames());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0007CreateJoinGame(Op0007CreateJoinGame e)
			throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeString(dataOut, e.getName());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0008LeaveJoinGame(Op0008LeaveJoinGame e)
			throws IOException {
		dataOut.writeShort(e.getOpCode());
		Optional<Integer> joinGameID = e.getJoinGameID();
		dataOut.writeInt(joinGameID.isPresent() ? joinGameID.get() : 0);
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0009LeftJoinedGame(Op0009LeftJoinedGame e)
			throws IOException {
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getGameID());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000BPlayersJoinedServer(Op000BPlayersJoinedServer e)
			throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeMap(dataOut, intWriter, stringWriter, e.getPlayerIDNames());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000CPlayersJoinedGame(Op000CPlayersJoinedGame e)
			throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeSet(dataOut, intWriter, e.getPlayerIDs());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000DPlayersLeftGame(Op000DPlayersLeftGame e)
			throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeSet(dataOut, intWriter, e.getPlayerIDs());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000FPlayersLeftServer(Op000FPlayersLeftServer e)
			throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeSet(dataOut, intWriter, e.getPlayerIDs());
		dataOut.flush();
	}
}
