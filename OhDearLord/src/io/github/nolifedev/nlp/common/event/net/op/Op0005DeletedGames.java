package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;

import java.util.Set;

import lombok.Data;

@Data
public class Op0005DeletedGames implements OpEvent {
	private final int opCode = OpCodes.DeletedGames;
	private final Set<Integer> gameIDs;
}
