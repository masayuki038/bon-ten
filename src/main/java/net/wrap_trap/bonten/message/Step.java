package net.wrap_trap.bonten.message;

import akka.actor.ActorRef;

public class Step extends Message{

  private ActorRef mergeRef;
  private int howMany;

  public Step(ActorRef mergeRef, int howMany) {
    super();
    this.mergeRef = mergeRef;
    this.howMany = howMany;
  }

  protected ActorRef getMergeRef() {
    return mergeRef;
  }



  protected int getHowMany() {
    return howMany;
  }
}
