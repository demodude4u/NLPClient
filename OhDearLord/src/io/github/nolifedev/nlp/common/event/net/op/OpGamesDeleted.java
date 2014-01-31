package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;

import java.util.Set;

import lombok.Data;

@Data
public class OpGamesDeleted implements OpEvent {
	private final int opCode = OpCodes.GamesDeleted;
	private final Set<Integer> gameIDs;
}
