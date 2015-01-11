package net.wrap_trap.bonten;

import java.util.ArrayList;
import java.util.List;

import net.wrap_trap.bonten.entry.Entry;

public class Node {

  private int level;
  private List<Entry> entryList;
  private int size;

  public Node(final int level) {
    this.level = level;
    this.entryList = new ArrayList<>();
    this.size = 0;
  }

  public Node(final int level, final List<Entry> entryList) {
    this.level = level;
    this.entryList = entryList;
    this.size = 0;
  }

  public int getLevel() {
    return this.level;
  }

  public List<Entry> getEntryList() {
    return this.entryList;
  }

  public void addEntry(final Entry entry) {
    this.entryList.add(entry);
  }

  public int getSize() {
    return this.size;
  }

  public void setSize(final int size) {
    this.size = size;
  }
}
