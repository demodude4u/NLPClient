package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class OpPartGame implements OpEvent {
	private final int opCode = OpCodes.Ping;
	private final int id;
}
