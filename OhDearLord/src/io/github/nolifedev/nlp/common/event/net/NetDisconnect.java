package io.github.nolifedev.nlp.common.event.net;

import lombok.Data;

@Data
public class NetDisconnect implements NetEvent {
	private final boolean error;
	private final String message;
}
