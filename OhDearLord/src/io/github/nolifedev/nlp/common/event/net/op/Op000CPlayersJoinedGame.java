package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;

import java.util.Set;

import lombok.Data;

@Data
public class Op000CPlayersJoinedGame implements OpEvent {
	private final int opCode = OpCodes.PlayersJoinedGame;
	private final Set<Integer> playerIDs;
}
