package net.wrap_trap.bonten.level;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import net.wrap_trap.bonten.Bonten;
import net.wrap_trap.bonten.BontenActor;
import net.wrap_trap.bonten.KeyRange;
import net.wrap_trap.bonten.LevelReader;
import net.wrap_trap.bonten.NullActorRef;
import net.wrap_trap.bonten.Read;
import net.wrap_trap.bonten.Reader;
import net.wrap_trap.bonten.Utils;
import net.wrap_trap.bonten.entry.DeletedEntry;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.merger.Merger;
import net.wrap_trap.bonten.message.AwaitIncrementalMerge;
import net.wrap_trap.bonten.message.BeginIncrementalMerge;
import net.wrap_trap.bonten.message.BottomLevel;
import net.wrap_trap.bonten.message.Close;
import net.wrap_trap.bonten.message.Destroy;
import net.wrap_trap.bonten.message.InitSnapshotRangeFold;
import net.wrap_trap.bonten.message.InitBlockingRangeFold;
import net.wrap_trap.bonten.message.Inject;
import net.wrap_trap.bonten.message.LevelQuery;
import net.wrap_trap.bonten.message.Lookup;
import net.wrap_trap.bonten.message.MergeDone;
import net.wrap_trap.bonten.message.RangeFoldDone;
import net.wrap_trap.bonten.message.SetMaxLevel;
import net.wrap_trap.bonten.message.RangeFoldStart;
import net.wrap_trap.bonten.message.Step;
import net.wrap_trap.bonten.message.StepDone;
import net.wrap_trap.bonten.message.StepLevel;
import net.wrap_trap.bonten.message.StepOk;
import net.wrap_trap.bonten.message.UnmergedCount;
import net.wrap_trap.bonten.range.RangeFolder;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;

public class Level extends BontenActor {
  
  private String dirPath;
  private int level;
  private ActorRef next;
  private ActorRef owner;
  
  private LevelReader aReader;
  private LevelReader bReader;
  private LevelReader cReader;
  private int maxLevel;
  private ActorRef mergePid;
  private List<ActorRef> folding;
  
  private ActorRef stepMergeRef;
  private ActorRef stepNextRef;
  private ActorRef stepCaller;
  
  private int workDone;
  private int workInProgress;
  
  private ActorRef injectDoneRef;
  
