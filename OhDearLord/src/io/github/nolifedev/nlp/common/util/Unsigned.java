package io.github.nolifedev.nlp.common.util;

public final class Unsigned {
	public static long uInt(int num) {
		return num & 0x00000000ffffffffL;
	}

	public static int uShort(short num) {
		return num & 0x0000ffff;
	}
}
