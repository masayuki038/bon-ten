package net.wrap_trap.bonten;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import net.wrap_trap.bonten.level.Level;

public class Bonten extends UntypedActor {

  public static final int TOP_LEVEL = 8;

  public static final int SIZE_OF_ENTRY_TYPE = 1;
  public static final int SIZE_OF_KEYSIZE = 4;
  public static final int SIZE_OF_TIMESTAMP = 4;
  public static final int SIZE_OF_POS = 8;
  public static final int SIZE_OF_LEN = 4;

  public static final byte TAG_KV_DATA = (byte) 0x80;
  public static final byte TAG_DELETED = (byte) 0x81;
  public static final byte TAG_POSLEN = (byte) 0x82;
  public static final byte TAG_TRANSACT = (byte) 0x83;
  public static final byte TAG_KV_DATA2 = (byte) 0x84;
  public static final byte TAG_DELETED2 = (byte) 0x85;
  
  public static final String FILE_FORMAT = "HAN2";
  public static final long FIRST_BLOCK_POS = Utils.toBytes(FILE_FORMAT).length;
  public static final byte[] dummyKey = new byte[]{};
  
  public static final Timeout ASK_TIMEOUT = new Timeout(Duration.create(5, "seconds"));;
  public static final int FOLD_CHUNK_SIZE = 100;
  
  private static final Pattern dataFilePattern = Pattern.compile("^[^\\d]+-(\\d+).data$");

  private static BontenConfig bontenConfig;

  public static ActorSystem actorSystem;
  
  private String dirPath;

  public static Bonten dbOpen(String dirPath) throws IOException {
    init();
    Bonten bonten = new Bonten(dirPath);
    bonten.open();
    return bonten;
  }
  
  public static void init() {
    bontenConfig = BontenConfig.loadConfig();
    Utils.ensureExpiry();
    actorSystem = ActorSystem.create("system");
  }

  public static BontenConfig getConfig() {
    return bontenConfig;
  }
  
  Bonten(String dirPath) throws IOException {
    this.dirPath = dirPath;
  }
  
  void open() throws IOException {
    File dir = new File(dirPath);
    if(dir.isDirectory()) {
      Levels level = openLevels(dirPath);
      Nursery nersery = Nursery.recover(this.dirPath, level.getTop(), level.getMax());
    }
  }
  
  private Levels openLevels(String dirPath) {
    Nursery.deleteNurseryDataFile(dirPath);

    File dir = new File(dirPath);
    Tuple<Integer, Integer> levels = getLevels(dir.list());
    IntStream.range(levels.e2, levels.e1)
      .map(i -> levels.e1 - i + levels.e2)
      .boxed()
      .reduce(new Tuple<ActorRef, Integer>(null, 0), 
              ((t, v) -> openLevel(dirPath, v, t)), (a, b) -> a);

    return null;
  }
  
  protected ActorRef createLevel(String dirPath, int level, ActorRef next) {
    return getContext().actorOf(Props.create(Level.class, dirPath, level, next, getSelf()));
  }
  
  private Tuple<ActorRef, Integer> openLevel(String dirPath, Integer v, Tuple<ActorRef, Integer> t) {
    try {
      ActorRef level = createLevel(dirPath, v, t.e1);
      Future<Object> future = Patterns.ask(level, "unmergedCount", -1L);
      Integer unmergedCount = (Integer)Await.result(future, Duration.create(5000, TimeUnit.MILLISECONDS));
      return new Tuple<>(level, t.e2 + unmergedCount);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
  
  protected Tuple<Integer, Integer> getLevels(final String[] dataFiles) {
    int topLevel = getConfig().getInt("top_level");
    return Arrays.stream(dataFiles)
      .map(dataFilePattern::matcher)
      .filter(Matcher::find)
      .map((matcher) -> matcher.group(1))
      .reduce(
        new Tuple<Integer, Integer>(topLevel, topLevel), 
        ((t, v) -> new Tuple<Integer, Integer>(Math.max(t.e1, Integer.valueOf(v)), Math.min(t.e2, Integer.valueOf(v)))),
        (a, b) -> a
      );
  }
  
  class Levels {
    private int top;
    private int max;
    private int min;

    public Levels(int top, int max, int min) {
      super();
      this.top = top;
      this.max = max;
      this.min = min;
    }
    
    protected int getTop() {
      return top;
    }

    protected int getMax() {
      return max;
    }

    protected int getMin() {
      return min;
    }
  }

  @Override
  public void onReceive(Object e) throws Exception {
  }
}
