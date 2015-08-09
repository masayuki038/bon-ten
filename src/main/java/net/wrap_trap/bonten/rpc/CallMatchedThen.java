package net.wrap_trap.bonten.rpc;

import akka.actor.ActorRef;
import net.wrap_trap.bonten.message.Message;

@FunctionalInterface
public interface CallMatchedThen {
  void apply(Message message, ActorRef mref) throws Exception;
}
