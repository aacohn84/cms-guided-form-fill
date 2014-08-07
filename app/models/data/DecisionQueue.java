package models.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class DecisionQueue implements Iterable<Decision> {
	public Queue<Decision> decisions;

	public DecisionQueue() {
		decisions = new LinkedList<Decision>();
	}

	public void addDecision(Decision decision) {
		if (decision == null) {
			throw new RuntimeException(
					"Tried to add a null decision to the DecisionQueue.");
		}
		decisions.add(decision);
	}

	public Decision getDecision() {
		return decisions.poll();
	}

	@Override
	public Iterator<Decision> iterator() {
		return decisions.iterator();
	}
}
