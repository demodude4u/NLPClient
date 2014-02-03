package io.github.nolifedev.nlp.common.net;

import io.github.nolifedev.nlp.client.util.SimpleEntry;
import io.github.nolifedev.nlp.common.ServiceHandler;
import io.github.nolifedev.nlp.common.event.net.NetConnect;
import io.github.nolifedev.nlp.common.event.net.NetDisconnect;
import io.github.nolifedev.nlp.common.event.net.op.Op0001Ping;
import io.github.nolifedev.nlp.common.event.net.op.Op0002Pong;
import io.github.nolifedev.nlp.common.event.net.op.Op0003Nickname;
import io.github.nolifedev.nlp.common.event.net.op.Op0004PlayerID;
import io.github.nolifedev.nlp.common.event.net.op.Op0005DeletedGames;
import io.github.nolifedev.nlp.common.event.net.op.Op0006CreatedGames;
import io.github.nolifedev.nlp.common.event.net.op.Op0007CreateJoinGame;
import io.github.nolifedev.nlp.common.event.net.op.Op0008LeaveJoinGame;
import io.github.nolifedev.nlp.common.event.net.op.Op0009LeftJoinedGame;
import io.github.nolifedev.nlp.common.event.net.op.Op000AMakeGlobalChatMessage;
import io.github.nolifedev.nlp.common.event.net.op.Op000BPlayersJoinedServer;
import io.github.nolifedev.nlp.common.event.net.op.Op000CPlayersJoinedGame;
import io.github.nolifedev.nlp.common.event.net.op.Op000DPlayersLeftGame;
import io.github.nolifedev.nlp.common.event.net.op.Op000EGlobalChatMessages;
import io.github.nolifedev.nlp.common.event.net.op.Op000FPlayersLeftServer;
import io.github.nolifedev.nlp.common.event.net.op.Op0010MakeGameChatMessage;
import io.github.nolifedev.nlp.common.event.net.op.Op0011GameChatMessages;
import io.github.nolifedev.nlp.common.event.net.op.Op0012MakePrivateChatMessage;
import io.github.nolifedev.nlp.common.event.net.op.Op0013PrivateChatMessages;
import io.github.nolifedev.nlp.common.event.net.op.OpUnknown;
import io.github.nolifedev.nlp.common.util.Unsigned;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.inject.Inject;

public class SocketEventAdapter extends AbstractExecutionThreadService {

	private class EntryReader<K, V> implements IOReader<Entry<K, V>> {
		private final IOReader<K> keyReader;
		private final IOReader<V> valueReader;

		public EntryReader(IOReader<K> keyReader, IOReader<V> valueReader) {
			this.keyReader = keyReader;
			this.valueReader = valueReader;
		}

		@Override
		public Entry<K, V> read(DataInput dataIn) throws IOException {
			final K key = keyReader.read(dataIn);
			final V value = valueReader.read(dataIn);
			return new SimpleEntry<>(key, value);
		}
	}

	private class EntryWriter<K, V> implements IOWriter<Entry<K, V>> {
		private final IOWriter<K> keyWriter;
		private final IOWriter<V> valueWriter;

		public EntryWriter(IOWriter<K> keyWriter, IOWriter<V> valueWriter) {
			this.keyWriter = keyWriter;
			this.valueWriter = valueWriter;
		}

		@Override
		public void write(DataOutput dataOut, Entry<K, V> value)
				throws IOException {
			keyWriter.write(dataOut, value.getKey());
			valueWriter.write(dataOut, value.getValue());
		}
	}

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

	private static <T> List<T> readCollection(DataInput dataIn,
			IOReader<T> reader) throws IOException {
		int count = dataIn.readInt();
		List<T> set = Lists.newArrayListWithExpectedSize(count);
		for (int i = 0; i < count; i++) {
			set.add(reader.read(dataIn));
		}
		return set;
	}

	private static <K, V> Map<K, V> readMap(DataInput dataIn,
			IOReader<K> keyReader, IOReader<V> valueReader) throws IOException {
		int count = dataIn.readInt();
		Map<K, V> map = Maps.newLinkedHashMap();
		for (int i = 0; i < count; i++) {
			map.put(keyReader.read(dataIn), valueReader.read(dataIn));
		}
		return map;
	}

	private static String readString(DataInput dataIn) throws IOException {
		byte[] buf = new byte[dataIn.readInt()];
		dataIn.readFully(buf);
		return new String(buf, StandardCharsets.UTF_8);
	}

