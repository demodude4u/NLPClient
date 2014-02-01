package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class Op0009LeftJoinedGame implements OpEvent {
	private final int opCode = OpCodes.LeftJoinedGame;
	private final int gameID;
}
