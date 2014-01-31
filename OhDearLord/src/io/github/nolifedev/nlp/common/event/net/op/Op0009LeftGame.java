package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class Op0009LeftGame implements OpEvent {
	private final int opCode = OpCodes.LeftGame;
	private final int gameID;
}
