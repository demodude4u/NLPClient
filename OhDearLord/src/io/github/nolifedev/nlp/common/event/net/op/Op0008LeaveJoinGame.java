package io.github.nolifedev.nlp.common.event.net.op;

import io.github.nolifedev.nlp.common.net.OpCodes;
import lombok.Data;

import com.google.common.base.Optional;

@Data
public class Op0008LeaveJoinGame implements OpEvent {
	private final int opCode = OpCodes.LeaveJoinGame;
	private final Optional<Integer> joinGameID;
}
