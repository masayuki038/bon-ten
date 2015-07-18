package net.wrap_trap.bonten.message;

import java.util.List;

import net.wrap_trap.bonten.entry.Entry;
import akka.actor.ActorRef;

public class LevelResult extends Message {
  
  private ActorRef selfOrRef;
  private List<Entry> entries;
  
  public LevelResult(ActorRef selfOrRef, List<Entry> entries) {
    super();
    this.selfOrRef = selfOrRef;
    this.entries = entries;
    
  }

  public ActorRef getSelfOrRef() {
    return selfOrRef;
  }

  public List<Entry> getEntries() {
    return entries;
  }
}
