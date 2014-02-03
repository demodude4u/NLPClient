package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;

import java.util.List;
import java.util.Map.Entry;

import lombok.Data;

@Data
public class Op0011GameChatMessages implements OpEvent {
	private final int opCode = OpCodes.GameChatMessages;
	private final List<Entry<Integer, String>> playerIDMessages;
}
