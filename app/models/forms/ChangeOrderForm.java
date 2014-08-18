package models.forms;

import java.math.BigDecimal;

import models.tree.CalculationNode;
import models.tree.FeeNode;
import models.tree.FieldsNode;
import models.tree.FieldsNode.FieldType;
import models.tree.NoteNode;
import models.tree.ChoiceNode;
import static models.tree.CalculationNode.*;

public class ChangeOrderForm extends CMSForm {
	
	static enum Fields {
		// TODO: name each form field.
	}
	
	public static enum NodeDef {
		PREREQUISITES_NOTE,
		CHANGE_ORDER_TYPE_CHOICE,
		CURR_CONTRACT_VALUE,
		CURR_CONTRACT_BALANCE_1,
		CURR_CONTRACT_BALANCE_2,
		CURR_CONTRACT_BALANCE_3,
		PAID_IN_FULL_CHOICE_1,
		PAID_IN_FULL_CHOICE_2,
		CASH_RECEIPT_1,
		CASH_RECEIPT_2,
		RETURN_INV_CHOICE,
		PROPERTY_OWNER_CHOICE,
		PROPERTY_OWNER_SIG_NOTE,
		DISINT_TYPE_CHOICE,
		DISINT_FEE_1,
		DISINT_FEE_2,
		DISINT_FEE_3,
		DISINT_FEE_4,
		NAME_1,
		NAME_2,
		NAME_3,
		NAME_4,
		NAME_5,
		NAME_6,
		NAME_7,
		NAME_8,
		NAME_9,
		LOC_1,
		LOC_2,
		LOC_3,
		LOC_4,
		LOC_5,
		LOC_6,
		LOC_7,
		LOC_8,
		LOC_9,
		ORIG_CONTRACT_NUM_1,
		ORIG_CONTRACT_NUM_2,
		ORIG_CONTRACT_NUM_3,
		ORIG_CONTRACT_NUM_4,
		ORIG_CONTRACT_NUM_5,
		ORIG_CONTRACT_NUM_6, 
		ORIG_CONTRACT_NUM_7,
		ORIG_CONTRACT_NUM_8,
		ORIG_CONTRACT_NUM_9,
		DISINT_INFO,
		REASON_1,
		REASON_2,
		REASON_3,
		REASON_4,
		REASON_5,
		REASON_6,
		REASON_7, 
		REASON_8,
		REASON_9,
		CALC_1,
		CALC_2,
		CALC_3,
		CALC_4,
		CALC_5,
		CALC_6,
		CALC_7,
		TRANSFER_TYPE_CHOICE,
		TRANSFER_FEE_NOTE,
		TRANSFER_DONATION,
		TRANSFER_FEE_WAIVED_NOTE,
		ASIGNEE_INFO,
		ORIG_CONTRACT_DATE_1,
		ORIG_CONTRACT_DATE_2,
		ADMIN_FEE_WAIVED_1,
		ADMIN_FEE_WAIVED_2,
		ADMIN_FEE_WAIVED_3,
		ADMIN_FEE_WAIVED_4,
		UPGRADE_EXCHG_CHOICE_1,
		UPGRADE_EXCHG_CHOICE_2,
		ADMIN_FEE_1,
		ADMIN_FEE_2,
		RETURN_INT_RIGHTS_CHOICE,
		RETURN_GOODS_CHOICE,
		GIFT_AMOUNT,
		ITEMS_RETURNED_1,
		ITEMS_RETURNED_2,
		CREDITS_DISCOUNTS_1,
		CREDITS_DISCOUNTS_2,
		APPLY_CREDIT_CHOICE,
		NEW_CONTRACT_NOTE,
		EXISTING_CONTRACT_NOTE,
		REFUND_REQUEST_NOTE,
		PLOT_FMV_1,
		PLOT_FMV_2,
		PARTIES_AVAIL_CHOICE,
		PARTY_PRESENT_CHOICE,
		CONSULT_AD_NOTE,
		REASON_PARTY_UNAVAIL_CHOICE,
		CFCS_BURIAL_CHOICE,
		RELOCATION_CHOICE,
		REASONABLE_EFFORT_CHOICE,
		BURIAL_EVIDENCE_NOTE,
		NOTARY_SIG_NOTE,
		NOTARIZED_RELEASE_NOTE,
		DUE_DILIGENCE_NOTE,
		LOC_ALL_PARTIES_NOTE;
	}
	
	private static ChangeOrderForm instance;
	
	public static ChangeOrderForm getInstance() {
		if (instance == null) {
			instance = new ChangeOrderForm();
		}
		return instance;
	}

