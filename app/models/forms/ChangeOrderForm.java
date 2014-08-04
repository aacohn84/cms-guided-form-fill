package models.forms;

import models.tree.FieldsNode;
import models.tree.FieldsNode.FieldType;
import models.tree.NoteNode;
import models.tree.ChoiceNode;

public class ChangeOrderForm extends CMSForm {

	private static ChangeOrderForm instance;

	private ChangeOrderForm() {
		root = new NoteNode("entry_note", "change_order_type_choice", "a note.");
		addNode(root);

		addNode(new ChoiceNode("change_order_type_choice",
				"What does the patron wish to do?", "change_order_type_choice")
			.addChoice("Return/Exch", "none")
			.addChoice("Transfer", "paid_in_full_choice_2")
			.addChoice("Disinterment", "none"));

		// Transfer
		addNode(new ChoiceNode("paid_in_full_choice_2",
				"Is the original contract paid in full?")
			.addChoice("Yes", "transfer_type")
			.addChoice("No", "curr_contract_balance_3"));

		addNode(new FieldsNode("curr_contract_balance_3",
				"cash_receipt_2")
			.addField("Current Contract Balance", "Contract Balance",
					FieldType.NUMBER));

		addNode(new NoteNode("cash_receipt_2",
				"transfer_type_choice", "Cash receipt reflecting balance."));

		addNode(new ChoiceNode("transfer_type_choice", "Transfer Type: ",
				"transfer_type_choice")
			.addChoice("Transfer", "none")
			.addChoice("Donation", "none")
			.addChoice("Release", "transfer_fee_waived"));

		addNode(new NoteNode("transfer_fee_waived", "name_9",
				"Fee waived, no addt'l sigs"));

		addNode(new FieldsNode("name_9", "loc_9")
			.addField("Patron Name", "Names", FieldType.TEXT));
		
		addNode(new FieldsNode("loc_9", "orig_contract_num_9")
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));
		
		addNode(new FieldsNode("orig_contract_num_9", "change_order_reason")
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));
			
		addNode(new FieldsNode("change_order_reason", "done")
			.addField("Reason for Change Order", "Reason for Change Order", 
					FieldType.TEXT));

		addNode(new NoteNode("done", "none", "End of form."));
	}

	public static ChangeOrderForm getInstance() {
		if (instance == null) {
			instance = new ChangeOrderForm();
		}
		return instance;
	}
}
