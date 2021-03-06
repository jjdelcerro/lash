package com.nyeggen.lash.util;

public final class Hash {
	private Hash(){}
	
	public static final int hashSeed = 0xe17a1465;
	
	/**Return a number greater than i whose bottom n bits collide when hashed.*/
	public static final long findCollision(long i, long nBits){
		final long mask = (1L << nBits) - 1;
		final long targetHash = Hash.murmurHash(i) & mask;
		
		for(i = i+1; i<Long.MAX_VALUE; i++){
			if((Hash.murmurHash(i) & mask) == targetHash) return i;
		}
		return -1;
	}
	
	/**Utility to hash a single value*/
	public static final long murmurHash(long k){
		final long m = 0xc6a4a7935bd1e995L;
		final int r = 47;

		long h = (hashSeed & 0xffffffffl)^(8*m);
		
		k *= m;
		k ^= k >>> r;
		k *= m;
		h ^= k;
		h *= m;
		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		return h & 0x7fffffffffffffffL;
	}
	
	public static final long murmurHash(long[] data){
		final int length = data.length;
		final long m = 0xc6a4a7935bd1e995L;
		final int r = 47;
		
		long h = (hashSeed & 0xffffffffl)^(length*8*m);
		
		for(int i=0; i<length; i++){
			long k=data[i];
			k *= m;
			k ^= k >>> r;
			k *= m;
			h ^= k;
			h *= m;
		}
		
		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		return h & 0x7fffffffffffffffL;
	}
	
	public static final long murmurHash(byte[] data){
		return murmurHash(hashSeed, data);
	}
	
	/**Modified to always return positive number. Murmurhash2 with 64-bit output.*/
	public static final long murmurHash(long hashSeed, byte[] data){
		final int length = data.length;
		final long m = 0xc6a4a7935bd1e995L;
		final int r = 47;

		long h = (hashSeed & 0xffffffffl)^(length*m);

		int length8 = length/8;

		for (int i=0; i<length8; i++) {
			final int i8 = i*8;
			long k = ((long)data[i8+0]&0xff) +(((long)data[i8+1]&0xff)<<8)
					+(((long)data[i8+2]&0xff)<<16) +(((long)data[i8+3]&0xff)<<24)
					+(((long)data[i8+4]&0xff)<<32) +(((long)data[i8+5]&0xff)<<40)
					+(((long)data[i8+6]&0xff)<<48) +(((long)data[i8+7]&0xff)<<56);

			k *= m;
			k ^= k >>> r;
			k *= m;
			h ^= k;
			h *= m;
		}

		switch (length%8) {
			case 7: h ^= (long)(data[(length&~7)+6]&0xff) << 48;
			case 6: h ^= (long)(data[(length&~7)+5]&0xff) << 40;
			case 5: h ^= (long)(data[(length&~7)+4]&0xff) << 32;
			case 4: h ^= (long)(data[(length&~7)+3]&0xff) << 24;
			case 3: h ^= (long)(data[(length&~7)+2]&0xff) << 16;
			case 2: h ^= (long)(data[(length&~7)+1]&0xff) << 8;
			case 1: h ^= (long)(data[length&~7]&0xff);
			h *= m;
		};

		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		//Clear sign bit
		return h & 0x7fffffffffffffffL;
	}
}
