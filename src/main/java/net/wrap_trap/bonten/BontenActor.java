package net.wrap_trap.bonten;

import net.wrap_trap.bonten.message.Message;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public abstract class BontenActor extends UntypedActor implements PlainRpc {

  @Override
  public ActorRef sendCall(ActorRef actorRef, Message message) {
    ActorRef mref = getContext().watch(actorRef);
    actorRef.tell(message, getSender());
    return mref;
  }
}
