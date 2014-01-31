package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class OpCreateJoinGame implements OpEvent {
	private final int opCode = OpCodes.CreateJoinGame;
	private final String name;
}
