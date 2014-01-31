package io.github.nolifedev.nlp.common.net;

public final class OpCodes {
	public static final int Ping = 0x1;
	public static final int Pong = 0x2;
	public static final int Nickname = 0x3;
	public static final int SessionID = 0x4;
	public static final int DeletedGames = 0x5;
	public static final int CreatedGames = 0x6;
	public static final int CreateJoinGame = 0x7;
	public static final int LeaveJoinGame = 0x8;
	public static final int LeftGame = 0x9;
	// 0xA
	public static final int PlayersJoinedServer = 0xB;
	public static final int PlayersJoinedGame = 0xC;
	public static final int PlayersLeftGame = 0xD;
	// 0xE
	public static final int PlayersLeftServer = 0xF;

	private OpCodes() {
	}
}
