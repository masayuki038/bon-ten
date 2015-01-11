package net.wrap_trap.bonten;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import net.wrap_trap.bonten.deserializer.DeserializerFactory;
import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.InvalidCrcException;

public class Nursery {

  private static final String LOG_FILENAME = "nursery.log";
  private static final String DATA_FILENAME = "nursery.data";

  private String dirPath;
  private java.io.Writer logWriter;
  private int maxLevel;
  private Map<byte[], Entry> tree;

  public static int getBtreeSize(final int level) {
    return level << 1;
  }

  public static int getIncrementalMergeStep() {
    return getBtreeSize(Bonten.TOP_LEVEL);
  }

  public static Nursery newNursery(final String dirPath, final int maxLevel) throws IOException {
    return newNursery(dirPath, maxLevel, new TreeMap<byte[], Entry>());
  }

  public static Nursery newNursery(final String dirPath, final int maxLevel, final Map<byte[], Entry> tree)
      throws IOException {
    final Nursery nursery = new Nursery(dirPath, maxLevel, tree);
    nursery.init();
    return nursery;
  }

  public static Nursery recover(final String dirPath, final int topLevel, final int maxLevel) throws IOException {
    final File logFile = new File(getNurseryLogFilePath(dirPath));
    if (logFile.exists()) {
      doRecover(logFile, topLevel, maxLevel);
    }
    return newNursery(dirPath, maxLevel);
  }

  private static String getNurseryLogFilePath(final String dirPath) {
    return dirPath + java.io.File.pathSeparator + LOG_FILENAME;
  }

  private static String getNurseryDataFilePath(final String dirPath) {
    return dirPath + java.io.File.pathSeparator + DATA_FILENAME;
  }

  private static void doRecover(final File logFile, final int topLevel, final int maxLevel) throws IOException {
    final Nursery nursery = readNurseryFromLog(logFile, maxLevel);
    nursery.finish();
    if (logFile.exists())
      throw new IllegalStateException("nursery.log exists.");
  }

  private static Nursery readNurseryFromLog(final File logFile, final int maxLevel) throws IOException {
    final Map<byte[], Entry> entryMap = new TreeMap<>();
    try (BontenInputStream bis = new BontenInputStream(new FileInputStream(logFile))) {
      while (true) {
        final Entry entry = deserializeEntry(bis);
        entryMap.put(entry.getKey(), entry);
      }
    } catch (EOFException | InvalidCrcException ignore) {}

    return newNursery(logFile.getParent(), maxLevel, entryMap);
  }

  private static Entry deserializeEntry(final BontenInputStream bis) throws IOException, InvalidCrcException {
    final int size = bis.readInt();
    final long crc = bis.readLong();
    final byte[] body = bis.read(size);
    bis.readEndTag();

    if (!Utils.checkCRC(crc, body))
      throw new InvalidCrcException();

    if (body.length < 1)
      throw new IllegalStateException("Empty body");
    
    return DeserializerFactory.getDeserializer(body[0]).deserialize(body);
  }
  
  public static void deleteNurseryDataFile(String dirPath) {
    String nurseryDataFilePath = getNurseryDataFilePath(dirPath);
    FileUtils.deleteQuietly(new File(nurseryDataFilePath));
  }

  public Nursery(final String dirPath, final int maxLevel, final Map<byte[], Entry> tree) {
    this.dirPath = dirPath;
    this.maxLevel = maxLevel;
    this.tree = tree;
  }

  public void init() throws IOException {
    final String logFilename = getNurseryLogFilePath(this.dirPath);
    this.logWriter = new java.io.FileWriter(logFilename);
  }

  protected void finish() {
    if (this.logWriter != null) {
      try {
        this.logWriter.close();
      } catch (final IOException ignore) {}
    }

    final String nurseryDataFilePath = getNurseryDataFilePath(this.dirPath);
    final Writer writer = new Writer(nurseryDataFilePath);

  }
}
