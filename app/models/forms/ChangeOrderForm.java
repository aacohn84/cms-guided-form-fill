package models.forms;

import java.math.BigDecimal;

import models.tree.CalculationNode;
import models.tree.FeeNode;
import models.tree.FieldsNode;
import models.tree.FieldsNode.FieldType;
import models.tree.NoteChecksABoxNode;
import models.tree.NoteNode;
import models.tree.ChoiceNode;
import static models.tree.CalculationNode.*;

public class ChangeOrderForm extends CMSForm {
	
	private static ChangeOrderForm instance;
	
	public static ChangeOrderForm getInstance() {
		if (instance == null) {
			instance = new ChangeOrderForm();
		}
		return instance;
	}

	private ChangeOrderForm() {
		super("Change_Order_Form.pdf");
		
		root = new NoteNode(prerequisites_note, change_order_type_choice,
				"a note.");
		addNode(root);

		// What does the patron wish to do? (return/exchg, transfer, disinterment)
		addNode(new ChoiceNode(change_order_type_choice,
				"What does the patron wish to do?", "Change Order Type")
			.addChoice("Return/Exch", "Return", curr_contract_value)
			.addChoice("Transfer", "Assignment", paid_in_full_choice_2)
			.addChoice("Disinterment", "Disinterment", "none"));
		
		returnExchg();
		
		transfer();
		
		partiesAvailable();
		
		addNode(new NoteNode(done, "none", "End of form."));
	}

	private void returnExchg() {
		// Return / Exchange
		addNode(new FieldsNode(curr_contract_value, curr_contract_balance_2)
			.addField("Current contract value", "Contract Amount",
					FieldType.NUMBER));
		
		addNode(new FieldsNode(curr_contract_balance_2, return_inv_choice)
			.addField("Current Contract Balance", "Contract Balance",
					FieldType.NUMBER));
	
		addNode(new ChoiceNode(return_inv_choice,
				"Is inventory to be returned?")
			.addChoice("Yes", orig_contract_date_1)
			.addChoice("No", orig_contract_date_2));
	}

	private void transfer() {
		addNode(new ChoiceNode(paid_in_full_choice_2,
				"Is the original contract paid in full?")
			.addChoice("Yes", transfer_type_choice)
			.addChoice("No", curr_contract_balance_3));

		addNode(new FieldsNode(curr_contract_balance_3,
				cash_receipt_2)
			.addField("Current Contract Balance", "Contract Balance",
					FieldType.NUMBER));

		addNode(new NoteNode(cash_receipt_2,
				transfer_type_choice, "Cash receipt reflecting balance."));

		addNode(new ChoiceNode(transfer_type_choice, "Transfer Type: ",
				"Property Assignment")
			.addChoice("Transfer", "Transfer of Ownership", transfer_fee_note)
			.addChoice("Donation", "Donation", plot_fmv_2)
			.addChoice("Release", "Release of Interest", transfer_fee_waived_note));

		transferTransfer();
		
		transferDonation();

		transferRelease();
	}

	private void transferTransfer() {
		addNode(new FeeNode(transfer_fee_note, name_7,
				"A processing fee applies for any property transfer to a new owner.",
				"Admin/Return Fees",
				new BigDecimal("300")));
		
		addNode(new FieldsNode(name_7, loc_7)
		.addField("Patron Name", "Names", FieldType.TEXT));

		addNode(new FieldsNode(loc_7, orig_contract_num_7)
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));
	
