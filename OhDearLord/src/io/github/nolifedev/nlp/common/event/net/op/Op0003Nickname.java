package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class Op0003Nickname implements OpEvent {
	private final int opCode = OpCodes.Nickname;
	private final String name;
}