	private static <T> void writeCollection(DataOutput dataOut,
			IOWriter<T> writer, Collection<T> collection) throws IOException {
		dataOut.writeInt(collection.size());
		for (T value : collection) {
			writer.write(dataOut, value);
		}
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
			case OpCodes.PlayerID: {
				int sessionID = dataIn.readInt();
				busIn.post(new Op0004PlayerID(sessionID));
				break;
			}
			case OpCodes.DeletedGames: {
				Set<Integer> gameIDs = Sets.newLinkedHashSet(readCollection(
						dataIn, intReader));
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
			case OpCodes.MakeGlobalChatMessage: {
				String message = readString(dataIn);
				busIn.post(new Op000AMakeGlobalChatMessage(message));
				break;
			}
			case OpCodes.PlayersJoinedServer: {
				Map<Integer, String> playerIDNames = readMap(dataIn, intReader,
						stringReader);
				busIn.post(new Op000BPlayersJoinedServer(playerIDNames));
				break;
			}
			case OpCodes.PlayersJoinedGame: {
				Set<Integer> playerIDs = Sets.newLinkedHashSet(readCollection(
						dataIn, intReader));
				busIn.post(new Op000CPlayersJoinedGame(playerIDs));
				break;
			}
			case OpCodes.PlayersLeftGame: {
				Set<Integer> playerIDs = Sets.newLinkedHashSet(readCollection(
						dataIn, intReader));
				busIn.post(new Op000DPlayersLeftGame(playerIDs));
				break;
			}
			case OpCodes.GlobalChatMessages: {
				List<Entry<Integer, String>> messages = readCollection(dataIn,
						new EntryReader<>(intReader, stringReader));
				busIn.post(new Op000EGlobalChatMessages(messages));
				break;
			}
			case OpCodes.PlayersLeftServer: {
				Set<Integer> playerIDs = Sets.newLinkedHashSet(readCollection(
						dataIn, intReader));
				busIn.post(new Op000FPlayersLeftServer(playerIDs));
				break;
			}
			case OpCodes.MakeGameChatMessage: {
				String message = readString(dataIn);
				busIn.post(new Op0010MakeGameChatMessage(message));
				break;
			}
			case OpCodes.GameChatMessages: {
				List<Entry<Integer, String>> messages = readCollection(dataIn,
						new EntryReader<>(intReader, stringReader));
				busIn.post(new Op0011GameChatMessages(messages));
				break;
			}
			case OpCodes.MakePrivateChatMessage: {
				int playerID = dataIn.readInt();
				String message = readString(dataIn);
				busIn.post(new Op0012MakePrivateChatMessage(playerID, message));
				break;
			}
			case OpCodes.PrivateChatMessages: {
				List<Entry<Integer, String>> messages = readCollection(dataIn,
						new EntryReader<>(intReader, stringReader));
				busIn.post(new Op0013PrivateChatMessages(messages));
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
	public void sendOp0001(Op0001Ping e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getID());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0002(Op0002Pong e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getID());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0003(Op0003Nickname e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeString(dataOut, e.getName());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0004(Op0004PlayerID e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getPlayerID());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0005(Op0005DeletedGames e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeCollection(dataOut, intWriter, e.getGameIDs());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0006(Op0006CreatedGames e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeMap(dataOut, intWriter, stringWriter, e.getGameIDNames());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0007(Op0007CreateJoinGame e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeString(dataOut, e.getName());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0008(Op0008LeaveJoinGame e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		Optional<Integer> joinGameID = e.getJoinGameID();
		dataOut.writeInt(joinGameID.isPresent() ? joinGameID.get() : 0);
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0009(Op0009LeftJoinedGame e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		dataOut.writeInt(e.getGameID());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000A(Op000AMakeGlobalChatMessage e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeString(dataOut, e.getMessage());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000B(Op000BPlayersJoinedServer e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeMap(dataOut, intWriter, stringWriter, e.getPlayerIDNames());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000C(Op000CPlayersJoinedGame e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeCollection(dataOut, intWriter, e.getPlayerIDs());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000D(Op000DPlayersLeftGame e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeCollection(dataOut, intWriter, e.getPlayerIDs());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000E(Op000EGlobalChatMessages e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeCollection(dataOut, new EntryWriter<>(intWriter, stringWriter),
				e.getPlayerIDMessages());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp000F(Op000FPlayersLeftServer e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeCollection(dataOut, intWriter, e.getPlayerIDs());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0010(Op0010MakeGameChatMessage e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeString(dataOut, e.getMessage());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0011(Op0011GameChatMessages e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeCollection(dataOut, new EntryWriter<>(intWriter, stringWriter),
				e.getPlayerIDMessages());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0012(Op0012MakePrivateChatMessage e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeString(dataOut, e.getMessage());
		dataOut.flush();
	}

	@Subscribe
	public void sendOp0013(Op0013PrivateChatMessages e) throws IOException {
		dataOut.writeShort(e.getOpCode());
		writeCollection(dataOut, new EntryWriter<>(intWriter, stringWriter),
				e.getPlayerIDMessages());
		dataOut.flush();
	}
}
