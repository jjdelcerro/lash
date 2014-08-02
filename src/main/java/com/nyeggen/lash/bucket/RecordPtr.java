package com.nyeggen.lash.bucket;

import com.nyeggen.lash.util.MMapper;

public class RecordPtr{
	public final long hash, dataPtr;
	public final int kLength, vLength;
	public static final RecordPtr DELETED = new RecordPtr(-1, -1, -1, -1);
	
	public RecordPtr(long hash, long dataPtr, int kLength, int vLength){
		this.hash = hash;
		this.dataPtr = dataPtr;
		this.kLength = kLength;
		this.vLength = vLength;
	}
	public RecordPtr(MMapper mapper, long pos) {
		hash = mapper.getLong(pos);
		dataPtr = mapper.getLong(pos + 8);
		kLength = mapper.getInt(pos + 16);
		vLength = mapper.getInt(pos + 20);
	}
	public static RecordPtr overwrite(MMapper mapper, long pos, long hash, long dataPtr, int kLength, int vLength){
		mapper.putLong(pos,  hash);
		mapper.putLong(pos+8, dataPtr);
		mapper.putInt(pos+16, kLength);
		mapper.putInt(pos+20, vLength);
		return new RecordPtr(hash, dataPtr, kLength, vLength);	
	}
	
	/**Returns true if the record at the given pos is writable.*/
	public static boolean writableAt(MMapper mapper, long pos){
		final long v = mapper.getLong(pos + 8);
		return v == 0 || v == -1;
	}
	
	public RecordPtr writeToPos(final long pos, final MMapper mapper){
		return overwrite(mapper, pos, hash, dataPtr, kLength, vLength);
	}
	
	public boolean matchesData(long hash, int kLength){
		return this.hash == hash && this.kLength == kLength;
	}
	public boolean isWritable(){ return dataPtr == 0 || dataPtr == -1; }
	public boolean isFree(){ return dataPtr == 0; }
	public boolean isDeleted(){ return dataPtr == -1; }
	public byte[] getKey(MMapper mapper){
		final byte[] out = new byte[kLength];
		mapper.getBytes(dataPtr, out);
		return out;
	}
	public byte[] getVal(MMapper mapper){
		final byte[] out = new byte[vLength];
		mapper.getBytes(dataPtr + kLength, out);
		return out;
	}
}