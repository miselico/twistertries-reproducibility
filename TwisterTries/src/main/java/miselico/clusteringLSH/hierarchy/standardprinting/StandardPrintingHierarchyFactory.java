package miselico.clusteringLSH.hierarchy.standardprinting;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveLeaf;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

public class StandardPrintingHierarchyFactory<E> implements HierarchyFactory<E> {

	private final AtomicInteger IDGenerator;
	private final PrintStream out;
	private final int leafCount;
	private int leafCounter;

	public StandardPrintingHierarchyFactory(OutputStream out, int i) {
		this.leafCount = i;
		this.out = new PrintStream(out);
		this.IDGenerator = new AtomicInteger();
		this.leafCounter = 0;
	}

	private static final CharMatcher quoteMatcher = CharMatcher.is('\'');

	@Override
	public ActiveLeaf<E> createLeaf(String ID, E content) {
		Preconditions.checkArgument(!StandardPrintingHierarchyFactory.quoteMatcher.matchesAnyOf(ID), "The quote symbol is not allowed as an because it is used as a separator when printing.");
		Preconditions.checkArgument(this.leafCounter < this.leafCount);
		StandardPrintingLeaf<E> leaf = new StandardPrintingLeaf<E>(this.leafCounter);
		this.leafCounter++;
		return leaf;
	}

	@Override
	public ActiveElement<E> merge(ActiveElement<E> left, ActiveElement<E> right) {
		Preconditions.checkArgument(left instanceof StandardPrintingActiveElement);
		Preconditions.checkArgument(right instanceof StandardPrintingActiveElement);
		StandardPrintingActiveElement<E> la = (StandardPrintingActiveElement<E>) left;
		StandardPrintingActiveElement<E> ra = (StandardPrintingActiveElement<E>) right;
		this.out.printf("%d %d\n", la.getIntID(), ra.getIntID());
		this.out.flush();
		int ID = (this.IDGenerator.incrementAndGet() + this.leafCount) - 1;
		return new StandardPrintingActiveElement<E>(la.getValueCount() + ra.getValueCount(), ID);
	}

}
