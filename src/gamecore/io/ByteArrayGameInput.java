package gamecore.io;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * GameInput实现。内部使用ByteArrayInputStream。
 */
public class ByteArrayGameInput extends AbstractGameInput {

	private DataInputStream in;

	public ByteArrayGameInput(byte[] data) {
		in = new DataInputStream(new FastByteArrayInputStream(data));
	}

	public ByteArrayGameInput(DataInputStream in) {
		this.in = in;
	}

	@Override
	public int remaining() {
		try {
			return in.available();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean getBoolean() {
		try {
			return in.readBoolean();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte get() {
		try {
			return in.readByte();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public short getShort() {
		try {
			return in.readShort();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getInt() {
		try {
			return in.readInt();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long getLong() {
		try {
			return in.readLong();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public char getChar() {
		try {
			return in.readChar();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public float getFloat() {
		try {
			return in.readFloat();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getDouble() {
		try {
			return in.readDouble();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] getBytes() {
		try {
			int length = in.readInt();
			byte[] bytes = new byte[length];
			in.read(bytes);

			return bytes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int[] getInts() {
		try {
			int length = in.readInt();
			int[] ints = new int[length];
			for (int i = 0; i < length; i++) {
				ints[i] = in.readInt();
			}
			return ints;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] getBytesNoLength() {
		try {
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			return bytes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getString() {
		try {
			int length = in.readInt();
			if (length == 0) {
				return "";
			}

			byte[] bytes = new byte[length];
			in.read(bytes);

			return new String(bytes, GameMessage.DEFAULT_CHARSET);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] getCopyRemainBytes() {

		try {
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			in = new DataInputStream(new FastByteArrayInputStream(bytes));
			return bytes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
