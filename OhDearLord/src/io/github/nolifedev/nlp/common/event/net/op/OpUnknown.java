package io.github.nolifedev.nlp.common.event.net.op;

import lombok.Data;

@Data
public class OpUnknown implements OpEvent {
	private final int opCode;
}