  public Level(String dirPath, int level, ActorRef next, ActorRef owner) throws IOException {
    this.dirPath = dirPath;
    this.level = level;
    this.next = next;
    this.owner = owner;
    open();
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
      this.aReader = new LevelReader(aFile.getAbsolutePath());
      this.aReader.open();      
      if(cFile.exists()) {
        FileUtils.moveDirectory(cFile, bFile);
        this.bReader = new LevelReader(bFile.getAbsolutePath());
        this.bReader.open();
        checkBeginMergeThenLoop0();
      } 
    } else {
      if(bFile.exists()) {
        this.aReader = new LevelReader(aFile.getAbsolutePath());
        this.aReader.open();
        this.bReader = new LevelReader(bFile.getAbsolutePath());
        this.bReader.open();
        if(cFile.exists()) {
          this.cReader = new LevelReader(cFile.getAbsolutePath());
          this.cReader.open();
        }
        checkBeginMergeThenLoop0();
      } else {
        if(cFile.exists()) {
          throw new IllegalStateException("Invalid data files. bFile does not exist and cFile exists.");
        }
        if(aFile.exists()) {
          this.aReader = new LevelReader(aFile.getAbsolutePath());
          this.aReader.open();
        }
      }
    }
  }
  
  protected void checkBeginMergeThenLoop0() {
    if((this.aReader != null) && (this.bReader != null) && (this.mergePid == null)) {
      this.mergePid = beginMerge();
      // monitor
      this.stepMergeRef = this.getContext().watch(this.mergePid);
      int bTreeSize = Utils.getBtreeSize(this.level);
      this.workInProgress = (this.cReader == null)? bTreeSize : 2 * bTreeSize;
      this.mergePid.tell(new Step(this.stepMergeRef, this.workInProgress), this.getSelf());
      this.workDone = 0;
    }
  }
  
  protected void checkBeginMergeThenLoop() {
    if((this.aReader != null) && (this.bReader != null) && (this.mergePid == null)) {
      this.mergePid = beginMerge();
      this.workDone = 0;
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
    return Paths.get(this.dirPath, getFileName(prefix)).toFile();
  }
  
  protected String getFileName(String prefix) {
    return prefix + "-" + String.valueOf(this.level) + ".data";
  }

  public int unmergedCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void onReceive(Object message) throws Exception {
    if(message instanceof Lookup) {
      Entry entry = doLookup((Lookup)message);
      getContext().sender().tell(entry, getSelf());
      return;
    }
    if(message instanceof Inject) {
      if(cReader == null) {  
        doInject((Inject)message);
        getContext().sender().tell(true, getSelf());
        return;
      }
    }
    if(message instanceof UnmergedCount) {
      getContext().sender().tell(totalUnmerged(), getSelf());
      return;
    }
    if(message instanceof SetMaxLevel) {
      setMaxLevel((SetMaxLevel)message);
      return;
    }
    if(message instanceof BeginIncrementalMerge) {
      BeginIncrementalMerge biMerge = (BeginIncrementalMerge)message;
      if((this.stepMergeRef == null) && (this.stepNextRef == null)) {
        getContext().sender().tell(true, getSelf());
        doStep(null, 0, biMerge.getStepSize());
        return;
      }
    }
    if(message instanceof AwaitIncrementalMerge) {
      if(stepMergeRef == null && stepNextRef == null) {
        getContext().sender().tell(true, getSelf());
        return;
      }
    }
    if(message instanceof LevelQuery) {
      getContext().sender().tell(this.level, getSelf());
      return;
    }
    if(message instanceof StepDone) {
      ActorRef mRef = ((StepDone)message).getMRef();
      if(mRef.equals(this.stepMergeRef)) {        
        getContext().unwatch(mRef);
        this.workDone = this.workDone + this.workInProgress;
        this.workInProgress = 0;
        if(this.stepNextRef == null) {
          replyStepOk();
        }
        this.stepMergeRef = null;
        return;
      } else {
        throw new IllegalStateException("Unexpected step_done");
      }
    }
    if(message instanceof Terminated) {
      Terminated terminated = (Terminated)message;
      if(terminated.getActor().equals(this.stepMergeRef)) {
        if(this.stepNextRef == null) {
          replyStepOk();
        }
        this.stepMergeRef = null;
        this.workInProgress = 0;
      }
    }
    if(message instanceof StepOk) {
      if(getContext().sender().equals(this.stepNextRef) && (this.stepMergeRef == null)) {
        replyStepOk();
        getContext().unwatch(this.stepNextRef);
        this.stepNextRef = null;
      }
    }
    if(message instanceof Close) {
      closeIfDefined(this.aReader);
      closeIfDefined(this.bReader);
      closeIfDefined(this.cReader);
      
      stopIfDefined(this.mergePid);
      if(this.folding != null) {
        this.folding.stream().forEach(f -> stopIfDefined(f));
      }
      if(this.next != null) {
        close(next);
      }
      getContext().sender().tell(true, getSelf());
    }
    if(message instanceof Destroy) {
      destroyIfDefined(this.aReader);
      destroyIfDefined(this.bReader);
      destroyIfDefined(this.cReader);

      stopIfDefined(this.mergePid);
      if(this.folding != null) {
        this.folding.stream().forEach(f -> stopIfDefined(f));
      }
      
      if(this.next != null) {
        destroy(next);
      }
      getContext().sender().tell(true, getSelf());
    }
    if(message instanceof InitSnapshotRangeFold) {
      if((this.folding == null) || (folding.size() == 0)) {
        InitSnapshotRangeFold isRangeFold = (InitSnapshotRangeFold)message;
        List<ActorRef> nextList = isRangeFold.getList();
        List<ActorRef> foldingsPids = new ArrayList<>();
        
        if(this.aReader != null) {
          ActorRef ref = startRangeFold("AF", "A", isRangeFold);
          nextList.add(ref);
          foldingsPids.add(ref);
        }
                
        if(this.bReader != null) {          
          ActorRef refB = startRangeFold("BF", "B", isRangeFold);
          nextList.add(refB);
          foldingsPids.add(refB);
        }
        
        if(this.cReader != null) {
          ActorRef refC = startRangeFold("CF", "C", isRangeFold);
          nextList.add(refC);
          foldingsPids.add(refC); 
        }
        
        if(this.next == null) {
          getContext().sender().tell(nextList, getSelf());
        } else {
          next.tell(isRangeFold, getSelf());
        }
        this.folding = foldingsPids;
      }     
    }
    if(message instanceof RangeFoldDone) {
      RangeFoldDone rangeFoldDone = (RangeFoldDone)message;
      deleteFile(rangeFoldDone.getFile());
      this.folding.remove(rangeFoldDone.getPid());
    }
    if(message instanceof InitBlockingRangeFold) {
      InitBlockingRangeFold ibRangeFold = (InitBlockingRangeFold)message;
      if(this.aReader != null) {
        startRangeFold2(this.aReader, ibRangeFold);
      }
      if(this.bReader != null) {
        startRangeFold2(this.bReader, ibRangeFold);
      }
      if(this.cReader != null) {
        startRangeFold2(this.cReader, ibRangeFold);
      }
      if(this.next == null) {
        getContext().sender().tell(ibRangeFold.getList(), getSelf());
      } else {
        next.tell(ibRangeFold, getSelf());
      }
    }
    if(message instanceof MergeDone) {
      MergeDone mergeDone = (MergeDone)message;
      if(mergeDone.getCount() == 0) {
        deleteFile(mergeDone.getOutFileName());
        closeAndDeleteAandB();
        if(this.cReader == null) {
          this.mergePid = null;
        } else {
          this.cReader.close();
          File aFile = getFile("A");
          getFile("C").renameTo(aFile);
          LevelReader levelReader = new LevelReader(aFile.getAbsolutePath());
          levelReader.open();
          this.aReader = levelReader;
        }
      } else if(
          (mergeDone.getCount() < Utils.getBtreeSize(this.level)) 
          && (this.cReader == null)
          && (this.next == null)) {
        File mFile = getFile("M");
        new File(mergeDone.getOutFileName()).renameTo(mFile);
        closeAndDeleteAandB();
        
        File aFile = getFile("A");
        mFile.renameTo(aFile);
        LevelReader aLevelReader = new LevelReader(aFile.getAbsolutePath());
        aLevelReader.open();

        this.aReader = aLevelReader;
        if(this.cReader != null) {
          cReader.close();
          File bFile = getFile("B");
          getFile("C").renameTo(bFile);
          LevelReader bLevelReader = new LevelReader(aFile.getAbsolutePath());
          bLevelReader.open();
          this.bReader = bLevelReader;
          this.cReader = null;
          this.mergePid = null;
          this.checkBeginMergeThenLoop();
        }
      } else {
        if(next == null) {
          ActorRef actorRef = getContext().actorOf(
            Props.create(
              Level.class, 
              this.dirPath, 
              this.level + 1, 
              this.next, 
              this.owner
            )
          );
          this.owner.tell(new BottomLevel(this.level + 1), getSelf());
          this.next = actorRef;
          this.maxLevel = this.level + 1;
        }
        this.injectDoneRef = sendCall(this.next, new Inject(mergeDone.getOutFileName()));
        this.mergePid = null;
      }
    }
    // ここから再開
    // REPLY?(MRef, ok) when...when
  }

  protected void closeAndDeleteAandB() throws IOException {
    this.aReader.close();
    this.bReader.close();
    deleteFile(getFile("A"));
    deleteFile(getFile("B"));
  }
  
  protected void startRangeFold2(LevelReader levelReader, InitBlockingRangeFold ibRangeFold) throws IOException {
    ActorRef ref = new NullActorRef();
    RangeFolder rangeFolder = new RangeFolder();
    rangeFolder.doRangeFold2(levelReader, ibRangeFold.getWorkerPid(), ref, ibRangeFold.getRange());
    ibRangeFold.getList().add(ref);
  }

  protected ActorRef startRangeFold(String fFileName, String fileName, InitSnapshotRangeFold isRangeFold) throws IOException {
    File f = getFile(fFileName);
    Files.createLink(f.toPath(), getFile(fileName).toPath());
    return startRangeFold(f, isRangeFold.getWorkerPid(), isRangeFold.getRange());
  }
    
  protected ActorRef startRangeFold(File file, ActorRef workerPid, KeyRange keyRange) {
    ActorRef actorRef = Bonten.actorSystem.actorOf(Props.create(RangeFolder.class));
    actorRef.tell(new RangeFoldStart(file, workerPid, keyRange), getSender());
    return actorRef;
  }
  
  protected void destroy(ActorRef ref) {
    ref.tell(new Destroy(), getSelf());
  }
  
  protected void destroyIfDefined(LevelReader levelReader) throws IOException {
    if(levelReader == null) {
      return;
    }
    levelReader.destroy();
  }
  
  protected void close(ActorRef ref) {
    ref.tell(new Close(), getSelf());
  }
  
  protected void stopIfDefined(ActorRef ref) {
    if(ref == null) {
      return;
    }
    getContext().stop(ref);
  }
  
  protected void closeIfDefined(LevelReader levelReader) throws IOException {
    if(levelReader == null) {
      return;
    }
    levelReader.close();
  }
  
  protected void doStep(ActorRef stepFrom, int prevWork, int stepSize) throws Exception {
    int workLeftHere = 0;
    if(this.bReader != null && this.mergePid != null) {
      workLeftHere = Math.max(0, (2 * Utils.getBtreeSize(this.level)) - this.workDone);
    }
    int workUnit = stepSize;
    int maxLevel = Math.max(this.maxLevel, this.level);
    int depth = maxLevel - Bonten.TOP_LEVEL + 1;
    int totalWork = depth * workUnit;
    int workUnitsLeft = Math.max(0, totalWork - prevWork);

    String mergeStrategy = Bonten.getConfig().getString("merge_strategy", "fast");
    int workToDoHere = 0;
    switch(mergeStrategy) {
      case "fast":
        workToDoHere = Math.min(workLeftHere, workUnitsLeft);
        break;
      case "predictable":
        if(workLeftHere < (depth * workUnit)) {
          workToDoHere = Math.min(workLeftHere, workUnit);
        } else {
          workToDoHere = Math.min(workLeftHere, workUnitsLeft);
        }
        break;
      default:
        throw new IllegalArgumentException("Unexpected merge_strategy: " + mergeStrategy);
    }
    
    int workIncludingHere = prevWork + workToDoHere;
    
    ActorRef newDelegateRef = null;
    if(this.next != null) {
      Future<Object> future = Patterns.ask(next, new StepLevel(workIncludingHere, stepSize), Bonten.ASK_TIMEOUT);
      newDelegateRef = (ActorRef)Await.result(future, Bonten.ASK_TIMEOUT.duration());
    }
    ActorRef newMergeRef = null;
    if(workToDoHere > 0) {
      newMergeRef = this.getContext().watch(this.mergePid);
      this.mergePid.tell(new Step(newMergeRef, workToDoHere), getSelf());
    }
    if((newDelegateRef == null) && (newMergeRef == null)) {
      replyStepOk();
    } else {
      this.stepNextRef = newDelegateRef;
      this.stepCaller = stepFrom;
      this.stepMergeRef = newMergeRef;
      this.workInProgress = workToDoHere;
    }
  }
  
  protected void replyStepOk() {
    if(this.stepCaller != null) {
      this.stepCaller.tell(new StepOk(), getSender());
    }
    this.stepCaller = null;
  }
  
  protected void setMaxLevel(SetMaxLevel setMaxLevel) {
    int max = setMaxLevel.getMax();
    if(this.next != null) {
      next.tell(max, getSelf());
    }
    this.maxLevel = max;
  }
  
  protected int totalUnmerged() {
    int files = (this.bReader == null)? 0 : 2;
    return files * Utils.getBtreeSize(this.level);
  }

  protected void doInject(Inject inject) throws IOException {
    File from = new File(inject.getFilePath());
    if((aReader == null) && (bReader == null)) {
      this.aReader = createLevelReader(from, getFile("A"));
    } else if(bReader == null) {
      this.bReader = createLevelReader(from, getFile("B"));
      checkBeginMergeThenLoop();
    } else {
      this.cReader = createLevelReader(from, getFile("C"));
    }
  }
  
  protected LevelReader createLevelReader(File from, File to) throws IOException {
    from.renameTo(to);
    LevelReader levelReader = new LevelReader(to);
    levelReader.open();
    return levelReader;
  }

  protected Entry doLookup(Lookup lookup) throws IOException {
    for(LevelReader r : Arrays.asList(this.cReader, this.bReader, this.aReader)) {      
      Entry entry = r.lookup(lookup.getKey());
      if(entry != null) {
        if(entry instanceof DeletedEntry) {
          return null;
        }
        return entry;
      }
    }
    return null;
  }
  
  protected void deleteFile(String fileName) {
    deleteFile(new File(fileName));
  }
  
  protected void deleteFile(File file) {
    if(!file.delete()) {
      throw new IllegalStateException("Failed to delete file: " + file.getAbsolutePath());
    }
  }
}
