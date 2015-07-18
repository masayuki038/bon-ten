package net.wrap_trap.bonten.message;

import java.io.File;

import akka.actor.ActorRef;
import net.wrap_trap.bonten.KeyRange;

public class RangeFoldStart extends Message {
  
  private File file;
  private ActorRef workerPid;
  private KeyRange keyRange;

  public RangeFoldStart(File file, ActorRef workerPid, KeyRange keyRange) {
    super();
    this.file = file;
    this.workerPid = workerPid;
    this.keyRange = keyRange;
  }

  public File getFile() {
    return file;
  }

  public ActorRef getWorkerPid() {
    return workerPid;
  }

  public KeyRange getKeyRange() {
    return keyRange;
  }
}
