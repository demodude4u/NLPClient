package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.event.net.NetEvent;

public interface OpEvent extends NetEvent {
	public int getOpCode();
}
