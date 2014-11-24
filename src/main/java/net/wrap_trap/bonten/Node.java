package net.wrap_trap.bonten;

import java.util.ArrayList;
import java.util.List;

import net.wrap_trap.bonten.entry.Entry;

public class Node {

	private int level;
	private List<Entry> entryList;
	private int size;
	
	public Node(int level) {
		this.level = level;
		this.entryList = new ArrayList<>();
		this.size = 0;
	}

	public int getLevel() {
		return level;
	}

	public List<Entry> getEntryList() {
		return entryList;
	}

	public int getSize() {
		return size;
	}
}
