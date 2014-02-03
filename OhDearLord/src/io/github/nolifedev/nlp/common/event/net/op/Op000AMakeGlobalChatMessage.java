package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class Op000AMakeGlobalChatMessage implements OpEvent {
	private final int opCode = OpCodes.MakeGlobalChatMessage;
	private final String message;
}
