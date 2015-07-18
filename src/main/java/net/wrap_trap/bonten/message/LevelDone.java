package net.wrap_trap.bonten.message;

import akka.actor.ActorRef;

public class LevelDone extends Message {

  private ActorRef selfOrRef;

  public LevelDone(ActorRef selfOrRef) {
    super();
    this.selfOrRef = selfOrRef;
  }

  public ActorRef getSelfOrRef() {
    return selfOrRef;
  }
}
