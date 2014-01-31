package io.github.nolifedev.nlp.common.net;

public final class OpCodes {
	public static final int Ping = 0x1;
	public static final int Pong = 0x2;
	public static final int Nickname = 0x3;
	public static final int SessionID = 0x4;
	public static final int GamesDeleted = 0x5;
	public static final int GamesCreated = 0x6;
	public static final int CreateJoinGame = 0x7;
	public static final int JoinGame = 0x8;
	public static final int FailGame = 0x9;
	public static final int SuccessGame = 0xA;
	public static final int OthersJoinServer = 0xB;
	public static final int OtherJoinGame = 0xC;
	public static final int OtherPartGame = 0xD;
	public static final int PartGame = 0xE;

	private OpCodes() {
	}
}
