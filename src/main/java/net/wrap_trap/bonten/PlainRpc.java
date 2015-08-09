package net.wrap_trap.bonten;

import net.wrap_trap.bonten.message.Message;
import akka.actor.ActorRef;

public interface PlainRpc {
  ActorRef sendCall(ActorRef actorRef, Message message);
  void call(ActorRef actorRef, Message message);
  void sendReply(ActorRef actorRef, Message message, ActorRef mref);
}
