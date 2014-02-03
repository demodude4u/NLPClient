package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class Op0004PlayerID implements OpEvent {
	private final int opCode = OpCodes.PlayerID;
	private final int playerID;
}
