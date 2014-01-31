package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;

import java.util.Map;

import lombok.Data;

@Data
public class Op000BPlayersJoinedServer implements OpEvent {
	private final int opCode = OpCodes.PlayersJoinedServer;
	private final Map<Integer, String> playerIDNames;
}
