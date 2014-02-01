package io.github.nolifedev.nlp.common.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PacketOutputStream extends OutputStream implements DataOutput {

	private final ByteArrayOutputStream baos;
	private final DataOutputStream out;
	private final DataOutputStream dos;

	public PacketOutputStream(OutputStream out) {
		this.out = new DataOutputStream(out);
		baos = new ByteArrayOutputStream();
		dos = new DataOutputStream(baos);
	}

	@Override
	public void close() throws IOException {
		baos.close();
		out.close();
	}

	@Override
	public void flush() throws IOException {
		if (baos.size() == 0) {
			return;
		}
		out.writeInt(baos.size());
		baos.writeTo(out);
		baos.reset();
		out.flush();
	}

	@Override
	public void write(byte[] b) throws IOException {
		dos.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		dos.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		dos.write(b);
	}

	@Override
	public final void writeBoolean(boolean v) throws IOException {
		dos.writeBoolean(v);
	}

	@Override
	public final void writeByte(int v) throws IOException {
		dos.writeByte(v);
	}

	@Override
	public final void writeBytes(String s) throws IOException {
		dos.writeBytes(s);
	}

	@Override
	public final void writeChar(int v) throws IOException {
		dos.writeChar(v);
	}

	@Override
	public final void writeChars(String s) throws IOException {
		dos.writeChars(s);
	}

	@Override
	public final void writeDouble(double v) throws IOException {
		dos.writeDouble(v);
	}

	@Override
	public final void writeFloat(float v) throws IOException {
		dos.writeFloat(v);
	}

	@Override
	public final void writeInt(int v) throws IOException {
		dos.writeInt(v);
	}

	@Override
	public final void writeLong(long v) throws IOException {
		dos.writeLong(v);
	}

	@Override
	public final void writeShort(int v) throws IOException {
		dos.writeShort(v);
	}

	@Override
	public final void writeUTF(String str) throws IOException {
		dos.writeUTF(str);
	}
}
