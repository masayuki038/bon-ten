package net.wrap_trap.bonten;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

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
	
	public static int getBtreeSize(int level) {
		return level << 1;
	}
	
	public static int getIncrementalMergeStep() {
		return getBtreeSize(Bonten.TOP_LEVEL);
	}
	
	public static Nursery newNursery(String dirPath, int maxLevel) throws IOException {
		return newNursery(dirPath, maxLevel, new TreeMap<byte[], Entry>());
	}

	public static Nursery newNursery(String dirPath, int maxLevel, Map<byte[], Entry> tree) throws IOException {
		Nursery nursery = new Nursery(dirPath, maxLevel, tree);
		nursery.init();
		return nursery;
	}

	public static Nursery recover(String dirPath, int topLevel, int maxLevel) throws IOException {
		File logFile = new File(getNurseryLogFilePath(dirPath));
		if(logFile.exists()) {
			doRecover(logFile, topLevel, maxLevel);
		}
		return newNursery(dirPath, maxLevel);
	}

	private static String getNurseryLogFilePath(String dirPath) {
		return dirPath + java.io.File.pathSeparator + LOG_FILENAME;
	}
	
	private static String getNurseryDataFilePath(String dirPath) {
		return dirPath + java.io.File.pathSeparator + DATA_FILENAME;
	}

	private static void doRecover(File logFile, int topLevel, int maxLevel) throws IOException {
		Nursery nursery = readNurseryFromLog(logFile, maxLevel);
		nursery.finish();
		if(logFile.exists()) {
			throw new IllegalStateException("nursery.log exists.");
		}
	}
	
	private static Nursery readNurseryFromLog(File logFile, int maxLevel) throws IOException {
		Map<byte[], Entry> entryMap = new TreeMap<>();
		try(BontenInputStream bis = new BontenInputStream(new FileInputStream(logFile))) {
			while(true) {
				Entry entry = deserializeEntry(bis);
				entryMap.put(entry.getKey(), entry);
			}
		} catch(EOFException | InvalidCrcException ignore){}
		
		return newNursery(logFile.getParent(), maxLevel, entryMap);
	}
	
	private static Entry deserializeEntry(BontenInputStream bis) throws IOException, InvalidCrcException {
		long size = bis.readUnsignedInt();
		long crc = bis.readUnsignedInt();
		byte[] body = bis.read(size);
		bis.readEndTag();
		
		if(!Utils.checkCRC(crc, body)) {
			throw new InvalidCrcException();
		}
		
		if(body.length < 1) {
			throw new IllegalStateException("Empty body");
		}
		byte tag = body[0];
		
		return DeserializerFactory.getDeserializer(tag).deserialize(body);
	}

	public Nursery(String dirPath, int maxLevel, Map<byte[], Entry> tree) {
		this.dirPath = dirPath;
		this.maxLevel = maxLevel;
		this.tree = tree;		
	}
	
	public void init() throws IOException {
		String logFilename = getNurseryLogFilePath(dirPath);
		logWriter = new java.io.FileWriter(logFilename);
	}
	
	protected void finish() {
		if(logWriter != null) {
			try {
				logWriter.close();
			} catch(IOException ignore) {}
		} 
		
		String nurseryDataFilePath = getNurseryDataFilePath(dirPath);
		Writer writer = new Writer(nurseryDataFilePath);
		
	}
}
