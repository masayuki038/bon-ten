package net.wrap_trap.bonten.rpc;

import akka.actor.ActorRef;
import net.wrap_trap.bonten.message.Message;

@FunctionalInterface
public interface ReplyMatchedThen {
  void apply(Message message, ActorRef mref) throws Exception;
}
