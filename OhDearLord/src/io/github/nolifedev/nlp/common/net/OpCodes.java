package io.github.nolifedev.nlp.common.net;

public final class OpCodes {
	public static final int Ping = 0x1;
	public static final int Pong = 0x2;
	public static final int Nickname = 0x3;
	public static final int PlayerID = 0x4;
	public static final int DeletedGames = 0x5;
	public static final int CreatedGames = 0x6;
	public static final int CreateJoinGame = 0x7;
	public static final int LeaveJoinGame = 0x8;
	public static final int LeftJoinedGame = 0x9;
	public static final int MakeGlobalChatMessage = 0xA;
	public static final int PlayersJoinedServer = 0xB;
	public static final int PlayersJoinedGame = 0xC;
	public static final int PlayersLeftGame = 0xD;
	public static final int GlobalChatMessages = 0xE;
	public static final int PlayersLeftServer = 0xF;
	public static final int MakeGameChatMessage = 0x10;
	public static final int GameChatMessages = 0x11;
	public static final int MakePrivateChatMessage = 0x12;
	public static final int PrivateChatMessages = 0x13;

	private OpCodes() {
	}
}
