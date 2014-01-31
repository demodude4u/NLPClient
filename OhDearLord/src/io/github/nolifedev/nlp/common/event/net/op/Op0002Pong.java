package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class Op0002Pong implements OpEvent {
	private final int opCode = OpCodes.Pong;
	private final int ID;
}
