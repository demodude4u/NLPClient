package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class Op0010MakeGameChatMessage implements OpEvent {
	private final int opCode = OpCodes.MakeGameChatMessage;
	private final String message;
}
