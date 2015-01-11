package net.wrap_trap.bonten;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import net.wrap_trap.bonten.level.Level;

public class Bonten {

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

  private static Pattern dataFilePattern = Pattern.compile("^[^\\d]+-(\\d+).data$");

  private static BontenConfig bontenConfig;
  
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
      Levels level = openLevel(dirPath);
      Nursery nersery = Nursery.recover(this.dirPath, level.getTop(), level.getMax());
    }
  }
  
  private Levels openLevel(String dirPath) {
    Nursery.deleteNurseryDataFile(dirPath);

    File dir = new File(dirPath);
    Tuple<Integer, Integer> levels = getLevels(dir.list());
    IntStream.range(levels.e2, levels.e1)
      .map(i -> levels.e1 - i + levels.e2)
      .boxed()
      .reduce(new Tuple<Level, Integer>(null, 0), 
              ((t, v) -> {
                Level level = Level.open(dirPath, v, t.e1);
                int mergeWork = t.e2 + level.unmergedCount();
                return new Tuple<>(level, mergeWork);
              }), (a, b) -> a);

    return null;
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
}
