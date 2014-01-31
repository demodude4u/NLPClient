package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;

import java.util.Set;

import lombok.Data;

@Data
public class Op000DPlayersLeftGame implements OpEvent {
	private final int opCode = OpCodes.PlayersLeftGame;
	private final Set<Integer> playerIDs;
}