	private ChangeOrderForm() {
		root = new NoteNode("prerequisites_note", "change_order_type_choice",
				"a note.");
		addNode(root);

		// What does the patron wish to do? (return/exchg, transfer, disinterment)
		addNode(new ChoiceNode("change_order_type_choice",
				"What does the patron wish to do?", "change_order_type_choice")
			.addChoice("Return/Exch", "Return", "curr_contract_value")
			.addChoice("Transfer", "Assignment", "paid_in_full_choice_2")
			.addChoice("Disinterment", "Disinterment", "none"));
		
		returnExchg();
		
		transfer();
		
		addNode(new NoteNode("done", "none", "End of form."));
	}

	private void returnExchg() {
		// Return / Exchange
		addNode(new FieldsNode("curr_contract_value", "curr_contract_balance_2")
			.addField("Current contract value", "", FieldType.NUMBER));
		
		addNode(new FieldsNode("curr_contract_balance_2", "return_inv_choice")
			.addField("Current Contract Balance", "Contract Balance",
					FieldType.NUMBER));
	
		addNode(new ChoiceNode("return_inv_choice",
				"Is inventory to be returned?")
			.addChoice("Yes", "orig_contract_date_1")
			.addChoice("No", "orig_contract_date_2"));
	}

	private void transfer() {
		addNode(new ChoiceNode("paid_in_full_choice_2",
				"Is the original contract paid in full?")
			.addChoice("Yes", "transfer_type_choice")
			.addChoice("No", "curr_contract_balance_3"));

		addNode(new FieldsNode("curr_contract_balance_3",
				"cash_receipt_2")
			.addField("Current Contract Balance", "Contract Balance",
					FieldType.NUMBER));

		addNode(new NoteNode("cash_receipt_2",
				"transfer_type_choice", "Cash receipt reflecting balance."));

		addNode(new ChoiceNode("transfer_type_choice", "Transfer Type: ",
				"transfer_type_choice")
			.addChoice("Transfer", "Transfer of Ownership", "transfer_fee")
			.addChoice("Donation", "Donation", "plot_fmv_2")
			.addChoice("Release", "Release of Interest", "transfer_fee_waived_note"));

		transferTransfer();
		
		transferDonation();

		transferRelease();
	}

	private void transferTransfer() {
		addNode(new FeeNode("transfer_fee", "name_7",
				"$300 fee applies to New Owner Transfer", "Admin/Return Fees",
				new BigDecimal("300")));
		
		addNode(new FieldsNode("name_7", "loc_7")
		.addField("Patron Name", "Names", FieldType.TEXT));

		addNode(new FieldsNode("loc_7", "orig_contract_num_7")
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));
	
		addNode(new FieldsNode("orig_contract_num_7", "reason_7")
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));
		
		addNode(new FieldsNode("assignee_info", "reason_7")
			.addField("Name", "Assignee Name", FieldType.TEXT)
			.addField("Address", "Assignee Address", FieldType.TEXT)
			.addField("Phone", "Assignee Phone", FieldType.TEXT)
			.addField("Email", "Assignee Email", FieldType.TEXT));
	
		addNode(new FieldsNode("reason_7", "calc_7")
			.addField("Reason for Change Order", "Reason for Change Order",
					FieldType.TEXT));
		
		addNode(new CalculationNode("calc_7", "done")
			.addCalculatedField("Credit/Balance", new RefExpr(
					"Admin/Return Fees")));
	}

	private void transferDonation() {
		addNode(new FieldsNode("plot_fmv_2", "name_8")
			.addFilledField("Admin/Return Fees", "0")
			.addField("Donation", "Donation", FieldType.NUMBER));
	
		addNode(new FieldsNode("name_8", "loc_8")
			.addField("Patron Name", "Names", FieldType.TEXT));
	
		addNode(new FieldsNode("loc_8", "orig_contract_num_8")
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));
	
		addNode(new FieldsNode("orig_contract_num_8", "reason_8")
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));
	
		addNode(new FieldsNode("reason_8", "done")
			.addField("Reason for Change Order", "Reason for Change Order",
					FieldType.TEXT));
	}

	private void transferRelease() {
		addNode(new FeeNode("transfer_fee_waived_note", "name_9",
				"Fee waived, no addt'l sigs", "Admin/Return Fees",
				BigDecimal.ZERO));

		addNode(new FieldsNode("name_9", "loc_9")
			.addField("Patron Name", "Names", FieldType.TEXT));

		addNode(new FieldsNode("loc_9", "orig_contract_num_9")
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));

		addNode(new FieldsNode("orig_contract_num_9", "reason_9")
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));
	
		addNode(new FieldsNode("reason_9", "done")
			.addField("Reason for Change Order", "Reason for Change Order",
					FieldType.TEXT));
	}
}
