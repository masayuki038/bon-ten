package net.wrap_trap.bonten.level;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import net.wrap_trap.bonten.Utils;

public class LevelImpl implements Level {
  
  private String dirPath;
  private int level;
  private Level next;
  
  public LevelImpl(String dirPath, int level, Level next) throws IOException {
    this.dirPath = dirPath;
    this.level = level;
    this.next = next;
    init();
  }
  
  protected void init() throws IOException {
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
  
    }

  }
  
  protected File getFile(String prefix) {
    return Paths.get(this.dirPath, prefix + "-" + String.valueOf(this.level) + ".data").toFile();
  }

  @Override
  public int unmergedCount() {
    // TODO Auto-generated method stub
    return 0;
  }
}
