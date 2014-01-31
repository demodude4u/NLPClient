import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

public class WhatsUp {

	private static final int OP_PING = 1;
	private static final int OP_PONG = 2;
	private static final int OP_NICKNAME = 3;
	private static final int OP_SESSIONID = 4;
	private static Queue<Function<DataOutputStream, Void>> outputActions = new ConcurrentLinkedQueue<>();
	private static volatile Socket socket = null;

	private static ThreadLocal<Random> rand = new ThreadLocal<Random>() {
		@Override
		protected Random initialValue() {
			return new Random();
		}
	};

	private static synchronized void initializeHeartBeat(final Socket socket) {
		new Thread() {
			@Override
			public void run() {
				Socket mySocket = socket;
				while (mySocket == WhatsUp.socket) {
					try {
						queueOutputAction(sendPing());
						Thread.sleep(5000);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("OutputActions Exit!");
			}
		}.start();
	}

	private static synchronized void initializeOutputActions(
			final Socket socket, final DataOutputStream dataOutputStream) {
		new Thread() {
			@Override
			public void run() {
				Socket mySocket = socket;
				while (mySocket == WhatsUp.socket) {
					while (outputActions.isEmpty()) {
						Thread.yield();
					}
					Function<DataOutputStream, Void> function = outputActions
							.poll();
					try {
						function.apply(dataOutputStream);
					} catch (Exception e) {
						System.out.println("[OUTPUT] "
								+ e.getClass().getSimpleName() + ": "
								+ e.getMessage());
						e.printStackTrace();
					}
					Thread.yield();
				}
				System.out.println("OutputActions Exit!");
			}
		}.start();
	}

	public static void main(String[] args) throws InterruptedException {
		new Thread() {
			@Override
			public void run() {
				try {
					runClient();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static void queueOutputAction(
			Function<DataOutputStream, Void> function) {
		outputActions.offer(function);
	}

	private static void runClient() throws InterruptedException {
		while (true) {
			try {
				try (Socket socket = new Socket("retep998.no-ip.org", 273)) {
					WhatsUp.socket = socket;
					System.out.println("Connected!");
					try (DataOutputStream dataOutputStream = new DataOutputStream(
							socket.getOutputStream())) {
						try (DataInputStream dataInputStream = new DataInputStream(
								socket.getInputStream())) {

							initializeOutputActions(socket, dataOutputStream);
							initializeHeartBeat(socket);

							queueOutputAction(sendNickname("TheAnswerToThatLUEIs42"));
							System.out.println("Sent Nickname!");

							while (true) {
								long packetLength = uInt(dataInputStream
										.readInt());
								int opCode = uShort(dataInputStream.readShort());
								switch (opCode) {
								case OP_PING: {
									int id = dataInputStream.readInt();
									queueOutputAction(sendPong(id));
									break;
								}
								case OP_PONG: {
									int id = dataInputStream.readInt();
									validatePong(id);
									break;
								}
								case OP_SESSIONID: {
									int sessionID = dataInputStream.readInt();
									System.out.println("Session ID! "
											+ sessionID);
									break;
								}
								default:
									dataInputStream
											.skipBytes((int) (packetLength - 2));
									System.out.println("Unknown Opcode: "
											+ String.format("%04X", opCode));
									break;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				System.err.println(e.getClass().getSimpleName() + ": "
						+ e.getMessage());
				Thread.yield();
			}
		}
	}

	private static Function<DataOutputStream, Void> sendNickname(
			final String name) throws IOException {
		return new Function<DataOutputStream, Void>() {
			@Override
			public Void apply(DataOutputStream dataOutputStream) {
				try {
					// System.out.println("Sending Pong!" + id);
					dataOutputStream.writeInt(2 + 4 + name.length());
					dataOutputStream.writeShort(OP_NICKNAME);
					writePonyString(dataOutputStream, name);
					dataOutputStream.flush();
				} catch (IOException e) {
					Throwables.propagate(e);
				}
				return null;
			}

			private void writePonyString(DataOutputStream dataOutputStream,
					String name) throws IOException {
				dataOutputStream.writeInt(name.length());
				dataOutputStream.write(name.getBytes(StandardCharsets.UTF_8));
			}
		};
	}

	private static Function<DataOutputStream, Void> sendPing()
			throws IOException {
		return new Function<DataOutputStream, Void>() {
			@Override
			public Void apply(DataOutputStream dataOutputStream) {
				try {
					int id = rand.get().nextInt();
					// System.out.println("Sending Ping! " + id);
					dataOutputStream.writeInt(6);// 2 bytes?
					dataOutputStream.writeShort(OP_PING);
					dataOutputStream.writeInt(id);
					dataOutputStream.flush();
				} catch (IOException e) {
					Throwables.propagate(e);
				}
				return null;
			}
		};
	}

	private static Function<DataOutputStream, Void> sendPong(final int id)
			throws IOException {
		return new Function<DataOutputStream, Void>() {
			@Override
			public Void apply(DataOutputStream dataOutputStream) {
				try {
					// System.out.println("Sending Pong!" + id);
					dataOutputStream.writeInt(6);// 2 bytes?
					dataOutputStream.writeShort(OP_PONG);
					dataOutputStream.writeInt(id);
					dataOutputStream.flush();
				} catch (IOException e) {
					Throwables.propagate(e);
				}
				return null;
			}
		};
	}

	private static long uInt(int num) {
		return num & 0x00000000ffffffffL;
	}

	private static int uShort(short num) {
		return num & 0x0000ffff;
	}

	private static void validatePong(int id) {
		// System.out.println("Recieved Pong! " + id);
		// TODO
	}

}
