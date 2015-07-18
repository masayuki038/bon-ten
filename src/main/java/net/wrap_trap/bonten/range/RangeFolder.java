package net.wrap_trap.bonten.range;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import scala.concurrent.Await;
import net.wrap_trap.bonten.Bonten;
import net.wrap_trap.bonten.BontenException;
import net.wrap_trap.bonten.KeyRange;
import net.wrap_trap.bonten.LevelReader;
import net.wrap_trap.bonten.Tuple;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.KeyValueEntry;
import net.wrap_trap.bonten.message.LevelDone;
import net.wrap_trap.bonten.message.LevelLimit;
import net.wrap_trap.bonten.message.LevelResult;
import net.wrap_trap.bonten.message.RangeFoldDone;
import net.wrap_trap.bonten.message.RangeFoldStart;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;

public class RangeFolder extends UntypedActor {

  public void run(File file, ActorRef workerPid, KeyRange keyRange) throws IOException {
    getContext().watch(workerPid);
    LevelReader reader = new LevelReader(file);
    reader.open();
    doRangeFold2(reader, workerPid, getSelf(), keyRange);
    getContext().unwatch(workerPid);
    reader.close();
    getSender().tell(new RangeFoldDone(getSelf(), file), getSender());
  }
  
  public void doRangeFold2(LevelReader reader, ActorRef workerPid, ActorRef selfOrRef, KeyRange keyRange) throws IOException {
    RangeFoldChunkContainer container = new RangeFoldChunkContainer();
    RangeFolderFuncResult results = reader.rangeFold((Entry entry) -> {
      container.add(entry);
      if(container.getSize() == 0) {
        send(workerPid, selfOrRef, container.getList());
        container.reset();
      } else {
        container.decrement();
      }
    }, keyRange);
    
    switch(results.getType()) {
      case LIMIT:
        send(workerPid, selfOrRef, results.getResults());
        workerPid.tell(new LevelLimit(selfOrRef, results.getLastKey()), getSender());
        break;
      case DONE:
        send(workerPid, selfOrRef, results.getResults());
        workerPid.tell(new LevelDone(selfOrRef), getSender());
        break;
    }
  }

  private void send(ActorRef workerPid, ActorRef selfOrRef, List<Entry> entries) {
    try{
      Await.result(
        Patterns.ask(
          workerPid, 
          new LevelResult(selfOrRef, entries),
          Bonten.ASK_TIMEOUT
        ), 
        Bonten.ASK_TIMEOUT.duration()
      );
    } catch (Exception e) {
      throw new BontenException(e);
    }
  }
  
  @Override
  public void onReceive(Object message) throws Exception {
    if(message instanceof Terminated) {
      getContext().stop(getSelf());
    }
    if(message instanceof RangeFoldStart) {
      RangeFoldStart rangeFoldStart = (RangeFoldStart)message;
      run(rangeFoldStart.getFile(), rangeFoldStart.getWorkerPid(), rangeFoldStart.getKeyRange());
    }
  }

  class RangeFoldChunkContainer {
    private List<Entry> list;
    private int size;
    
    RangeFoldChunkContainer() {
      this.list = Lists.newArrayList();
      this.size = Bonten.FOLD_CHUNK_SIZE;
    }
    
    void add(Entry entry) {
      this.list.add(entry);
    }
    void decrement() {
      this.size --;
    }
    public List<Entry> getList() {
      return list;
    }
    public int getSize() {
      return size;
    }
    public void reset() {
      this.list = Lists.newArrayList();
      this.size = Bonten.FOLD_CHUNK_SIZE;      
    }
  }
}
