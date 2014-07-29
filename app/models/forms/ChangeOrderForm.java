package models.forms;

import models.tree.FieldsNode;
import models.tree.FieldsNode.FieldType;
import models.tree.NoteNode;
import models.tree.ChoiceNode;

public class ChangeOrderForm extends CMSForm {

	private static ChangeOrderForm instance;

	private ChangeOrderForm() {
		root = new NoteNode("entry_note", "change_order_type", "a note.");
		addNode(root);

		addNode(new ChoiceNode("change_order_type",
				"What does the patron wish to do?", true)
			.addOption("Return/Exch", "none")
			.addOption("Transfer", "transfer_contract_paid")
			.addOption("Disinterment", "none"));

		// Transfer
		addNode(new ChoiceNode("transfer_contract_paid",
				"Is the original contract paid in full?")
			.addOption("Yes", "transfer_type")
			.addOption("No", "current_contract_balance_form"));

		addNode(new FieldsNode("current_contract_balance_form",
				"receipt_reflecting_balance")
			.addField("Current Contract Balance", FieldType.NUMBER));

		addNode(new NoteNode("receipt_reflecting_balance",
				"transfer_type", "Cash receipt reflecting balance."));

		addNode(new ChoiceNode("transfer_type", "Transfer Type: ", true)
			.addOption("Transfer", "none")
			.addOption("Donation", "none")
			.addOption("Release", "fee_waived"));

		addNode(new NoteNode("fee_waived", "name_loc_num_reason_2",
				"Fee waived, no addt'l sigs"));

		addNode(new FieldsNode("name_loc_num_reason_2", "done", true)
			.addField("Patron Name", FieldType.TEXT)
			.addField("Cemetery & Location", FieldType.TEXT)
			.addField("Original Contract Number", FieldType.TEXT)
			.addField("Reason for Change Order", FieldType.TEXT));

		addNode(new NoteNode("done", "none", "End of form."));
	}

	public static ChangeOrderForm getInstance() {
		if (instance == null) {
			instance = new ChangeOrderForm();
		}
		return instance;
	}
}
