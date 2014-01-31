package io.github.nolifedev.nlp.common.event.net.op;

import lombok.Data;

@Data
public class OpUnknown implements OpEvent {

	@Override
	public int getOpCode() {
		return -1;
	}

}
