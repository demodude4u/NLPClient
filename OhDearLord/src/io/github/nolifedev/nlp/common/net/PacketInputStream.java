package io.github.nolifedev.nlp.common.net;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.Queue;

import com.google.common.collect.Queues;

public class PacketInputStream implements DataInput {

	private static final int CHUNK_SIZE = 16384;

	private final DataInputStream in;
	private final Queue<byte[]> bufferQueue = Queues.newArrayDeque();
	private final Enumeration<ByteArrayInputStream> bufferQueueDigest = new Enumeration<ByteArrayInputStream>() {
		@Override
		public boolean hasMoreElements() {
			return !bufferQueue.isEmpty();
		}

		@Override
		public ByteArrayInputStream nextElement() {
			return new ByteArrayInputStream(bufferQueue.poll());
		}
	};

	private DataInputStream dis;

	public PacketInputStream(InputStream in) {
		this.in = new DataInputStream(in);
	}

	public int next() throws IOException {
		if (dis != null) {
			if (dis.read() != -1) {
				System.err.println("Last Packet was not fully read!");
			}
		}

		int size = in.readInt();
		if (size < 0) {
			throw new IOException("Sum ting wong with size! " + size);
		}
		int remaining = size;
		while (remaining > 0) {
			byte[] buf = new byte[remaining < CHUNK_SIZE ? remaining
					: CHUNK_SIZE];
			in.readFully(buf);
			bufferQueue.offer(buf);
			remaining -= buf.length;
		}
		dis = new DataInputStream(new SequenceInputStream(bufferQueueDigest));
		return size;
	}

	@Override
	public final boolean readBoolean() throws IOException {
		return dis.readBoolean();
	}

	@Override
	public final byte readByte() throws IOException {
		return dis.readByte();
	}

	@Override
	public final char readChar() throws IOException {
		return dis.readChar();
	}

	@Override
	public final double readDouble() throws IOException {
		return dis.readDouble();
	}

	@Override
	public final float readFloat() throws IOException {
		return dis.readFloat();
	}

	@Override
	public final void readFully(byte[] b) throws IOException {
		dis.readFully(b);
	}

	@Override
	public final void readFully(byte[] b, int off, int len) throws IOException {
		dis.readFully(b, off, len);
	}

	@Override
	public final int readInt() throws IOException {
		return dis.readInt();
	}

	@Deprecated
	@Override
	public final String readLine() throws IOException {
		return dis.readLine();
	}

	@Override
	public final long readLong() throws IOException {
		return dis.readLong();
	}

	@Override
	public final short readShort() throws IOException {
		return dis.readShort();
	}

	@Override
	public final int readUnsignedByte() throws IOException {
		return dis.readUnsignedByte();
	}

	@Override
	public final int readUnsignedShort() throws IOException {
		return dis.readUnsignedShort();
	}

	@Override
	public final String readUTF() throws IOException {
		return dis.readUTF();
	}

	@Override
	public final int skipBytes(int n) throws IOException {
		return dis.skipBytes(n);
	}

}
