package net.wrap_trap.bonten;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.wrap_trap.bonten.entry.Entry;
import net.wrap_trap.bonten.entry.PosLenEntry;

import com.google.common.collect.Lists;

public class Writer {

  private static final String FILE_FORMAT = "HAN2";

  private String dataFilePath;
  private OutputStream fileWriter;
  private Bloom bloom;
  private int blockSize;
  private Map<String, Object> options;
  private String compress;
  private long indexFilePos;
  private int valueCount;
  private int tombstoneCount;
  private long lastNodePos;
  private long lastNodeSize;
  private List<Node> nodeList;

  public Writer(final String dataFilePath) {
    this.dataFilePath = dataFilePath;
    this.nodeList = new ArrayList<>();
    this.valueCount = 0;
    this.tombstoneCount = 0;
  }

  public void open(final Map<String, Object> options) throws IOException {
    final BontenConfig config = Bonten.getConfig();

    int size;
    if (options != null && options.containsKey("size")) {
      size = (int) options.get("size");
    } else {
      size = config.getInt("size", 2048);
    }

    this.bloom = new Bloom(size);

    final int writeBufferSize = config.getInt("write_buffer_size", 512 * 1024);
    this.fileWriter = new BufferedOutputStream(new FileOutputStream(this.dataFilePath), writeBufferSize);
    final byte[] fileFormat = Utils.toBytes(FILE_FORMAT);
    this.fileWriter.write(fileFormat);

    this.blockSize = config.getInt("block_size", 8192);
    this.compress = config.getString("compress", null);
    this.options = options;
    this.indexFilePos = fileFormat.length;
  }

  public void add(final Entry entry) throws IOException {
    if (entry.expired())
      return;
    appendNode(0, entry);
  }

  protected void appendNode(final int level, final Entry entry) throws IOException {
    if (this.nodeList.isEmpty()) {
      this.nodeList.add(new Node(level));
    }
    final Node tmp = this.nodeList.get(0);
    if (level < tmp.getLevel()) {
      this.nodeList.remove(0);
      this.nodeList.add(0, new Node(tmp.getLevel() - 1));
    }
    final Node node = this.nodeList.get(0);
    final List<Entry> entryList = node.getEntryList();
    if (!entryList.isEmpty()) {
      final Entry entryInNode = entryList.get(0);
      if (Utils.compareBytes(entry.getKey(), entryInNode.getKey()) < 0)
        throw new IllegalStateException("entry.getKey() < entryInNode.getKey()");
    }
    final int newSize = node.getSize() + entry.estimateNodeSizeIncrement();
    this.bloom.add(entry.getKey());
    if (level == 0) {
      if (entry.isTombstone()) {
        this.tombstoneCount++;
      } else {
        this.valueCount++;
      }
    }
    node.addEntry(entry);
    node.setSize(newSize);

    if (newSize >= this.blockSize) {
      final PosLenEntry posLenEntry = flushNodeBuffer(level);
      this.appendNode(level + 1, posLenEntry);
    }
  }

  protected PosLenEntry flushNodeBuffer(final int level) throws IOException {
    final Node node = this.nodeList.get(0);
    final List<Entry> orderedMembers = Lists.reverse(node.getEntryList());
    final byte[] blockData = Utils.encodeIndexNode(orderedMembers, this.compress);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (BontenOutputStream bos = new BontenOutputStream(baos)) {
      bos.writeUnsignedInt(blockData.length + 2);
      bos.writeUnsignedShort(level);
      bos.write(blockData);
      this.fileWriter.write(baos.toByteArray());
    }
    final Entry entry = orderedMembers.get(0);
    final long dataSize = blockData.length + 6;
    final PosLenEntry posLenEntry = new PosLenEntry(entry.getKey(), BigInteger.valueOf(this.indexFilePos), dataSize);
    this.nodeList = this.nodeList.subList(1, this.nodeList.size());
    this.lastNodePos = this.indexFilePos;
    this.lastNodeSize = dataSize;
    this.indexFilePos += dataSize;
    return posLenEntry;
  }

}
