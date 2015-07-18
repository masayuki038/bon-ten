package net.wrap_trap.bonten;

import akka.actor.ActorContext;
import akka.actor.ActorPath;
import akka.actor.ActorRef;

public class NullActorRef extends ActorRef {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 6473100990366516096L;

  @Override
  public boolean isTerminated() {
    throw new IllegalStateException("Not Implemented");
  }

  @Override
  public ActorPath path() {
    throw new IllegalStateException("Not Implemented");
  }

  @Override
  public void forward(Object message, ActorContext context) {
    throw new IllegalStateException("Not Implemented");
  }  
}