		addNode(new FieldsNode(orig_contract_num_7, assignee_info)
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));
		
		addNode(new FieldsNode(assignee_info, reason_7)
			.addField("Name", "Assignee Names", FieldType.TEXT)
			.addField("Address", "Assignee Address", FieldType.TEXT)
			.addField("Phone", "Assignee Phone", FieldType.TEXT)
			.addField("Email", "Assignee Email", FieldType.TEXT));
	
		addNode(new FieldsNode(reason_7, calc_7)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));
		
		addNode(new CalculationNode(calc_7, parties_avail_choice)
			.addCalculatedField("Credit/Balance", new RefExpr(
					"Admin/Return Fees")));
	}

	private void transferDonation() {
		addNode(new FieldsNode(plot_fmv_2, name_8)
			.addFilledField("Admin/Return Fees", "0")
			.addField("Donation", "Donation Amount", FieldType.NUMBER));
	
		addNode(new FieldsNode(name_8, loc_8)
			.addField("Patron Name", "Names", FieldType.TEXT));
	
		addNode(new FieldsNode(loc_8, orig_contract_num_8)
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));
	
		addNode(new FieldsNode(orig_contract_num_8, reason_8)
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));
	
		addNode(new FieldsNode(reason_8, parties_avail_choice)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));
	}

	private void transferRelease() {
		addNode(new FeeNode(transfer_fee_waived_note, name_9,
				"Fee waived, no addt'l sigs", "Admin/Return Fees",
				BigDecimal.ZERO));

		addNode(new FieldsNode(name_9, loc_9)
			.addField("Patron Name", "Names", FieldType.TEXT));

		addNode(new FieldsNode(loc_9, orig_contract_num_9)
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));

		addNode(new FieldsNode(orig_contract_num_9, reason_9)
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));
	
		addNode(new FieldsNode(reason_9, done)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));
	}
	
	private void partiesAvailable() {
		addNode(new ChoiceNode(parties_avail_choice,
				"Are all parties to the original contract available to sign?")
			.addChoice("Yes", done)
			.addChoice("No", party_present_choice));
		
		addNode(new ChoiceNode(party_present_choice, "Who is present?")
			.addChoice("Party(s) to contract", reason_party_unavail_choice)
			.addChoice("Other", consult_ad_note));
		
		addNode(new ChoiceNode(reason_party_unavail_choice, "Why unavailable?")
			.addChoice("Death", cfcs_burial_choice)
			.addChoice("Relocation", relocation_choice)
			.addChoice("Can't locate", reasonable_effort_choice));
		
		addNode(new NoteNode(consult_ad_note, done,
				"Consult A.D. for requirements."));
		
		addNode(new ChoiceNode(cfcs_burial_choice, "Are the deceased buried in a CFCS cemetery?")
			.addChoice("Yes", burial_evidence_note)
			.addChoice("No", death_cert_note));
		
		addNode(new NoteChecksABoxNode(burial_evidence_note, done,
				"Evidence of Burial", "Evidence of burial required."));
		
		addNode(new NoteChecksABoxNode(death_cert_note, done,
				"Death Certificate", "Death certificate required."));
		
		addNode(new ChoiceNode(relocation_choice,
				"Will out-of-town parties be signing Change Order?")
			.addChoice("Yes", notary_sig_note)
			.addChoice("No", notarized_release_note));
		
		addNode(new NoteChecksABoxNode(notary_sig_note, done,
				"Notary Signature", "Notary signature required."));
		
		addNode(new NoteChecksABoxNode(notarized_release_note, done,
				"Signed/Notarized Release",
				"Signed, notarized release required."));
		
		addNode(new ChoiceNode(reasonable_effort_choice,
				"Have reasonable efforts been made to locate party(s)?")
			.addChoice("Yes", due_diligence_note)
			.addChoice("No", loc_all_parties_note));
		
		addNode(new NoteChecksABoxNode(due_diligence_note, done,
				"Statement of Due Diligence Form",
				"Statement of due diligence form required."));
		
		addNode(new NoteNode(loc_all_parties_note, due_diligence_note,
				"Efforts must be made to locate all parties to the original contract!"));
	}
	
	// Node names
	@SuppressWarnings("unused")
	private static final String
	prerequisites_note = "prerequisites_note",
	change_order_type_choice = "change_order_type_choice",
	curr_contract_value = "curr_contract_value",
	curr_contract_balance_1 = "curr_contract_balance_1",
	curr_contract_balance_2 = "curr_contract_balance_2",
	curr_contract_balance_3 = "curr_contract_balance_3",
	paid_in_full_choice_1 = "paid_in_full_choice_1",
	paid_in_full_choice_2 = "paid_in_full_choice_2",
	cash_receipt_1 = "cash_receipt_1",
	cash_receipt_2 = "cash_receipt_2",
	return_inv_choice = "return_inv_choice",
	property_owner_choice = "property_owner_choice",
	property_owner_sig_note = "property_owner_sig_note",
	disint_type_choice = "disint_type_choice",
	disint_fee_1 = "disint_fee_1",
	disint_fee_2 = "disint_fee_2",
	disint_fee_3 = "disint_fee_3",
	disint_fee_4 = "disint_fee_4",
	name_1 = "name_1",
	name_2 = "name_2",
	name_3 = "name_3",
	name_4 = "name_4",
	name_5 = "name_5",
	name_6 = "name_6",
	name_7 = "name_7",
	name_8 = "name_8",
	name_9 = "name_9",
	loc_1 = "loc_1",
	loc_2 = "loc_2",
	loc_3 = "loc_3",
	loc_4 = "loc_4",
	loc_5 = "loc_5",
	loc_6 = "loc_6",
	loc_7 = "loc_7",
	loc_8 = "loc_8",
	loc_9 = "loc_9",
	orig_contract = "orig_contract_num_1",
	orig_contract_num_2 = "orig_contract_num_2",
	orig_contract_num_3 = "orig_contract_num_3",
	orig_contract_num_4 = "orig_contract_num_4",
	orig_contract_num_5 = "orig_contract_num_5",
	orig_contract_num_6 = "orig_contract_num_6", 
	orig_contract_num_7 = "orig_contract_num_7",
	orig_contract_num_8 = "orig_contract_num_8",
	orig_contract_num_9 = "orig_contract_num_9",
	disint_info = "disint_info",
	reason_1 = "reason_1",
	reason_2 = "reason_2",
	reason_3 = "reason_3",
	reason_4 = "reason_4",
	reason_5 = "reason_5",
	reason_6 = "reason_6",
	reason_7 = "reason_7", 
	reason_8 = "reason_8",
	reason_9 = "reason_9",
	calc_1 = "calc_1",
	calc_2 = "calc_2",
	calc_3 = "calc_3",
	calc_4 = "calc_4",
	calc_5 = "calc_5",
	calc_6 = "calc_6",
	calc_7 = "calc_7",
	transfer_type_choice = "transfer_type_choice",
	transfer_fee_note = "transfer_fee_note",
	transfer_donation = "transfer_donation",
	transfer_fee_waived_note = "transfer_fee_waived_note",
	assignee_info = "assignee_info",
	orig_contract_date_1 = "orig_contract_date_1",
	orig_contract_date_2 = "orig_contract_date_2",
	admin_fee_waived_1 = "admin_fee_waived_1",
	admin_fee_waived_2 = "admin_fee_waived_2",
	admin_fee_waived_3 = "admin_fee_waived_3",
	admin_fee_waived_4 = "admin_fee_waived_4",
	upgrade_exchg_choice_1 = "upgrade_exchg_choice_1",
	upgrade_exchg_choice_2 = "upgrade_exchg_choice_2",
	admin_fee_1 = "admin_fee_1",
	admin_fee_2 = "admin_fee_2",
	return_int_rights_choice = "return_int_rights_choice",
	return_goods_choice = "return_goods_choice",
	gift_amount = "gift_amount",
	items_returned_1 = "items_returned_1",
	items_returned_2 = "items_returned_2",
	credits_discounts_1 = "credits_discounts_1",
	credits_discounts_2 = "credits_discounts_2",
	apply_credit_choice = "apply_credit_choice",
	new_contract_note = "new_contract_note",
	existing_contract_note = "existing_contract_note",
	refund_request_note = "refund_request_note",
	plot_fmv_1 = "plot_fmv_1",
	plot_fmv_2 = "plot_fmv_2",
	parties_avail_choice = "parties_avail_choice",
	party_present_choice = "party_present_choice",
	consult_ad_note = "consult_ad_note",
	reason_party_unavail_choice = "reason_party_unavail_choice",
	cfcs_burial_choice = "cfcs_burial_choice",
	relocation_choice = "relocation_choice",
	reasonable_effort_choice = "reasonable_effort_choice",
	burial_evidence_note = "burial_evidence_note",
	death_cert_note = "death_cert_note",
	notary_sig_note = "notary_sig_note",
	notarized_release_note = "notarized_release_note",
	due_diligence_note = "due_diligence_note",
	loc_all_parties_note = "loc_all_parties_note",
	done = "done";
}
