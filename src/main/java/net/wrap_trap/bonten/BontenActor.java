package net.wrap_trap.bonten;

import net.wrap_trap.bonten.message.Message;
import net.wrap_trap.bonten.rpc.AsyncRequest;
import net.wrap_trap.bonten.rpc.Reply;
import net.wrap_trap.bonten.rpc.SyncRequest;
import scala.concurrent.Await;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;

public abstract class BontenActor extends UntypedActor implements PlainRpc {

  @Override
  public ActorRef sendCall(ActorRef actorRef, Message message) {
    ActorRef mref = getContext().watch(actorRef);
    actorRef.tell(new SyncRequest(message), getSender());
    return mref;
  }
  
  @Override
  public void call(ActorRef actorRef, Message message) {
    ActorRef mref = getContext().watch(actorRef);
    try {
      Await.result(
  		  Patterns.ask(
  			  actorRef, 
  			  new SyncRequest(message),
  			  Bonten.ASK_TIMEOUT
  			), 
  		  Bonten.ASK_TIMEOUT.duration()
  	  );
    } catch(Exception e) {
      throw new BontenException(e);
    } finally {
      if(mref != null) {
        getContext().unwatch(mref);
      }
    }
  }

  @Override
  public void sendReply(ActorRef actorRef, Object message) {
    actorRef.tell(new Reply(message), getSender());
  }
  
  protected boolean matchCall(Object message, Class<?> t) {
    if(!(message instanceof SyncRequest)) {
      return false;
    }
    return (message.getClass() == t);
  }
  
  protected boolean matchCast(Object message, Class<?> t) {
    if(!(message instanceof AsyncRequest)) {
      return false;
    }
    return (message.getClass() == t);
  }
  
  protected boolean matchReply(Object message, Class<?> t) {
    if(!(message instanceof Reply)) {
      return false;
    }
    return (message.getClass() == t);
  }
}
