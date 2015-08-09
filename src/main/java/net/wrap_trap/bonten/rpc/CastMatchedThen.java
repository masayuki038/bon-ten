package net.wrap_trap.bonten.rpc;

import net.wrap_trap.bonten.message.Message;

@FunctionalInterface
public interface CastMatchedThen {
  void apply(Message message) throws Exception;
}
