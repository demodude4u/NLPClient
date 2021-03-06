package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;

import java.util.Map;

import lombok.Data;

@Data
public class Op0006CreatedGames implements OpEvent {
	private final int opCode = OpCodes.CreatedGames;
	private final Map<Integer, String> gameIDNames;
}
