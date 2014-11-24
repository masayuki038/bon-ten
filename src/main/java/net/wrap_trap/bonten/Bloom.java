package net.wrap_trap.bonten;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class Bloom {
	private static final Logger logger = LoggerFactory.getLogger(Bloom.class);

	private double e;
	private int n;
	private int mb;
	private List<BitSet> a;
	
	public Bloom(int size) {
		this(size, 0.001);
	}
	
	public Bloom(int n, double e) {
		if(n <= 0) {
			throw new IllegalArgumentException("n should be grater than 0.");
		}
		
		if(e <= 0.0 || e > 1.0) {
			throw new IllegalArgumentException("e should be 0.0 < e < 1.0");
		}
		
		if(n >= 4/e) {
			logger.info("selected BloomMode.size");
			prepare(BloomMode.size, n, e);
		} else {
			logger.info("selected BloomMode.bit");
			prepare(BloomMode.bit, n, e);
		}
	}

	public void add(byte[] key) {
		int hashes = makeHashes(key);
		hashAdd(hashes);
	}
	
	public boolean member(byte[] key) {
		int hashes = makeHashes(key);
		return hashMember(hashes);
	}
	
	protected void prepare(BloomMode mode, int n1, double e1) {
		this.e = e1;
		this.n = n1;
		
		int k;
		switch(mode) {
			case size:
				k = 1 + new Double(Math.floor(Utils.log2(1/this.e))).intValue();
				break;
			case bit:
				k = 1;
				break;
			default:
				throw new IllegalArgumentException("BloomMode should be size or bit.");
		}

		double p = Math.pow(this.e, 1 / k);
		
		switch(mode) {
		case size:
			this.mb = 1 + new Double(-1 * Math.floor(Utils.log2(1 - Math.pow(1 - p, 1 / this.n)))).intValue();
			break;
		case bit:
			this.mb = this.n;
			break;
		default:
			throw new IllegalArgumentException("BloomMode should be size or bit.");
		}
		
		int m = 1 << this.mb;
		this.n = new Double(Math.floor(Math.log(1 - p) / Math.log(1 - 1 / m))).intValue();
		
		this.a = new ArrayList<>();
		for(int i = 1; i <= k; i++) {
			this.a.add(new BitSet());
		}
		
		logger.info(String.format("mb: %d, n: %d, k: %d", mb, n, k));
	}
		
	protected void hashAdd(int hashes) {
		int mask = (1 << this.mb) - 1;
		List<Integer> indexes = makeIndexes(mask, hashes);
		setBits(mask, indexes.get(0), indexes.get(1));
	}
	
	protected boolean hashMember(int hashes) {
		int mask = (1 << this.mb) - 1;
		List<Integer> indexes = makeIndexes(mask, hashes);
		return allSet(mask, indexes.get(0), indexes.get(1));
	}
	
	protected boolean allSet(int mask, int i1, int i2) {
		logger.debug(String.format("allSet mask: %d, i1: %d, i2: %d", mask, i1, i2));
		int i = i2;
		for(BitSet bitmap : this.a) {
			if(!bitmap.get(i)) {
				return false;
			}
			i = (i + i1) & mask;
		}
		return true;
	}

	protected void setBits(int mask, int i1, int i2) {
		logger.debug(String.format("setBit mask: %d, i1: %d, i2: %d", mask, i1, i2));
		int i = i2;
		for(BitSet bitmap : this.a) {
			bitmap.set(i);
			logger.debug("bitmap.set(i) i: " + i);
			i = (i + i1) & mask;
		}
	}
	
	protected List<Integer> makeIndexes(int mask, int hashes) {
		return ImmutableList.of(((hashes >> 16) & mask), (hashes & mask));
	}
	
	protected int makeHashes(byte[] key) {
		int hashCode = Arrays.hashCode(key);
		logger.debug(String.format("hashCode: %d", hashCode));
		return hashCode;
	}
	
	enum BloomMode {
		size, bit
	}
}
