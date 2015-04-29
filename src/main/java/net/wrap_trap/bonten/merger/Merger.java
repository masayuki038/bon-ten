package net.wrap_trap.bonten.merger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wrap_trap.bonten.Read;
import net.wrap_trap.bonten.Reader;
import net.wrap_trap.bonten.Writer;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.message.Message;
import net.wrap_trap.bonten.message.Step;
import net.wrap_trap.bonten.message.StepDone;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Merger extends UntypedActor {
  
  private File aFile;
  private File bFile;
  private File xFile;
  private int bTreeSize;
  private boolean isLastLevel;
  
  public Merger(ActorRef from, File aFile, File bFile, File xFile, int bTreeSize, boolean isLastLevel) throws IOException {
    this.aFile = aFile;
    this.bFile = bFile;
    this.xFile = xFile;
    this.bTreeSize = bTreeSize;
    this.isLastLevel = isLastLevel;
    
    int outCount = merge();
    from.tell(outCount, from);
  }

  protected void merge() throws IOException {
    Reader aReader = new Reader(this.aFile.getAbsolutePath());
    aReader.open(Read.SEQUENTIAL);

    Reader bReader = new Reader(this.bFile.getAbsolutePath());
    bReader.open(Read.SEQUENTIAL);
    
    Map<String, Object> options = new HashMap<>();
    options.put("size", this.bTreeSize);
    Writer xWriter = new Writer(xFile.getAbsolutePath());
    xWriter.open(options);
    
    List<Entry> aEntryList = aReader.getFirstNode();
    List<Entry> bEntryList = bReader.getFirstNode();
    
    scan(aEntryList, bEntryList, 0, null);
  }
  
  protected void scan(List<Entry> aEntryList, List<Entry> bEntryList, int n, ActorRef fromPid, ) {
    if((n < 1) && (aEntryList.size() > 0) && (bEntryList.size() > 0)) {
      if(fromPid != null) {
        fromPid.tell(new StepDone(), refs); // refs->mergerのPIDをmonitorしている
      } 
    }
  }
  
  @Override
  public void onReceive(Object object) throws Exception {
    if(!(object instanceof Message)) {
      throw new IllegalArgumentException("Expected Message, but not");
    }

    if(object instanceof Step) {
      scan()
    }
  }
}
