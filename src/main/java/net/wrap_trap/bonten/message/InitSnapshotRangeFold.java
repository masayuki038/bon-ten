package net.wrap_trap.bonten.message;

import java.util.List;

import net.wrap_trap.bonten.KeyRange;
import akka.actor.ActorRef;

public class InitSnapshotRangeFold extends Message {

  private ActorRef workerPid;
  private KeyRange range;
  private List<ActorRef> list;

  public InitSnapshotRangeFold(ActorRef workerPid, KeyRange range, List<ActorRef> list) {
    super();
    this.workerPid = workerPid;
    this.range = range;
    this.list = list;
  }

  public ActorRef getWorkerPid() {
    return workerPid;
  }

  public KeyRange getRange() {
    return range;
  }

  public List<ActorRef> getList() {
    return list;
  }
}
