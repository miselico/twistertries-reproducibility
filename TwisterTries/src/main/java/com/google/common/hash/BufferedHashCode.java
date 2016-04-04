package com.google.common.hash;

public class BufferedHashCode extends HashCode {

	private final HashCode code;
	private final byte[] bufferedBytes;

	public BufferedHashCode(HashCode code, byte[] asBytes) {
		this.code = code;
		this.bufferedBytes = asBytes;
	}

	@Override
	public int bits() {
		return this.code.bits();
	}

	@Override
	public int asInt() {
		return this.code.asInt();
	}

	@Override
	public long asLong() {
		return this.code.asLong();
	}

	@Override
	public long padToLong() {
		return this.code.padToLong();
	}

	@Override
	public byte[] asBytes() {
		return this.bufferedBytes;
	}

	@Override
	void writeBytesToImpl(byte[] dest, int offset, int maxLength) {
		this.code.writeBytesTo(dest, offset, maxLength);
	}

	@Override
	boolean equalsSameBits(HashCode that) {
		return this.code.equalsSameBits(that);
	}

}
