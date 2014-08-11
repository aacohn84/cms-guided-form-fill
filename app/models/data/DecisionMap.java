package models.data;

import java.util.HashMap;
import java.util.Map;

public class DecisionMap {
	Map<String, Decision> decisions;
	
	public DecisionMap() {
		decisions = new HashMap<String, Decision>();
	}
	
	public Decision getDecision(String contextId) {
		return decisions.get(contextId);
	}
	
	public void putDecision(Decision decision) {
		decisions.put(decision.context.id, decision);
	}
}
