package com.google.common.hash;

import java.nio.ByteBuffer;

public abstract class AbstractStreamingHashFunctionMadePublic extends com.google.common.hash.AbstractStreamingHashFunction {

	@Override
	public Hasher newHasher() {
		return new AbstractStreamingHasher(0) {

			@Override
			protected void process(ByteBuffer bb) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void processRemaining(ByteBuffer bb) {
				//this could be overriden if we do things differently for non full bytebuffers
				super.processRemaining(bb);
			}

			@Override
			HashCode makeHash() {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}

	@Override
	public int bits() {
		return 53;
	}

}
