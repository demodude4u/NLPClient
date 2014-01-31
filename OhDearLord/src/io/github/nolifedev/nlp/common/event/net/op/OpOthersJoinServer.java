package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

@Data
public class OpOthersJoinServer implements OpEvent {
	private final int opCode = OpCodes.OthersJoinServer;
	private final int id;
}
