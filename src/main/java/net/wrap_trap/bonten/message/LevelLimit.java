package net.wrap_trap.bonten.message;

import akka.actor.ActorRef;

public class LevelLimit extends Message {

  private ActorRef selfOrRef;
  private byte[] lastKey;

  public LevelLimit(ActorRef selfOrRef, byte[] lastKey) {
    super();
    this.selfOrRef = selfOrRef;
    this.lastKey = lastKey;
  }

  public ActorRef getSelfOrRef() {
    return selfOrRef;
  }
  
  public byte[] getLastKey() {
    return lastKey;
  }
}
