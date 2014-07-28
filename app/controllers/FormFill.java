package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import models.forms.CMSForm;
import models.forms.ChangeOrderForm;
import models.tree.Node;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.questionnaire.backdrop;

public class FormFill extends Controller {

	public static class RequestParams {
		public static final String CURRENT_NODE = "currentNode";
	}

	/*
	 * Provides the root node of the requested form.
	 */
	public static Result getForm() {
		CMSForm form = ChangeOrderForm.getInstance();
		return ok(backdrop.render(form.getRoot()));
	}

	/*
	 * Provides the next node of the current form.
	 */
	public static Result getNextNode() {
		CMSForm form = ChangeOrderForm.getInstance();
		String idCurrentNode = request().getQueryString(
				RequestParams.CURRENT_NODE);
		Node currentNode = form.getNode(idCurrentNode);

		Map<String, String> input = new HashMap<>();
		for (Entry<String, String[]> entry : request().queryString().entrySet()) {
			String value = entry.getValue()[0];
			input.put(entry.getKey(), value);
		}

		String idNextNode = currentNode.idNextNode(input);

		Node nextNode = form.getNode(idNextNode);

		return ok(backdrop.render(nextNode));
	}
}
