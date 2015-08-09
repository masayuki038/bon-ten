package net.wrap_trap.bonten;

import net.wrap_trap.bonten.message.Message;
import net.wrap_trap.bonten.rpc.AsyncRequest;
import net.wrap_trap.bonten.rpc.Reply;
import net.wrap_trap.bonten.rpc.ReplyMatchedThen;
import net.wrap_trap.bonten.rpc.CallMatchedThen;
import net.wrap_trap.bonten.rpc.CastMatchedThen;
import net.wrap_trap.bonten.rpc.SyncRequest;
import scala.concurrent.Await;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;

public abstract class BontenActor extends UntypedActor implements PlainRpc {

  @Override
  public ActorRef sendCall(ActorRef actorRef, Message message) {
    ActorRef mref = getContext().watch(actorRef);
    actorRef.tell(new SyncRequest(message, mref), getSender());
    return mref;
  }
  
  @Override
  public void call(ActorRef actorRef, Message message) {
    ActorRef mref = getContext().watch(actorRef);
    try {
      Await.result(
  		  Patterns.ask(
  			  actorRef, 
  			  new SyncRequest(message, mref),
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
  public void sendReply(ActorRef actorRef, Message message, ActorRef mref) {
    actorRef.tell(new Reply(message, mref), getSender());
  }

  protected void matchCall(Object request, Class<?> t, CallMatchedThen f) throws Exception {
    if(!(request instanceof SyncRequest)) {
      return;
    }
    
    SyncRequest syncRequest = (SyncRequest)request;
    Message message = syncRequest.getMessage();
    if(message.getClass() != t) {
      return;
    }
    
    f.apply(message, syncRequest.getMref());
  }
  
  protected void matchCast(Object object, Class<?> t, CastMatchedThen f) throws Exception {
    if(!(object instanceof AsyncRequest)) {
      return;
    }
    
    AsyncRequest asyncRequest = (AsyncRequest)object;
    Message message = asyncRequest.getMessage();
    if(message.getClass() != t) {
      return;
    }
    
    f.apply(message);
  }
  
  protected void matchReply(Object object, Class<?> t, ReplyMatchedThen f) throws Exception {
    if(!(object instanceof Reply)) {
      return;
    }
    
    Reply reply = (Reply)object;
    Message message = reply.getMessage();
    if(message.getClass() != t) {
      return;
    }
    
    f.apply(message, reply.getMref());
  }
}
