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
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;

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

  protected int merge() throws IOException {
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
    
    return scan(aEntryList, bEntryList, 0, null);
  }
  
  protected int scan(List<Entry> aEntryList, List<Entry> bEntryList, int n, Object pid) {
    if((n < 1) && (aEntryList.size() > 0) && (bEntryList.size() > 0)) {
      // TODO step done
    }
    return 0;
  }
  
  @Override
  public void onReceive(Object arg0) throws Exception {
    // TODO Auto-generated method stub
    
  }
}
