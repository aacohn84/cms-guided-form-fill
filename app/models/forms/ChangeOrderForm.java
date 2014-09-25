package models.forms;

import java.math.BigDecimal;

import models.data.FilledFormFields;
import models.tree.CalculationNode;
import models.tree.CalculationNode.BinaryExpr;
import models.tree.CalculationNode.Condition;
import models.tree.CalculationNode.ConditionalExpr;
import models.tree.CalculationNode.NumExpr;
import models.tree.CalculationNode.Operators;
import models.tree.CalculationNode.RefExpr;
import models.tree.CalculationNode.nAryExpr;
import models.tree.ChoiceNode;
import models.tree.FeeNode;
import models.tree.NoteChecksABoxNode;
import models.tree.NoteNode;
import models.tree.TerminalNode;
import models.tree.fields.EmailField;
import models.tree.fields.FieldTableNode;
import models.tree.fields.FieldsNode;
import models.tree.fields.HiddenField;
import models.tree.fields.NumberField;
import models.tree.fields.SelectField;
import models.tree.fields.TextAreaField;
import models.tree.fields.TextField;

public class ChangeOrderForm extends CMSForm {
	private static ChangeOrderForm instance;

	public static ChangeOrderForm getInstance() {
		if (instance == null) {
			instance = new ChangeOrderForm();
		}
		return instance;
	}

	private ChangeOrderForm() {
		// Name of PDF file associated with this form
		super("Change_Order_Form.pdf");

		// decision tree definition
		root = addNode(new NoteChecksABoxNode(Id.prerequisites_note,
				Id.change_order_type_choice, PDFieldName.originalContract,
				Desc.prerequisites_note));

		addNode(new ChoiceNode(Id.change_order_type_choice,
				Desc.change_order_type_choice, PDFieldName.changeOrderType)
			.addChoice("Return/Exch", PDFieldName.return_, Id.curr_contract_value)
			.addChoice("Transfer", PDFieldName.assignment, Id.paid_in_full_choice_2)
			.addChoice("Disinterment", PDFieldName.disinterment, Id.paid_in_full_choice_1));

		returnExchg();

		transfer();

		disinterment();

		partiesAvailable();

		addNode(new TerminalNode(Id.done, Desc.done, Desc.done_detail));
	}

	private void disinterment() {
		addNode(Node.paidInFullChoice(Id.paid_in_full_choice_1,
				Id.property_owner_choice, Id.curr_contract_balance_1));

		addNode(Node.currContractBalance(Id.curr_contract_balance_1, Id.cash_receipt_1));

		addNode(Node.cashReceipt(Id.cash_receipt_1, Id.property_owner_choice));

		disintermentPropertyOwnerChoice();
	}

	private void disintermentPropertyOwnerChoice() {
		addNode(new ChoiceNode(Id.property_owner_choice, Desc.property_owner_choice)
			.addChoice("Yes", Id.property_owner_sig_note)
			.addChoice("No", Id.disint_type_choice));

		addNode(new NoteNode(Id.property_owner_sig_note, Id.disint_type_choice,
				Desc.property_owner_sig_note));

		disintermentType();
	}

	private void disintermentType() {
		addNode(new ChoiceNode(Id.disint_type_choice, Desc.disint_type_choice)
			.addChoice("Casket/Ground", Id.disint_fee_1)
			.addChoice("Casket/Crypt", Id.disint_fee_2)
			.addChoice("Urn", Id.disint_fee_3)
			.addChoice("Infant", Id.disint_fee_4));

		addNode(Node.disintFee(Id.disint_fee_1, new BigDecimal("1950.00")));
		addNode(Node.disintFee(Id.disint_fee_2, new BigDecimal("1350.00")));
		addNode(Node.disintFee(Id.disint_fee_3, new BigDecimal("750.00")));
		addNode(Node.disintFee(Id.disint_fee_4, new BigDecimal("500.00")));

		disintermentInfo();
	}

	private void disintermentInfo() {
		addNode(Node.name(Id.name_1, Id.loc_1));
		addNode(Node.loc(Id.loc_1, Id.orig_contract_num_1));
		addNode(Node.origContractNum(Id.orig_contract_num_1, Id.disint_info));

		addNode(new FieldsNode(Id.disint_info, Id.reason_1, Desc.disint_info)
			.addField(Field.decedents)
			.addField(Field.placeOfFinalDisposition)
			.addField(Field.cfcsReIntermentLocation)
			.addField(Field.cfcsReIntermentCemetery));

		addNode(Node.reason(Id.reason_1, Id.calc_1));

		addNode(new CalculationNode(Id.calc_1, Id.parties_avail_choice)
			.addCalculatedField(PDFieldName.creditBalance, new RefExpr(PDFieldName.adminReturnFees)));
	}

	private void returnExchg() {
		addNode(new FieldsNode(Id.curr_contract_value,
				Id.curr_contract_balance_2, Desc.curr_contract_value)
			.setDetailDescription(Desc.curr_contract_value_detail)
			.addField(Field.contractAmount));

		addNode(Node.currContractBalance(Id.curr_contract_balance_2,
				Id.return_inv_choice));

		addNode(new ChoiceNode(Id.return_inv_choice, Desc.return_inv_choice)
			.addChoice("Yes", Id.orig_contract_date_1)
			.addChoice("No", Id.orig_contract_date_2));

		returnInventoryYes();

		returnInventoryNo();

		applyCredit();
	}

