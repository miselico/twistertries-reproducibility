package miselico.clusteringLSH.hierarchy.printing;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import miselico.clusteringLSH.hierarchy.ActiveElement;
import miselico.clusteringLSH.hierarchy.ActiveLeaf;
import miselico.clusteringLSH.hierarchy.HierarchyFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;

public class PrintingHierarchyFactory<E> implements HierarchyFactory<E> {

	private final AtomicInteger IDGenerator;
	private final PrintStream out;

	public PrintingHierarchyFactory(OutputStream out) {
		this.out = new PrintStream(out);
		this.IDGenerator = new AtomicInteger();
	}

	private static final CharMatcher quoteMatcher = CharMatcher.is('\'');

	@Override
	public ActiveLeaf<E> createLeaf(String ID, E content) {
		Preconditions.checkArgument(!PrintingHierarchyFactory.quoteMatcher.matchesAnyOf(ID), "The quote symbol is not allowed as an because it is used as a separator when printing.");
		return new PrintingLeaf<E>(ID);
	}

	@Override
	public ActiveElement<E> merge(ActiveElement<E> left, ActiveElement<E> right) {
		Preconditions.checkArgument(left instanceof PrintingActiveElement);
		Preconditions.checkArgument(right instanceof PrintingActiveElement);
		PrintingActiveElement<E> la = (PrintingActiveElement<E>) left;
		PrintingActiveElement<E> ra = (PrintingActiveElement<E>) right;
		int number = this.IDGenerator.incrementAndGet();
		String newID = "merged" + number;
		this.out.printf("'%s'='%s'U'%s'\n", newID, la.getID(), ra.getID());
		this.out.flush();
		return new PrintingActiveElement<E>(la.getValueCount() + ra.getValueCount(), newID);
	}

}
