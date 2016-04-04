package miselico.clusteringLSH.twistertrie;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class ForestConfig {
	private final int numberOfTries;
	private final int trieheight;

	public ForestConfig(int numberOfTries, int trieheight) {
		Preconditions.checkArgument(numberOfTries > 0, "Must have at least one trie, got %s", numberOfTries);
		Preconditions.checkArgument(trieheight > 0, "Tree must have a positive height, got %s", trieheight);
		this.numberOfTries = numberOfTries;
		this.trieheight = trieheight;
	}

	public int getNumberOfTries() {
		return this.numberOfTries;
	}

	public int getTrieheight() {
		return this.trieheight;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("tries", this.numberOfTries).add("height", this.trieheight).toString();
	}
}