	private void returnInventoryYes() {
		addNode(Node.origContractDate(Id.orig_contract_date_1,
				Id.admin_fee_waived_1, Id.upgrade_exchg_choice_1));
		addNode(Node.adminFeeWaived(Id.admin_fee_waived_1, Id.return_int_rights_choice));

		addNode(new ChoiceNode(Id.upgrade_exchg_choice_1, Desc.upgrade_exchg_choice)
			.addChoice("Yes", Id.admin_fee_waived_1)
			.addChoice("No", Id.admin_fee_1));

		addNode(new NoteNode(Id.admin_fee_1, Id.return_int_rights_choice,
				Desc.admin_fee));

		addNode(new ChoiceNode(Id.return_int_rights_choice, Desc.return_int_rights_choice)
			.addChoice("Yes-Contract date before 8/1/12", Id.name_2)
			.addChoice("Yes-Contract date after 8/1/12", Id.name_3)
			.addChoice("No", Id.name_4));

		returnIntermentRightsYes1();

		returnIntermentRightsYes2();

		returnIntermentRightsNo();
	}

	private void returnIntermentRightsYes1() {
		addNode(Node.name(Id.name_2, Id.loc_2));
		addNode(Node.loc(Id.loc_2, Id.orig_contract_num_2));
		addNode(Node.origContractNum(Id.orig_contract_num_2, Id.reason_2));
		addNode(Node.reason(Id.reason_2, Id.calc_2));
		
		addNode(new CalculationNode(Id.calc_2, Id.apply_credit_choice)
			/*
			 *  Total to be Returned = Contract Amount - Contract Balance
			 */
			.addCalculatedField(PDFieldName.totalToBeReturned,
				Expr.contractAmountMinusBalanceExpr)
			/* 
			 * If fee applies, then
			 * Admin/Return Fees = Contract Amount * 20%
			 */
			.addCalculatedField(PDFieldName.adminReturnFees,
				Expr.conditionalAdminReturnFees)
			/*
			 *  Total Deductions = Admin/Return Fees
			 */
			.addCalculatedField(PDFieldName.totalDeductions, Expr.adminReturnFees)
			/*
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(PDFieldName.creditBalance,
				Expr.amtReturnedMinusDeductions));
	}

	private void returnIntermentRightsYes2() {
		addNode(Node.name(Id.name_3, Id.loc_3));
		addNode(Node.loc(Id.loc_3, Id.orig_contract_num_3));
		addNode(Node.origContractNum(Id.orig_contract_num_3, Id.gift_amount));
		
		addNode(new FieldsNode(Id.gift_amount, Id.reason_3, Desc.gift_amount)
			.setDetailDescription(Desc.gift_amount_detail)
			.addField(Field.giftAmount));

		addNode(Node.reason(Id.reason_3, Id.calc_3));
		
		addNode(new CalculationNode(Id.calc_3, Id.apply_credit_choice)
			/* 
			 * Total to be Returned = Contract Amt - Contract Bal - Gift Amt
			 */
			.addCalculatedField(PDFieldName.totalToBeReturned,
				new BinaryExpr(
					Expr.contractAmountMinusBalanceExpr,
					new RefExpr(PDFieldName.giftAmount),
					Operators.SUBTRACT))
			/*
			 * If fee applies, then
			 * Admin/Return Fees = Contract Amount * 0.20
			 */
			.addCalculatedField(PDFieldName.adminReturnFees,
				Expr.conditionalAdminReturnFees)
			/*
			 *  Total Deductions = Admin/Return Fees
			 */
			.addCalculatedField(PDFieldName.totalDeductions,
					Expr.adminReturnFees)
			/* 
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(PDFieldName.creditBalance,
				Expr.amtReturnedMinusDeductions));
	}

	private void returnIntermentRightsNo() {
		addNode(Node.name(Id.name_4, Id.loc_4));
		addNode(Node.loc(Id.loc_4, Id.orig_contract_num_4));
		addNode(Node.origContractNum(Id.orig_contract_num_4, Id.items_returned_1));
		addNode(Node.itemsReturned(Id.items_returned_1, Id.credits_discounts_1));
		addNode(Node.creditsDiscounts(Id.credits_discounts_1, Id.reason_4));
		addNode(Node.reason(Id.reason_4, Id.calc_4));

		addNode(new CalculationNode(Id.calc_4, Id.apply_credit_choice)
			/*
			 *  Total to be Returned = Items to be Returned - Contract Balance
			 */
			.addCalculatedField(PDFieldName.totalToBeReturned, Expr.sumItemsReturnedMinusContractBalance)
			/* 
			 * If admin fee applies, then
			 * Admin/Return Fees = Items to be Returned * 20%
			 */
			.addCalculatedField(PDFieldName.adminReturnFees, new ConditionalExpr(
					Expr.zero,
					new BinaryExpr(
						Expr.sumItemsReturned,
						Expr.twentyPercent,
						Operators.MULTIPLY),
					Expr.adminReturnFeesCondition))
			/*
			 *  Total Deductions = Admin/Return Fees + Credits/Discounts
			 */
			.addCalculatedField(PDFieldName.totalDeductions, Expr.adminFeesPlusCredits)
			/*
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(PDFieldName.creditBalance, Expr.amtReturnedMinusDeductions));
	}

	private void returnInventoryNo() {
		addNode(Node.origContractDate(Id.orig_contract_date_2,
				Id.admin_fee_waived_2, Id.upgrade_exchg_choice_2));

		addNode(Node.adminFeeWaived(Id.admin_fee_waived_2, Id.return_goods_choice));

		addNode(new ChoiceNode(Id.upgrade_exchg_choice_2, Desc.upgrade_exchg_choice)
			.addChoice("Yes", Id.admin_fee_waived_2)
			.addChoice("No", Id.admin_fee_2));

		addNode(new NoteNode(Id.admin_fee_2, Id.return_goods_choice,
				Desc.admin_fee));

		addNode(new ChoiceNode(Id.return_goods_choice, Desc.return_goods_choice)
			.addChoice("Yes", Id.name_5)
			.addChoice("No", Id.name_6));

		returnGoodsYes();

		returnGoodsNo();
	}

	private void returnGoodsYes() {
		addNode(Node.name(Id.name_5, Id.loc_5));
		addNode(Node.loc(Id.loc_5, Id.orig_contract_num_5));
		addNode(Node.origContractNum(Id.orig_contract_num_5, Id.reason_5));
		addNode(Node.reason(Id.reason_5, Id.calc_5));
		
		addNode(new CalculationNode(Id.calc_5, Id.apply_credit_choice)
			/*
			 *  Total to be Returned = Contract Amount - Contract Balance
			 */
			.addCalculatedField(PDFieldName.totalToBeReturned, Expr.contractAmountMinusBalanceExpr)
			/*
			 *  Total Deductions = Admin/Return Fees
			 */
			.addCalculatedField(PDFieldName.totalDeductions, Expr.adminReturnFees)
			/*
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(PDFieldName.creditBalance, Expr.amtReturnedMinusDeductions));
	}

	private void returnGoodsNo() {
		addNode(Node.name(Id.name_6, Id.loc_6));
		addNode(Node.loc(Id.loc_6, Id.orig_contract_num_6));
		addNode(Node.origContractNum(Id.orig_contract_num_6, Id.items_returned_2));
		addNode(Node.itemsReturned(Id.items_returned_2, Id.credits_discounts_2));
		addNode(Node.creditsDiscounts(Id.credits_discounts_2, Id.reason_6));
		addNode(Node.reason(Id.reason_6, Id.calc_6));
		
		addNode(new CalculationNode(Id.calc_6, Id.apply_credit_choice)
			/*
			 *  Total to be Returned = Items to be Returned - Contract Balance
			 */
			.addCalculatedField(PDFieldName.totalToBeReturned, Expr.sumItemsReturnedMinusContractBalance)
			/*
			 *  Total Deductions = Admin/Return Fees + Credits/Discounts
			 */
			.addCalculatedField(PDFieldName.totalDeductions, Expr.adminFeesPlusCredits)
			/*
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(PDFieldName.creditBalance, Expr.amtReturnedMinusDeductions));
	}

	private void applyCredit() {
		addNode(new ChoiceNode(Id.apply_credit_choice, Desc.apply_credit_choice, PDFieldName.applyCredit)
			.addChoice(PDFieldName.newContract, PDFieldName.newContract, Id.new_contract_note)
			.addChoice(PDFieldName.existingContract, PDFieldName.existingContract, Id.existing_contract_note)
			.addChoice(PDFieldName.refund, PDFieldName.refund, Id.refund_request_note)
			.addChoice(PDFieldName.donation, PDFieldName.donation, Id.plot_fmv_1));

		addNode(new NoteChecksABoxNode(Id.new_contract_note,
				Id.parties_avail_choice, PDFieldName.newExistingContract,
				Desc.new_contract_note));

		addNode(new NoteChecksABoxNode(Id.existing_contract_note,
				Id.parties_avail_choice, PDFieldName.newExistingContract,
				Desc.existing_contract_note));

		addNode(new NoteNode(Id.refund_request_note, Id.parties_avail_choice,
				Desc.refund_request_note));

		addNode(new FieldsNode(Id.plot_fmv_1, Id.parties_avail_choice,
				Desc.plot_fmv)
			.addField(Field.donationAmount)
			.addField(new HiddenField(PDFieldName.donationLetter, "Yes")));
	}

	private void transfer() {
		addNode(new ChoiceNode(Id.paid_in_full_choice_2,
				Desc.paid_in_full_choice)
			.addChoice("Yes", Id.transfer_type_choice)
			.addChoice("No", Id.curr_contract_balance_3));

		addNode(Node.currContractBalance(Id.curr_contract_balance_3,
				Id.cash_receipt_2));
		addNode(Node.cashReceipt(Id.cash_receipt_2, Id.transfer_type_choice));

		addNode(new ChoiceNode(Id.transfer_type_choice, Desc.transfer_type_choice,
				PDFieldName.propertyAssignment)
			.addChoice(PDFieldName.transferOfOwnership, "Transfer of Ownership", Id.transfer_fee_note)
			.addChoice(PDFieldName.donation, "Donation", Id.plot_fmv_2)
			.addChoice(PDFieldName.releaseOfInterest, "Release of Interest", Id.transfer_fee_waived_note));

		transferTransfer();

		transferDonation();

		transferRelease();
	}

	private void transferTransfer() {
		addNode(new FeeNode(Id.transfer_fee_note, Id.name_7, 
				Desc.transfer_fee_note, PDFieldName.adminReturnFees,
				new BigDecimal("300.00")));

		addNode(Node.name(Id.name_7, Id.loc_7));
		addNode(Node.loc(Id.loc_7, Id.orig_contract_num_7));
		addNode(Node.origContractNum(Id.orig_contract_num_7, Id.assignee_info));

		addNode(new FieldsNode(Id.assignee_info, Id.reason_7, Desc.assignee_info)
			.addField(Field.assigneeNames)
			.addField(Field.assigneeAddress)
			.addField(Field.assigneePhone)
			.addField(Field.assigneeEmail));

		addNode(Node.reason(Id.reason_7, Id.calc_7));

		addNode(new CalculationNode(Id.calc_7, Id.parties_avail_choice)
			.addCalculatedField("Credit/Balance", new RefExpr(
					"Admin/Return Fees")));
	}

	private void transferDonation() {
		addNode(new FieldsNode(Id.plot_fmv_2, Id.name_8, Desc.plot_fmv)
			.addField(Field.donationAmount)
			.addField(new HiddenField(PDFieldName.adminReturnFees, "0.00"))
			.addField(new HiddenField(PDFieldName.donationLetter, "Yes")));

		addNode(Node.name(Id.name_8, Id.loc_8));
		addNode(Node.loc(Id.loc_8, Id.orig_contract_num_8));
		addNode(Node.origContractNum(Id.orig_contract_num_8, Id.reason_8));
		addNode(Node.reason(Id.reason_8, Id.parties_avail_choice));
	}

	private void transferRelease() {
		addNode(new FeeNode(Id.transfer_fee_waived_note, Id.name_9,
				Desc.transfer_fee_waived_note, PDFieldName.adminReturnFees,
				new BigDecimal("0.00")));

		addNode(Node.name(Id.name_9, Id.loc_9));
		addNode(Node.loc(Id.loc_9, Id.orig_contract_num_9));
		addNode(Node.origContractNum(Id.orig_contract_num_9, Id.reason_9));
		addNode(Node.reason(Id.reason_9, Id.done));
	}
	
	private void partiesAvailable() {
		addNode(new ChoiceNode(Id.parties_avail_choice, Desc.parties_avail_choice)
			.addChoice("Yes", Id.done)
			.addChoice("No", Id.party_present_choice));

		addNode(new ChoiceNode(Id.party_present_choice, Desc.party_present_choice)
			.addChoice("Party(s) to contract", Id.reason_party_unavail_choice)
			.addChoice("Other", Id.consult_ad_note));

		addNode(new NoteNode(Id.consult_ad_note, Id.done, Desc.consult_ad_note));

		reasonPartyUnavail();
	}

	private void reasonPartyUnavail() {
		addNode(new ChoiceNode(Id.reason_party_unavail_choice,
				Desc.reason_party_unavail_choice)
			.addChoice("Death", Id.cfcs_burial_choice)
			.addChoice("Relocation", Id.relocation_choice)
			.addChoice("Can't locate", Id.reasonable_effort_choice));

		addNode(new ChoiceNode(Id.cfcs_burial_choice, Desc.cfcs_burial_choice)
			.addChoice("Yes", Id.burial_evidence_note)
			.addChoice("No", Id.death_cert_note));

		addNode(new NoteChecksABoxNode(Id.burial_evidence_note, Id.done,
				PDFieldName.evidenceOfBurial, Desc.burial_evidence_note));

		addNode(new NoteChecksABoxNode(Id.death_cert_note, Id.done,
				PDFieldName.deathCertificate, Desc.death_cert_note));

		addNode(new ChoiceNode(Id.relocation_choice, Desc.relocation_choice)
			.addChoice("Yes", Id.notary_sig_note)
			.addChoice("No", Id.notarized_release_note));

		addNode(new NoteChecksABoxNode(Id.notary_sig_note, Id.done,
				PDFieldName.notarySignature, Desc.notary_sig_note));

		addNode(new NoteChecksABoxNode(Id.notarized_release_note, Id.done,
				PDFieldName.signedNotarizedRelease, Desc.notarized_release_note));

		addNode(new ChoiceNode(Id.reasonable_effort_choice,
				Desc.reasonable_effort_choice)
			.addChoice("Yes", Id.due_diligence_note)
			.addChoice("No", Id.loc_all_parties_note));

		addNode(new NoteChecksABoxNode(Id.due_diligence_note, Id.done,
				PDFieldName.statementOfDueDiligenceForm, Desc.due_diligence_note));

		addNode(new NoteNode(Id.loc_all_parties_note, Id.due_diligence_note,
				Desc.loc_all_parties_note));
	}
	
	// Description associated with each node in the decision tree
	private static class Desc {
		final static String prerequisites_note = "Before beginning the Change Order process, make sure you have the Patron’s original contract, and have performed all necessary verifications within HMIS (if applicable).";
		final static String change_order_type_choice = "What does the patron wish to do?";
		final static String curr_contract_value = "Please enter the current value of the contract.";
		final static String curr_contract_value_detail = "The current value is the total value of the original contract, subtracting any goods/services that have been fulfilled.";
		final static String curr_contract_balance = "Please enter the outstanding contract balance.";
		final static String curr_contract_balance_detail = "The balance is the amount that the patron still has to pay before the contract is paid in full.";
		final static String paid_in_full_choice = "Is the original contract paid in full?";
		final static String cash_receipt = "Since the contract must be paid in full for this type of transaction, a cash receipt reflecting the outstanding balance must be included with the change order packet.";
		final static String return_inv_choice = "Is cemetery inventory (plot, crypt, niche) to be returned?";
		final static String property_owner_choice = "Does the property Owner(s) differ from the contract Patron(s)?";
		final static String property_owner_sig_note = "Since a disinterment is subject to the discretion of the property owners, they must be the signatory(s) on the change order.";
		final static String disint_type_choice = "Please select the type of disinterment that is to occur:";
		final static String disint_fee = "A disinterment fee applies to this transaction.";
		final static String name = "Please enter the names of all patrons that were parties to the original contract:";
		final static String loc = "Please enter the cemetery, and the location of the interment space(s) that are subject to this change order:";
		final static String orig_contract_num = "Please enter the original contract number:";
		final static String disint_info = "Please provide the following information regarding the disinterment:";
		final static String reason = "Please provide a brief description of the reason for this transaction:";
		final static String transfer_type_choice = "Please select the type of transfer that the Patron wishes to perform:";
		final static String transfer_fee_note = "A processing fee applies for any property transfer to a new owner.";
		final static String transfer_fee_waived_note = "No transfer fee applies for a release of interest. Only those that are present are able to release their interest in the property.";
		final static String assignee_info = "Please provide the following information regarding the person to receive the property:";
		final static String orig_contract_date = "What is the date of the original contract?";
		final static String admin_fee_waived = "Note: No administration fee applies to this transaction.";
		final static String upgrade_exchg_choice = "Is the return/exchange part of the upgrade or even exchange?";
		final static String admin_fee = "Note: An administration fee applies to this transaction. The fee amount will be reflected on the Change Order Form.";
		final static String return_int_rights_choice = "Are all interment rights purchased to be returned?";
		final static String return_goods_choice = "Are all Goods & Services purchased to be returned?";
		final static String gift_amount = "Please enter the “Gift Amount,” as listed on the original contract:";
		final static String gift_amount_detail = "IMPORTANT: If Change Order is an upgrade, an even exchange, or if the purchase occurred within the last 30 days, enter \"0\" or leave as-is.";
		final static String items_returned = "Please list each of the items to be returned:";
		final static String credits_discounts = "Were any credits or discounts provided at the time of the original sale? If so, please list:";
		final static String apply_credit_choice = "How would the patron like any credits (if applicable) applied?";
		final static String new_contract_note = "Note: A copy of the new contract will need to be included with the change order packet.";
		final static String existing_contract_note = "Note: A copy of the existing contract will need to be included with the change order packet.";
		final static String refund_request_note = "Note: The refund will be processed, and a check will be sent to the patron subject to any conditions listed on the original contract or return policy.";
		final static String plot_fmv = "Note: A letter will be provided to the patron as evidence of the donation for tax purposes.";
		final static String parties_avail_choice = "Are all parties to the original contract available to sign?";
		final static String party_present_choice = "If all parties to the original contract are not currently available to sign the change order form, who is present?";
		final static String consult_ad_note = "Note: Because no parties to the original contract are present, this change order is subject to management approval, as appropriate rules of succession must be followed. Please consult your manager before processing the change order packet.";
		final static String reason_party_unavail_choice = "Why are other parties to the original contract unavailable?";
		final static String cfcs_burial_choice = "Are all the deceased buried in a CFCS cemetery?";
		final static String relocation_choice = "Will out-of-town parties be signing Change Order?";
		final static String reasonable_effort_choice = "Have reasonable efforts been made to locate party(s)?";
		final static String burial_evidence_note = "Note: Evidence of burial(s) must be included with the change order packet.";
		final static String death_cert_note = "Note: Death certificates for any deceased party(s) must be included with the change order packet.";
		final static String notary_sig_note = "Note: The signatures of out-of-town signatory(s) must be notarized in the area provided on the change order form.";
		final static String notarized_release_note = "Note: A signed, notarized release of interest (change order) form must be completed separately, and included with this change order packet.";
		final static String due_diligence_note = "Note: A signed “Statement of Due Diligence” form must be included with the change order packet.";
		final static String loc_all_parties_note = "Note: Efforts must be made to locate all parties to the original contract!";
		final static String done = "PROCESS COMPLETE!";
		final static String done_detail = "Your change order form is ready to be printed. You can access this form at any time through the main menu.";
	}

	private static class Expr {
		static final NumExpr zero = new NumExpr(BigDecimal.ZERO);

		static final NumExpr twentyPercent = new NumExpr(new BigDecimal("0.20"));
		
		static final BinaryExpr amtReturnedMinusDeductions = new BinaryExpr(
			new RefExpr(PDFieldName.totalToBeReturned),
			new RefExpr(PDFieldName.totalDeductions),
			Operators.SUBTRACT);
		
		static final BinaryExpr twentyPercentOfContractAmount = new BinaryExpr(
			new RefExpr(PDFieldName.contractAmount),
			Expr.twentyPercent,
			Operators.MULTIPLY);
		
		// this condition satisfied if admin fee was waived (that is, fee == 0.00)
		static final Condition adminReturnFeesCondition = new Condition() {
			@Override
			public boolean isSatisfied(FilledFormFields filledFormFields) {
				try {
					// return true if (fee == 0.00), false otherwise
					String adminReturnFeesVal = filledFormFields
							.getFieldValue(PDFieldName.adminReturnFees);
					BigDecimal returnFeeVal = new BigDecimal(adminReturnFeesVal);
					return (returnFeeVal.compareTo(BigDecimal.ZERO) == 0);
				} catch (RuntimeException e) {
					// field not filled (null); means the fee wasn't waived.
					return false;
				}
			}
		};

		/**
		 * <pre>
		 * if (Field.adminReturnFees == 0.00)
		 *     value -> 0.00
		 * else
		 *     value -> contractAmount * 0.20
		 * </pre>
		 */
		static final ConditionalExpr conditionalAdminReturnFees = new ConditionalExpr(
				Expr.zero,
				twentyPercentOfContractAmount,
				adminReturnFeesCondition);

		static final nAryExpr sumItemsReturned = new nAryExpr(Operators.ADD)
			.addExpr(new RefExpr(PDFieldName.extendedPrice1))
			.addExpr(new RefExpr(PDFieldName.extendedPrice2))
			.addExpr(new RefExpr(PDFieldName.extendedPrice3))
			.addExpr(new RefExpr(PDFieldName.extendedPrice4))
			.addExpr(new RefExpr(PDFieldName.extendedPrice5));

		static final RefExpr contractBalance = new RefExpr(PDFieldName.contractBalance);

		static final BinaryExpr contractAmountMinusBalanceExpr = new BinaryExpr(
			new RefExpr(PDFieldName.contractAmount),
			contractBalance,
			Operators.SUBTRACT);

		static final BinaryExpr sumItemsReturnedMinusContractBalance = new BinaryExpr(
			sumItemsReturned,
			contractBalance,
			Operators.SUBTRACT);

		static final RefExpr adminReturnFees = new RefExpr(PDFieldName.adminReturnFees);

		static final BinaryExpr adminFeesPlusCredits = new BinaryExpr(
			adminReturnFees,
			new RefExpr(PDFieldName.creditsDiscounts),
			Operators.ADD);
	}

	// Field definitions
	private static class Field {
		private static final String[] cemeteries = new String[] {
				"St. Mary Cemetery", "Calvary Cemetery", "All Souls Cemetery",
				"George L. Klumpp Chapel of Flowers", "Misc./Parish Cemetery" };
		private final static boolean required = true;
		final static TextField names = new TextField(PDFieldName.names, required, "Name(s)");
		final static TextField address = new TextField(PDFieldName.address, required, "Address");
		final static TextField phone = new TextField(PDFieldName.phone, required, "Phone");
		final static EmailField email = new EmailField(PDFieldName.email, required, "Email");
		final static SelectField cemetery = new SelectField(PDFieldName.cemetery, required, "Cemetery/Funeral Home", cemeteries);
		final static TextField location = new TextField(PDFieldName.location, required, "Plot Location");
		final static TextField origContractNum = new TextField(PDFieldName.origContractNum, required, "Original Contract Number");
		final static NumberField contractAmount = new NumberField(PDFieldName.contractAmount, required, "Current Contract Value");
		final static NumberField contractBalance = new NumberField(PDFieldName.contractBalance, required, "Current Contract Balance");
		final static TextAreaField reason = new TextAreaField(PDFieldName.reason, required, "Reason for Change Order");
		final static NumberField giftAmount = new NumberField(PDFieldName.giftAmount, required, "Gift Amount");
		final static NumberField creditsDiscounts = new NumberField(PDFieldName.creditsDiscounts, required, "Credits & Discounts to be deducted");
		final static NumberField donationAmount = new NumberField(PDFieldName.donationAmount, required, "Donation");
		final static TextField assigneeNames = new TextField(PDFieldName.assigneeNames, required, "Name(s)");
		final static TextField assigneeAddress = new TextField(PDFieldName.assigneeAddress, required, "Address");
		final static TextField assigneePhone = new TextField(PDFieldName.assigneePhone, required, "Phone");
		final static EmailField assigneeEmail = new EmailField(PDFieldName.assigneeEmail, required, "Email");
		final static TextField decedents = new TextField(PDFieldName.decedents, required, "Decedent(s)");
		final static TextField placeOfFinalDisposition = new TextField(PDFieldName.placeOfFinalDisposition, required, "Place of Final Disposition");
		final static SelectField cfcsReIntermentCemetery = new SelectField(PDFieldName.cfcsReIntermentCemetery, required, "Re-interment Cemetery", cemeteries);
		final static TextField cfcsReIntermentLocation = new TextField(PDFieldName.cfcsReIntermentLocation, required, "CFCS Re-Interment Location");
	}
	
	// Identifier of each node in the Change Order Form decision tree
	private static class Id {
		final static String prerequisites_note = "prerequisites_note";
		final static String change_order_type_choice = "change_order_type_choice";
		final static String curr_contract_value = "curr_contract_value";
		final static String curr_contract_balance_1 = "curr_contract_balance_1";
		final static String curr_contract_balance_2 = "curr_contract_balance_2";
		final static String curr_contract_balance_3 = "curr_contract_balance_3";
		final static String paid_in_full_choice_1 = "paid_in_full_choice_1";
		final static String paid_in_full_choice_2 = "paid_in_full_choice_2";
		final static String cash_receipt_1 = "cash_receipt_1";
		final static String cash_receipt_2 = "cash_receipt_2";
		final static String return_inv_choice = "return_inv_choice";
		final static String property_owner_choice = "property_owner_choice";
		final static String property_owner_sig_note = "property_owner_sig_note";
		final static String disint_type_choice = "disint_type_choice";
		final static String disint_fee_1 = "disint_fee_1";
		final static String disint_fee_2 = "disint_fee_2";
		final static String disint_fee_3 = "disint_fee_3";
		final static String disint_fee_4 = "disint_fee_4";
		final static String name_1 = "name_1";
		final static String name_2 = "name_2";
		final static String name_3 = "name_3";
		final static String name_4 = "name_4";
		final static String name_5 = "name_5";
		final static String name_6 = "name_6";
		final static String name_7 = "name_7";
		final static String name_8 = "name_8";
		final static String name_9 = "name_9";
		final static String loc_1 = "loc_1";
		final static String loc_2 = "loc_2";
		final static String loc_3 = "loc_3";
		final static String loc_4 = "loc_4";
		final static String loc_5 = "loc_5";
		final static String loc_6 = "loc_6";
		final static String loc_7 = "loc_7";
		final static String loc_8 = "loc_8";
		final static String loc_9 = "loc_9";
		final static String orig_contract_num_1 = "orig_contract_num_1";
		final static String orig_contract_num_2 = "orig_contract_num_2";
		final static String orig_contract_num_3 = "orig_contract_num_3";
		final static String orig_contract_num_4 = "orig_contract_num_4";
		final static String orig_contract_num_5 = "orig_contract_num_5";
		final static String orig_contract_num_6 = "orig_contract_num_6";
		final static String orig_contract_num_7 = "orig_contract_num_7";
		final static String orig_contract_num_8 = "orig_contract_num_8";
		final static String orig_contract_num_9 = "orig_contract_num_9";
		final static String disint_info = "disint_info";
		final static String reason_1 = "reason_1";
		final static String reason_2 = "reason_2";
		final static String reason_3 = "reason_3";
		final static String reason_4 = "reason_4";
		final static String reason_5 = "reason_5";
		final static String reason_6 = "reason_6";
		final static String reason_7 = "reason_7";
		final static String reason_8 = "reason_8";
		final static String reason_9 = "reason_9";
		final static String calc_1 = "calc_1";
		final static String calc_2 = "calc_2";
		final static String calc_3 = "calc_3";
		final static String calc_4 = "calc_4";
		final static String calc_5 = "calc_5";
		final static String calc_6 = "calc_6";
		final static String calc_7 = "calc_7";
		final static String transfer_type_choice = "transfer_type_choice";
		final static String transfer_fee_note = "transfer_fee_note";
		final static String transfer_fee_waived_note = "transfer_fee_waived_note";
		final static String assignee_info = "assignee_info";
		final static String orig_contract_date_1 = "orig_contract_date_1";
		final static String orig_contract_date_2 = "orig_contract_date_2";
		final static String admin_fee_waived_1 = "admin_fee_waived_1";
		final static String admin_fee_waived_2 = "admin_fee_waived_2";
		final static String upgrade_exchg_choice_1 = "upgrade_exchg_choice_1";
		final static String upgrade_exchg_choice_2 = "upgrade_exchg_choice_2";
		final static String admin_fee_1 = "admin_fee_1";
		final static String admin_fee_2 = "admin_fee_2";
		final static String return_int_rights_choice = "return_int_rights_choice";
		final static String return_goods_choice = "return_goods_choice";
		final static String gift_amount = "gift_amount";
		final static String items_returned_1 = "items_returned_1";
		final static String items_returned_2 = "items_returned_2";
		final static String credits_discounts_1 = "credits_discounts_1";
		final static String credits_discounts_2 = "credits_discounts_2";
		final static String apply_credit_choice = "apply_credit_choice";
		final static String new_contract_note = "new_contract_note";
		final static String existing_contract_note = "existing_contract_note";
		final static String refund_request_note = "refund_request_note";
		final static String plot_fmv_1 = "plot_fmv_1";
		final static String plot_fmv_2 = "plot_fmv_2";
		final static String parties_avail_choice = "parties_avail_choice";
		final static String party_present_choice = "party_present_choice";
		final static String consult_ad_note = "consult_ad_note";
		final static String reason_party_unavail_choice = "reason_party_unavail_choice";
		final static String cfcs_burial_choice = "cfcs_burial_choice";
		final static String relocation_choice = "relocation_choice";
		final static String reasonable_effort_choice = "reasonable_effort_choice";
		final static String burial_evidence_note = "burial_evidence_note";
		final static String death_cert_note = "death_cert_note";
		final static String notary_sig_note = "notary_sig_note";
		final static String notarized_release_note = "notarized_release_note";
		final static String due_diligence_note = "due_diligence_note";
		final static String loc_all_parties_note = "loc_all_parties_note";
		final static String done = "done";
	}

	// Functions for commonly used node patterns
	private static class Node {
		static FieldsNode currContractBalance(String id, String idNext) {
			return new FieldsNode(id, idNext, Desc.curr_contract_balance)
				.setDetailDescription(Desc.curr_contract_balance_detail)
				.addField(Field.contractBalance);
		}

		static NoteChecksABoxNode cashReceipt(String id, String idNext) {
			return new NoteChecksABoxNode(id, idNext, PDFieldName.cashReceipt,
					ChangeOrderForm.Desc.cash_receipt);
		}

		static ChoiceNode paidInFullChoice(String id, String idYes, String idNo) {
			return new ChoiceNode(id, "Is the original contract paid in full?")
				.addChoice("Yes", idYes)
				.addChoice("No", idNo);
		}

		static FeeNode disintFee(String id, BigDecimal fee) {
			return new FeeNode(id, ChangeOrderForm.Id.name_1, ChangeOrderForm.Desc.disint_fee,
					PDFieldName.adminReturnFees, fee);
		}

		static FieldsNode name(String id, String idNext) {
			return new FieldsNode(id, idNext, Desc.name)
				.addField(Field.names)
				.addField(Field.address)
				.addField(Field.phone)
				.addField(Field.email);
		}

		static FieldsNode loc(String id, String idNext) {
			return new FieldsNode(id, idNext, Desc.loc)
				.addField(Field.cemetery)
				.addField(Field.location);
		}

		static FieldsNode origContractNum(String id, String idNext) {
			return new FieldsNode(id, idNext, Desc.orig_contract_num)
				.addField(Field.origContractNum);
		}

		static FieldsNode reason(String id, String idNext) {
			return new FieldsNode(id, idNext, Desc.reason)
				.addField(Field.reason);
		}

		static ChoiceNode origContractDate(String id, String idWithin30Days, String idOutside30Days) {
			return new ChoiceNode(id, ChangeOrderForm.Desc.orig_contract_date)
				.addChoice("Within 30 days", idWithin30Days)
				.addChoice("Outside 30 days", idOutside30Days);
		}

		static FeeNode adminFeeWaived(String id, String idNext) {
			return new FeeNode(id, idNext, ChangeOrderForm.Desc.admin_fee_waived, 
					PDFieldName.adminReturnFees, new BigDecimal("0.00"));
		}

		static FieldsNode creditsDiscounts(String id, String idNext) {
			return new FieldsNode(id, idNext, Desc.credits_discounts)
				.addField(Field.creditsDiscounts);
		}

		static FieldTableNode itemsReturned(String id, String idNext) {
			return new FieldTableNode(id, ChangeOrderForm.Desc.items_returned, 5, idNext)
				.addColumn("Item Code", PDFieldName.itemCodeBase, TextField.class, "")
				.addColumn("Description", PDFieldName.descriptionBase, TextField.class, "")
				.addColumn("Extended Price (including tax)", PDFieldName.extendedPriceBase, NumberField.class, "0.00");
		}
	}

	// Names of fillable form fields in the Change Order form PDF
	private static class PDFieldName {
		final static String changeOrderType = "Change Order Type";
		final static String return_ = "Return";
		final static String assignment = "Assignment";
		final static String disinterment = "Disinterment";
		final static String names = "Names";
		final static String address = "Address";
		final static String phone = "Phone";
		final static String email = "Email";
		final static String cemetery = "Cemetery";
		final static String location = "Location";
		final static String origContractNum = "Orig Contract";
		final static String contractAmount = "Contract Amount";
		final static String contractBalance = "Contract Balance";
		final static String reason = "Reason";
		final static String itemCodeBase = "Item Code ";
		final static String descriptionBase = "Description ";
		final static String extendedPriceBase = "Extended Price ";
		final static String extendedPrice1 = "Extended Price 1";
		final static String extendedPrice2 = "Extended Price 2";
		final static String extendedPrice3 = "Extended Price 3";
		final static String extendedPrice4 = "Extended Price 4";
		final static String extendedPrice5 = "Extended Price 5";
		final static String giftAmount = "Gift Amount";
		final static String adminReturnFees = "Admin/Return Fees";
		final static String creditsDiscounts = "Credits/Discounts";
		final static String totalToBeReturned = "Total to be Returned";
		final static String applyCredit = "Apply Credit";
		final static String newContract = "New Contract";
		final static String existingContract = "Existing Contract";
		final static String refund = "Refund";
		final static String totalDeductions = "Total Deductions";
		final static String creditBalance = "Credit/Balance";
		final static String propertyAssignment = "Property Assignment";
		final static String transferOfOwnership = "Transfer of Ownership";
		final static String releaseOfInterest = "Release of Interest";
		final static String donation = "Donation";
		final static String donationAmount = "Donation Amount";
		final static String assigneeNames = "Assignee Names";
		final static String assigneeAddress = "Assignee Address";
		final static String assigneePhone = "Assignee Phone";
		final static String assigneeEmail = "Assignee Email";
		final static String decedents = "Decedents";
		final static String placeOfFinalDisposition = "Place of Final Disposition";
		final static String cfcsReIntermentLocation = "CFCS ReInterment Location";
		final static String cfcsReIntermentCemetery = "CFCS ReInterment Cemetery";
		final static String notarySignature = "Notary Signature";
		final static String originalContract = "Original Contract";
		final static String deathCertificate = "Death Certificate";
		final static String cashReceipt = "Cash Receipt";
		final static String signedNotarizedRelease = "Signed/Notarized Release";
		final static String donationLetter = "Donation Letter";
		final static String statementOfDueDiligenceForm = "Statement of Due Diligence Form";
		final static String evidenceOfBurial = "Evidence of Burial";
		final static String newExistingContract = "New/Existing Contract";
	}
}
