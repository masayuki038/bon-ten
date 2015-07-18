package net.wrap_trap.bonten.message;

import akka.actor.ActorRef;

public class StepDone extends Message {

  private ActorRef mRef;

  public StepDone(ActorRef mRef) {
    super();
    this.mRef = mRef;
  }

  public ActorRef getMRef() {
    return mRef;
  }
}
