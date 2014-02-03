package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class Op0012MakePrivateChatMessage implements OpEvent {
	private final int opCode = OpCodes.MakePrivateChatMessage;
	private final int playerID;
	private final String message;
}
