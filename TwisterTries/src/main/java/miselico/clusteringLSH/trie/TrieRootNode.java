package miselico.clusteringLSH.trie;

import com.google.common.hash.HashCode;

public class TrieRootNode<E> extends TrieInternalNode<E> {

	public TrieRootNode() {
		super(null, null);
	}

	@Override
	public TrieInternalNode<E> getParent() {
		throw new Error("Parent of root node requested");
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public HashCode edgeFromParent() {
		throw new Error("edgeFromParent requested from root");
	}
}
