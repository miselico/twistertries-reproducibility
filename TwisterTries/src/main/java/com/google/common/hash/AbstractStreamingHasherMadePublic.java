package com.google.common.hash;

import com.google.common.hash.AbstractStreamingHashFunction.AbstractStreamingHasher;

public abstract class AbstractStreamingHasherMadePublic extends AbstractStreamingHasher {

	protected AbstractStreamingHasherMadePublic(int arg0, int arg1) {
		super(arg0, arg1);
	}

	protected AbstractStreamingHasherMadePublic(int arg0) {
		super(arg0);
	}

	@Override
	public abstract HashCode makeHash();

}
