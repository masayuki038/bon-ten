package net.wrap_trap.bonten.message;

import java.io.File;

import akka.actor.ActorRef;

public class RangeFoldDone extends Message {

  private ActorRef pid;
  private File file;

  public RangeFoldDone(ActorRef pid, File file) {
    super();
    this.pid = pid;
    this.file = file;
  }
  
  public ActorRef getPid() {
    return this.pid;
  }

  public File getFile() {
    return file;
  }
}
