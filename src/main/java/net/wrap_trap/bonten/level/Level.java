package net.wrap_trap.bonten.level;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import net.wrap_trap.bonten.Read;
import net.wrap_trap.bonten.Reader;
import net.wrap_trap.bonten.Utils;
import net.wrap_trap.bonten.merger.Merger;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Level extends UntypedActor {
  
  private String dirPath;
  private int level;
  private ActorRef next;
  
  private Reader aReader;
  private Reader bReader;
  private Reader cReader;
  private ActorRef merger;
  
  public Level(String dirPath, int level, ActorRef next) throws IOException {
    this.dirPath = dirPath;
    this.level = level;
    this.next = next;
  }
  
  protected void open() throws IOException {
    Utils.ensureExpiry();
    File aFile = getFile("A");
    File bFile = getFile("B");
    File cFile = getFile("C");
    File mFile = getFile("M");
    
    FileUtils.deleteQuietly(getFile("X"));
    
    FileUtils.deleteQuietly(getFile("AF"));
    FileUtils.deleteQuietly(getFile("BF"));
    FileUtils.deleteQuietly(getFile("CF"));
    
    if(mFile.exists()) {
      FileUtils.deleteQuietly(aFile);
      FileUtils.deleteQuietly(bFile);
      FileUtils.moveFile(mFile, aFile);
      this.aReader = new Reader(aFile.getAbsolutePath());
      this.aReader.open(Read.RANDOM);      
      if(cFile.exists()) {
        FileUtils.moveDirectory(cFile, bFile);
        this.bReader = new Reader(bFile.getAbsolutePath());
        this.bReader.open(Read.RANDOM);
        checkBeginMergeThenLoop0();
      } 
    } else {
      if(bFile.exists()) {
        this.aReader = new Reader(aFile.getAbsolutePath());
        this.aReader.open(Read.RANDOM);
        this.bReader = new Reader(bFile.getAbsolutePath());
        this.bReader.open(Read.RANDOM);
        if(cFile.exists()) {
          this.cReader = new Reader(cFile.getAbsolutePath());
          this.cReader.open(Read.RANDOM);
        }
        checkBeginMergeThenLoop0();
      } else {
        if(cFile.exists()) {
          throw new IllegalStateException("Invalid data files. bFile does not exist and cFile exists.");
        }
        if(aFile.exists()) {
          this.aReader = new Reader(aFile.getAbsolutePath());
          this.aReader.open(Read.RANDOM);
        }
      }
    }
  }
  
  protected void checkBeginMergeThenLoop0() {
    if((this.aReader != null) && (this.bReader != null) && (this.merger == null)) {
      this.merger = beginMerge();
      // monitor
      int bTreeSize = Utils.getBtreeSize(this.level);
      int wip = (this.cReader == null)? bTreeSize : 2 * bTreeSize;
      
    }
  }

  protected ActorRef beginMerge() {
    File aFile = getFile("A");
    File bFile = getFile("B");
    File xFile = getFile("X");
    FileUtils.deleteQuietly(xFile);
    int bTreeSize = Utils.getBtreeSize(this.level + 1);
    return createMerger(getSelf(), aFile, bFile, xFile, bTreeSize, (next != null));
  }
  
  public ActorRef createMerger(ActorRef from, File aFile, File bFile, File xFile, int bTreeSize, boolean isLastLevel) {
    return getContext().actorOf(Props.create(Merger.class, from, aFile, bFile, xFile, bTreeSize, isLastLevel));
  }

  protected File getFile(String prefix) {
    return Paths.get(this.dirPath, prefix + "-" + String.valueOf(this.level) + ".data").toFile();
  }

  public int unmergedCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void onReceive(Object arg0) throws Exception {
    // TODO Auto-generated method stub
    
  }
}
