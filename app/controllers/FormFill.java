package controllers;

import java.util.List;
import java.util.Map;
import models.forms.CMSForm;
import models.forms.ChangeOrderForm;
import models.tree.Node;
import play.data.Form;
import play.mvc.Result;
import views.html.questionnaire.backdrop;

public class FormFill extends SecureController {

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
		// get current node
		CMSForm form = ChangeOrderForm.getInstance();
		Map<String, String> requestData = Form.form().bindFromRequest().data();
		String idCurrentNode = requestData.get(RequestParams.CURRENT_NODE);
		Node currentNode = form.getNode(idCurrentNode);

		// validate user input

		// save user input
		String serializedInput = currentNode.serializeInput(requestData);
		session().put(currentNode.id, serializedInput);
		
		// CMSSession.print();

		// retrieve next node
		String idNextNode = currentNode.getIdNextNode(requestData);
		Node nextNode = form.getNode(idNextNode);

		return ok(backdrop.render(nextNode));
	}
	
	public static Result getFormOutput() {
		CMSForm form = ChangeOrderForm.getInstance();
		List<Node> nodes = form.getOutputNodes();
		
		return ok(views.html.questionnaire.output.render(nodes));
	}
}
