package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;

import java.util.List;
import java.util.Map.Entry;

import lombok.Data;

@Data
public class Op000EGlobalChatMessages implements OpEvent {
	private final int opCode = OpCodes.GlobalChatMessages;
	private final List<Entry<Integer, String>> playerIDMessages;
}
