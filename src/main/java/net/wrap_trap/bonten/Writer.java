package net.wrap_trap.bonten;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.wrap_trap.bonten.entry.Entry;

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
	private List<Node> nodeList;
	
	public Writer(String dataFilePath) {
		this.dataFilePath = dataFilePath;
		this.nodeList = new ArrayList<>();
		this.valueCount = 0;
		this.tombstoneCount = 0;
	}

	public void open(Map<String, Object> options) throws IOException {
		BontenConfig config = Bonten.getConfig();
		
		int size;
		if(options != null && options.containsKey("size")) {
			size = (int)options.get("size");
		} else {
			size = config.getInt("size", 2048);
		}
	
		this.bloom = new Bloom(size);

		int writeBufferSize = config.getInt("write_buffer_size", 512 * 1024);
		this.fileWriter = new BufferedOutputStream(new FileOutputStream(dataFilePath), writeBufferSize);
		byte[] fileFormat = Utils.toBytes(FILE_FORMAT);
		this.fileWriter.write(fileFormat);
		
		this.blockSize = config.getInt("block_size", 8192);
		this.compress = config.getString("compress", null);
		this.options = options;
		this.indexFilePos = fileFormat.length;
	}
	
	public void add(Entry entry) {
		if(entry.expired()) {
			return;
		}
		appendNode(0, entry);
	}
	
	public void appendNode(int level, Entry entry) {
		if(nodeList.isEmpty()) {
			nodeList.add(new Node(level));
		}
		Node tmp = nodeList.get(0);
		if(level < tmp.getLevel() ) {
			nodeList.remove(0);
			nodeList.add(0, new Node(tmp.getLevel() - 1));
		}
		Node node = nodeList.get(0);
		List<Entry> entryList = node.getEntryList();
		if(!entryList.isEmpty()) {
			Entry entryInNode = entryList.get(0);
			if(Utils.compareBytes(entry.getKey(), entryInNode.getKey()) < 0) {
				throw new IllegalStateException("entry.getKey() < entryInNode.getKey()");
			}
		}
		int newSize = node.getSize() + entry.estimateNodeSizeIncrement();
		bloom.add(entry.getKey());
		if(level == 0) {
			if(entry.isTombstone()) {
				this.tombstoneCount++;
			} else {
				this.valueCount++;
			}
		}
		node.addEntry(entry);
		node.setSize(newSize);
		
		if(newSize >= this.blockSize) {
			flushNodeBuffer();
		}
	}
	
	protected void flushNodeBuffer() {

	}
	
}
