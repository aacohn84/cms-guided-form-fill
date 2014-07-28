package models.forms;

import models.tree.FieldsNode;
import models.tree.FieldsNode.FieldType;
import models.tree.NoteNode;
import models.tree.ChoiceNode;

public class ChangeOrderForm extends CMSForm {

	private static ChangeOrderForm instance;

	private ChangeOrderForm() {
		root = new NoteNode("entry_note", "a note.", "change_order_type");
		addNode(root);

		addNode(new ChoiceNode("change_order_type")
			.addOption("Return/Exch", "none")
			.addOption("Transfer", "none")
			.addOption("Disinterment", "none"));

		// Transfer
		addNode(new ChoiceNode("transfer_contract_paid")
			.addOption("Yes", "transfer_type")
			.addOption("No", "current_contract_balance_form"));

		addNode(new FieldsNode("current_contract_balance_form",
				"receipt_reflecting_balance")
			.addField("Current Contract Balance", FieldType.DOLLAR));

		addNode(new NoteNode("receipt_reflecting_balance",
				"Cash receipt reflecting balance.", "transfer_type"));

		addNode(new NoteNode("fee_waived", "Fee waived, no addt'l sigs",
				"name_loc_num_reason_2"));
		
		addNode(new FieldsNode("name_loc_num_reason_2", "done")
			.addField("Patron Name", FieldType.TEXT)
			.addField("Cemetery & Location", FieldType.TEXT)
			.addField("Original Contract Number", FieldType.TEXT)
			.addField("Reason for Change Order", FieldType.TEXT));
		
		addNode(new NoteNode("done", "End of form.", "none"));
	}
	
	public static ChangeOrderForm getInstance() {
		if (instance == null) {
			instance = new ChangeOrderForm();
		}
		return instance;
	}
}
