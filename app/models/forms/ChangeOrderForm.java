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
import models.tree.fields.FieldTableNode;
import models.tree.fields.FieldType;
import models.tree.fields.FieldsNode;
import models.tree.ChoiceNode;
import models.tree.FeeNode;
import models.tree.NoteChecksABoxNode;
import models.tree.NoteNode;

public class ChangeOrderForm extends CMSForm {
	private static ChangeOrderForm instance;

	public static ChangeOrderForm getInstance() {
		if (instance == null) {
			instance = new ChangeOrderForm();
		}
		return instance;
	}

	private ChangeOrderForm() {
		// file name associated with this form
		super("Change_Order_Form.pdf");

		// decision tree definition
		root = new NoteNode(Id.prerequisites_note,
				Id.change_order_type_choice, "a note.");
		addNode(root);
		
		addNode(new ChoiceNode(Id.change_order_type_choice,
				"What does the patron wish to do?", "Change Order Type")
			.addChoice("Return/Exch", "Return", Id.curr_contract_value)
			.addChoice("Transfer", "Assignment", Id.paid_in_full_choice_2)
			.addChoice("Disinterment", "Disinterment", Id.paid_in_full_choice_1));

		returnExchg();

		transfer();

		disinterment();

		partiesAvailable();

		addNode(new NoteNode(Id.done, "none", "End of form."));
	}

	private void disinterment() {
		addNode(new ChoiceNode(Id.paid_in_full_choice_1,
				"Is the original contract paid in full?")
			.addChoice("Yes", Id.property_owner_choice)
			.addChoice("No", Id.curr_contract_balance_1));

		addNode(new FieldsNode(Id.curr_contract_balance_1, Id.cash_receipt_1)
			.addField("Current Contract Balance", "Contract Balance",
				FieldType.NUMBER));

		addNode(new NoteChecksABoxNode(Id.cash_receipt_1,
				Id.property_owner_choice, Field.cashReceipt,
				Desc.cash_receipt_1));

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

		addNode(new FeeNode(Id.disint_fee_1, Id.name_1, Desc.disint_fee_1,
				Field.adminReturnFees, new BigDecimal("1950.00")));

		addNode(new FeeNode(Id.disint_fee_2, Id.name_1, Desc.disint_fee_2,
				Field.adminReturnFees, new BigDecimal("1350.00")));

		addNode(new FeeNode(Id.disint_fee_3, Id.name_1, Desc.disint_fee_3,
				Field.adminReturnFees, new BigDecimal("750.00")));

		addNode(new FeeNode(Id.disint_fee_4, Id.name_1, Desc.disint_fee_4,
				Field.adminReturnFees, new BigDecimal("500.00")));

		disintermentInfo();
	}

	private void disintermentInfo() {
		addNode(new FieldsNode(Id.name_1, Id.loc_1)
			.addField("Patron Name", Field.names, FieldType.TEXT));

		addNode(new FieldsNode(Id.loc_1, Id.orig_contract_num_1 )
			.addField("Cemetery", Field.cemetery, FieldType.TEXT)
			.addField("Location", Field.location, FieldType.TEXT));

		addNode(new FieldsNode(Id.orig_contract_num_1, Id.disint_info)
			.addField("Original Contract Number", Field.origContractNum, FieldType.TEXT));

		addNode(new FieldsNode(Id.disint_info, Id.reason_1)
			.addField("Decedent(s)", Field.decedents, FieldType.TEXT)
			.addField("Place of Final Disposition", Field.placeOfFinalDisposition, FieldType.TEXT)
			.addField("CFCS Re-Interment Location", Field.cfcsReIntermentLocation, FieldType.TEXT)
			.addField("Cemetery", Field.cfcsReIntermentCemetery, FieldType.TEXT));

		addNode(new FieldsNode(Id.reason_1, Id.calc_1)
			.addField("Reason for Change Order", Field.reason, FieldType.TEXT));

		addNode(new CalculationNode(Id.calc_1, Id.parties_avail_choice)
			.addCalculatedField("Credit/Balance", new RefExpr("Admin/Return Fees")));
	}

	private void returnExchg() {
		addNode(new FieldsNode(Id.curr_contract_value,
				Id.curr_contract_balance_2)
			.addField("Current contract value", "Contract Amount",
					FieldType.NUMBER));

		addNode(new FieldsNode(Id.curr_contract_balance_2, Id.return_inv_choice)
			.addField("Current Contract Balance", "Contract Balance",
					FieldType.NUMBER));

		addNode(new ChoiceNode(Id.return_inv_choice,
				"Is inventory to be returned?")
			.addChoice("Yes", Id.orig_contract_date_1)
			.addChoice("No", Id.orig_contract_date_2));

		returnInventoryYes();

		returnInventoryNo();

		applyCredit();
	}

	private void returnInventoryYes() {
		addNode(new ChoiceNode(Id.orig_contract_date_1, Desc.orig_contract_date_1)
			.addChoice("Within 30 days", Id.admin_fee_waived_1)
			.addChoice("Outside 30 days", Id.upgrade_exchg_choice_1));

		addNode(new FeeNode(Id.admin_fee_waived_1, Id.return_int_rights_choice,
				Desc.admin_fee_waived_1, Field.adminReturnFees,
				new BigDecimal("0.00")));

		addNode(new ChoiceNode(Id.upgrade_exchg_choice_1, Desc.upgrade_exchg_choice_1)
			.addChoice("Yes", Id.admin_fee_waived_1)
			.addChoice("No", Id.admin_fee_1));

		addNode(new NoteNode(Id.admin_fee_1, Id.return_int_rights_choice,
				Desc.admin_fee_1));

		addNode(new ChoiceNode(Id.return_int_rights_choice, Desc.return_int_rights_choice)
			.addChoice("Yes-Contract date before 8/1/12", Id.name_2)
			.addChoice("Yes-Contract date after 8/1/12", Id.name_3)
			.addChoice("No", Id.name_4));

		returnIntermentRightsYes1();

		returnIntermentRightsYes2();

		returnIntermentRightsNo();
	}

	private void returnIntermentRightsYes1() {
		addNode(new FieldsNode(Id.name_2, Id.loc_2)
			.addField("Patron Name", Field.names, FieldType.TEXT));

		addNode(new FieldsNode(Id.loc_2, Id.orig_contract_num_2)
			.addField("Cemetery", Field.cemetery, FieldType.TEXT)
			.addField("Location", Field.location, FieldType.TEXT));

		addNode(new FieldsNode(Id.orig_contract_num_2, Id.reason_2)
			.addField("Original Contract Number", Field.origContractNum, FieldType.TEXT));

		addNode(new FieldsNode(Id.reason_2, Id.calc_2)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));
		
		addNode(new CalculationNode(Id.calc_2, Id.apply_credit_choice)
			/*
			 *  Total to be Returned = Contract Amount - Contract Balance
			 */
			.addCalculatedField(Field.totalToBeReturned,
				Expr.contractAmountMinusBalanceExpr)
			/* 
			 * If fee applies, then
			 * Admin/Return Fees = Contract Amount * 20%
			 */
			.addCalculatedField(Field.adminReturnFees,
				Expr.conditionalAdminReturnFees)
			/*
			 *  Total Deductions = Admin/Return Fees
			 */
			.addCalculatedField(Field.totalDeductions, Expr.adminReturnFeesExpr)
			/*
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(Field.creditBalance,
				Expr.amtReturnedMinusDeductions));
	}

	private void returnIntermentRightsYes2() {
		addNode(new FieldsNode(Id.name_3, Id.loc_3)
			.addField("Patron Name", Field.names, FieldType.TEXT));

		addNode(new FieldsNode(Id.loc_3, Id.orig_contract_num_3)
			.addField("Cemetery", Field.cemetery, FieldType.TEXT)
			.addField("Location", Field.location, FieldType.TEXT));

		addNode(new FieldsNode(Id.orig_contract_num_3, Id.gift_amount)
			.addField("Original Contract Number", Field.origContractNum,
					FieldType.TEXT));

		addNode(new FieldsNode(Id.gift_amount, Id.reason_3)
			.addField("Gift Amount", Field.giftAmount, FieldType.NUMBER));

		addNode(new FieldsNode(Id.reason_3, Id.calc_3)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));
		
		addNode(new CalculationNode(Id.calc_3, Id.apply_credit_choice)
			/* 
			 * Total to be Returned = Contract Amt - Contract Bal - Gift Amt
			 */
			.addCalculatedField(Field.totalToBeReturned,
				new BinaryExpr(
					Expr.contractAmountMinusBalanceExpr,
					new RefExpr(Field.giftAmount),
					Operators.SUBTRACT))
			/*
			 * If fee applies, then
			 * Admin/Return Fees = Contract Amount * 0.20
			 */
			.addCalculatedField(Field.adminReturnFees,
				Expr.conditionalAdminReturnFees)
			/*
			 *  Total Deductions = Admin/Return Fees
			 */
			.addCalculatedField(Field.totalDeductions,
					Expr.adminReturnFeesExpr)
			/* 
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(Field.creditBalance,
				Expr.amtReturnedMinusDeductions));
	}

	private void returnIntermentRightsNo() {
		addNode(new FieldsNode(Id.name_4, Id.loc_4)
			.addField("Patron Name", Field.names, FieldType.TEXT));

		addNode(new FieldsNode(Id.loc_4, Id.orig_contract_num_4)
			.addField("Cemetery", Field.cemetery, FieldType.TEXT)
			.addField("Location", Field.location, FieldType.TEXT));

		addNode(new FieldsNode(Id.orig_contract_num_4, Id.items_returned_1)
			.addField("Original Contract Number", Field.origContractNum,
					FieldType.TEXT));

		addNode(new FieldTableNode(Id.items_returned_1, Desc.items_returned_1,
				5, Id.credits_discounts_1)
			.addColumn("Item Code", Field.itemCodeBase, FieldType.TEXT)
			.addColumn("Description", Field.descriptionBase, FieldType.TEXT)
			.addColumn("Extended Price (including tax)", Field.extendedPriceBase, FieldType.NUMBER));

		addNode(new FieldsNode(Id.credits_discounts_1, Id.reason_4)
			.addField("Credits & Discounts to be deducted",
					Field.creditsDiscounts, FieldType.NUMBER));

		addNode(new FieldsNode(Id.reason_4, Id.calc_4)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));

		addNode(new CalculationNode(Id.calc_4, Id.apply_credit_choice)
			/*
			 *  Total to be Returned = Items to be Returned - Contract Balance
			 */
			.addCalculatedField(Field.totalToBeReturned, Expr.sumItemsReturnedMinusContractBalance)
			/* 
			 * If admin fee applies, then
			 * Admin/Return Fees = Items to be Returned * 20%
			 */
			.addCalculatedField(Field.adminReturnFees, new ConditionalExpr(
					Expr.zero,
					new BinaryExpr(
						Expr.sumItemsReturned,
						Expr.twentyPercent,
						Operators.MULTIPLY),
					Expr.adminReturnFeesCondition))
			/*
			 *  Total Deductions = Admin/Return Fees + Credits/Discounts
			 */
			.addCalculatedField(Field.totalDeductions, Expr.adminFeesPlusCredits)
			/*
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(Field.creditBalance, Expr.amtReturnedMinusDeductions));
	}

	private void returnInventoryNo() {
		addNode(new ChoiceNode(Id.orig_contract_date_2, Desc.orig_contract_date_2)
			.addChoice("Within 30 days", Id.admin_fee_waived_2)
			.addChoice("Outside 30 days", Id.upgrade_exchg_choice_2));

		addNode(new FeeNode(Id.admin_fee_waived_2, Id.return_goods_choice,
				Desc.admin_fee_waived_2, Field.adminReturnFees,
				new BigDecimal("0.00")));

		addNode(new ChoiceNode(Id.upgrade_exchg_choice_2, Desc.upgrade_exchg_choice_2)
			.addChoice("Yes", Id.admin_fee_waived_2)
			.addChoice("No", Id.admin_fee_2));

		addNode(new NoteNode(Id.admin_fee_2, Id.return_goods_choice,
				Desc.admin_fee_2));

		addNode(new ChoiceNode(Id.return_goods_choice, Desc.return_goods_choice)
			.addChoice("Yes", Id.name_5)
			.addChoice("No", Id.name_6));

		returnGoodsYes();

		returnGoodsNo();
	}

	private void returnGoodsYes() {
		addNode(new FieldsNode(Id.name_5, Id.loc_5)
			.addField("Patron Name", Field.names, FieldType.TEXT));

		addNode(new FieldsNode(Id.loc_5, Id.orig_contract_num_5)
			.addField("Cemetery", Field.cemetery, FieldType.TEXT)
			.addField("Location", Field.location, FieldType.TEXT));

		addNode(new FieldsNode(Id.orig_contract_num_5, Id.reason_5)
			.addField("Original Contract Number", Field.origContractNum, FieldType.TEXT));

		addNode(new FieldsNode(Id.reason_5, Id.calc_5)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));

		addNode(new CalculationNode(Id.calc_5, Id.apply_credit_choice)
			/*
			 *  Total to be Returned = Contract Amount - Contract Balance
			 */
			.addCalculatedField(Field.totalToBeReturned, Expr.contractAmountMinusBalanceExpr)
			/*
			 *  Total Deductions = Admin/Return Fees + Credits/Discounts
			 */
			.addCalculatedField(Field.totalDeductions, Expr.adminFeesPlusCredits)
			/*
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(Field.creditBalance, Expr.amtReturnedMinusDeductions));
	}

	private void returnGoodsNo() {
		addNode(new FieldsNode(Id.name_6, Id.loc_6)
			.addField("Patron Name", Field.names, FieldType.TEXT));

		addNode(new FieldsNode(Id.loc_6, Id.orig_contract_num_6)
			.addField("Cemetery", Field.cemetery, FieldType.TEXT)
			.addField("Location", Field.location, FieldType.TEXT));

		addNode(new FieldsNode(Id.orig_contract_num_6, Id.items_returned_2)
			.addField("Original Contract Number", Field.origContractNum,
					FieldType.TEXT));

		addNode(new FieldTableNode(Id.items_returned_2, Desc.items_returned_2,
				5, Id.credits_discounts_2)
			.addColumn("Item Code", Field.itemCodeBase, FieldType.TEXT)
			.addColumn("Description", Field.descriptionBase, FieldType.TEXT)
			.addColumn("Extended Price (including tax)", Field.extendedPriceBase, FieldType.NUMBER));

		addNode(new FieldsNode(Id.credits_discounts_2, Id.reason_6)
			.addField("Credits & Discounts to be deducted",
					Field.creditsDiscounts, FieldType.NUMBER));

		addNode(new FieldsNode(Id.reason_6, Id.calc_6)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));

		addNode(new CalculationNode(Id.calc_6, Id.apply_credit_choice)
			/*
			 *  Total to be Returned = Items to be Returned - Contract Balance
			 */
			.addCalculatedField(Field.totalToBeReturned, Expr.sumItemsReturnedMinusContractBalance)
			/*
			 *  Total Deductions = Admin/Return Fees + Credits/Discounts
			 */
			.addCalculatedField(Field.totalDeductions, Expr.adminFeesPlusCredits)
			/*
			 * Credit/Balance = Total to be Returned - Total Deductions
			 */
			.addCalculatedField(Field.creditBalance, Expr.amtReturnedMinusDeductions));
	}

	private void applyCredit() {
		addNode(new ChoiceNode(Id.apply_credit_choice, Desc.apply_credit_choice, Field.applyCredit)
			.addChoice("New Contract", Field.newContract, Id.new_contract_note)
			.addChoice("Old Contract", Field.existingContract, Id.existing_contract_note)
			.addChoice("Refund", Field.refund, Id.refund_request_note)
			.addChoice("Donation", Field.donation, Id.plot_fmv_1));

		addNode(new NoteChecksABoxNode(Id.new_contract_note,
				Id.parties_avail_choice, Field.newExistingContract,
				Desc.new_contract_note));

		addNode(new NoteChecksABoxNode(Id.existing_contract_note,
				Id.parties_avail_choice, Field.newExistingContract,
				Desc.existing_contract_note));

		addNode(new NoteNode(Id.refund_request_note, Id.parties_avail_choice,
				Desc.refund_request_note));

		// TODO: Make plot_fmv_1 check the Donation Letter box
		addNode(new FieldsNode(Id.plot_fmv_1, Id.parties_avail_choice)
			.addField("Donation", Field.donationAmount, FieldType.NUMBER));
	}

	private void transfer() {
		addNode(new ChoiceNode(Id.paid_in_full_choice_2,
				"Is the original contract paid in full?")
			.addChoice("Yes", Id.transfer_type_choice)
			.addChoice("No", Id.curr_contract_balance_3));

		addNode(new FieldsNode(Id.curr_contract_balance_3,
				Id.cash_receipt_2)
			.addField("Current Contract Balance", "Contract Balance",
					FieldType.NUMBER));

		addNode(new NoteChecksABoxNode(Id.cash_receipt_2,
				Id.transfer_type_choice, Field.cashReceipt,
				"Cash receipt reflecting balance."));

		addNode(new ChoiceNode(Id.transfer_type_choice, "Transfer Type: ",
				"Property Assignment")
			.addChoice("Transfer", "Transfer of Ownership", Id.transfer_fee_note)
			.addChoice("Donation", "Donation", Id.plot_fmv_2)
			.addChoice("Release", "Release of Interest", Id.transfer_fee_waived_note));

		transferTransfer();

		transferDonation();

		transferRelease();
	}

	private void transferTransfer() {
		addNode(new FeeNode(Id.transfer_fee_note, Id.name_7,
				"A processing fee applies for any property transfer to a new owner.",
				"Admin/Return Fees",
				new BigDecimal("300.00")));

		addNode(new FieldsNode(Id.name_7, Id.loc_7)
		.addField("Patron Name", "Names", FieldType.TEXT));

		addNode(new FieldsNode(Id.loc_7, Id.orig_contract_num_7)
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));

		addNode(new FieldsNode(Id.orig_contract_num_7, Id.assignee_info)
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));

		addNode(new FieldsNode(Id.assignee_info, Id.reason_7)
			.addField("Name", "Assignee Names", FieldType.TEXT)
			.addField("Address", "Assignee Address", FieldType.TEXT)
			.addField("Phone", "Assignee Phone", FieldType.TEXT)
			.addField("Email", "Assignee Email", FieldType.TEXT));

		addNode(new FieldsNode(Id.reason_7, Id.calc_7)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));

		addNode(new CalculationNode(Id.calc_7, Id.parties_avail_choice)
			.addCalculatedField("Credit/Balance", new RefExpr(
					"Admin/Return Fees")));
	}

	private void transferDonation() {
		addNode(new FieldsNode(Id.plot_fmv_2, Id.name_8)
			.addFilledField("Admin/Return Fees", "0")
			.addField("Donation", "Donation Amount", FieldType.NUMBER));

		addNode(new FieldsNode(Id.name_8, Id.loc_8)
			.addField("Patron Name", "Names", FieldType.TEXT));

		addNode(new FieldsNode(Id.loc_8, Id.orig_contract_num_8)
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));

		addNode(new FieldsNode(Id.orig_contract_num_8, Id.reason_8)
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));

		addNode(new FieldsNode(Id.reason_8, Id.parties_avail_choice)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));
	}

	private void transferRelease() {
		addNode(new FeeNode(Id.transfer_fee_waived_note, Id.name_9,
				"Fee waived, no addt'l sigs", "Admin/Return Fees",
				new BigDecimal("0.00")));

		addNode(new FieldsNode(Id.name_9, Id.loc_9)
			.addField("Patron Name", "Names", FieldType.TEXT));

		addNode(new FieldsNode(Id.loc_9, Id.orig_contract_num_9)
			.addField("Cemetery", "Cemetery", FieldType.TEXT)
			.addField("Location", "Location", FieldType.TEXT));

		addNode(new FieldsNode(Id.orig_contract_num_9, Id.reason_9)
			.addField("Original Contract Number", "Orig Contract",
					FieldType.TEXT));

		addNode(new FieldsNode(Id.reason_9, Id.done)
			.addField("Reason for Change Order", "Reason", FieldType.TEXT));
	}
	
	private void partiesAvailable() {
		addNode(new ChoiceNode(Id.parties_avail_choice,
				"Are all parties to the original contract available to sign?")
			.addChoice("Yes", Id.done)
			.addChoice("No", Id.party_present_choice));

		addNode(new ChoiceNode(Id.party_present_choice, "Who is present?")
			.addChoice("Party(s) to contract", Id.reason_party_unavail_choice)
			.addChoice("Other", Id.consult_ad_note));

		addNode(new NoteNode(Id.consult_ad_note, Id.done,
				"Consult A.D. for requirements."));

		reasonPartyUnavail();
	}

	private void reasonPartyUnavail() {
		addNode(new ChoiceNode(Id.reason_party_unavail_choice,
				"Why unavailable?")
			.addChoice("Death", Id.cfcs_burial_choice)
			.addChoice("Relocation", Id.relocation_choice)
			.addChoice("Can't locate", Id.reasonable_effort_choice));

		addNode(new ChoiceNode(Id.cfcs_burial_choice,
				"Are the deceased buried in a CFCS cemetery?")
			.addChoice("Yes", Id.burial_evidence_note)
			.addChoice("No", Id.death_cert_note));

		addNode(new NoteChecksABoxNode(Id.burial_evidence_note, Id.done,
				"Evidence of Burial", "Evidence of burial required."));

		addNode(new NoteChecksABoxNode(Id.death_cert_note, Id.done,
				"Death Certificate", "Death certificate required."));

		addNode(new ChoiceNode(Id.relocation_choice,
				"Will out-of-town parties be signing Change Order?")
			.addChoice("Yes", Id.notary_sig_note)
			.addChoice("No", Id.notarized_release_note));

		addNode(new NoteChecksABoxNode(Id.notary_sig_note, Id.done,
				"Notary Signature", "Notary signature required."));

		addNode(new NoteChecksABoxNode(Id.notarized_release_note, Id.done,
				"Signed/Notarized Release",
				"Signed, notarized release required."));

		addNode(new ChoiceNode(Id.reasonable_effort_choice,
				"Have reasonable efforts been made to locate party(s)?")
			.addChoice("Yes", Id.due_diligence_note)
			.addChoice("No", Id.loc_all_parties_note));

		addNode(new NoteChecksABoxNode(Id.due_diligence_note, Id.done,
				"Statement of Due Diligence Form",
				"Statement of due diligence form required."));

		addNode(new NoteNode(Id.loc_all_parties_note, Id.due_diligence_note,
				"Efforts must be made to locate all parties to the original "
				+ "contract!"));
	}
	
	private static class Expr {
		static final NumExpr zero = new NumExpr(BigDecimal.ZERO);

		static final NumExpr twentyPercent = new NumExpr(new BigDecimal("0.20"));
		
		static final BinaryExpr amtReturnedMinusDeductions = new BinaryExpr(
			new RefExpr(Field.totalToBeReturned),
			new RefExpr(Field.totalDeductions),
			Operators.SUBTRACT);
		
		static final BinaryExpr twentyPercentOfContractAmount = new BinaryExpr(
			new RefExpr(Field.contractAmount),
			Expr.twentyPercent,
			Operators.MULTIPLY);
		
		// this condition satisfied if admin fee was waived (that is, fee == 0.00)
		static final Condition adminReturnFeesCondition = new Condition() {
			@Override
			public boolean isSatisfied(FilledFormFields filledFormFields) {
				try {
					// return true if (fee == 0.00), false otherwise
					String adminReturnFees = filledFormFields
							.getFieldValue(Field.adminReturnFees);
					BigDecimal returnFeeVal = new BigDecimal(adminReturnFees);
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
			.addExpr(new RefExpr(Field.extendedPrice1))
			.addExpr(new RefExpr(Field.extendedPrice2))
			.addExpr(new RefExpr(Field.extendedPrice3))
			.addExpr(new RefExpr(Field.extendedPrice4))
			.addExpr(new RefExpr(Field.extendedPrice5));

		static final RefExpr contractBalance = new RefExpr(Field.contractBalance);

		static final BinaryExpr contractAmountMinusBalanceExpr = new BinaryExpr(
			new RefExpr(ChangeOrderForm.Field.contractAmount),
			contractBalance,
			Operators.SUBTRACT);

		static final BinaryExpr sumItemsReturnedMinusContractBalance = new BinaryExpr(
			sumItemsReturned,
			contractBalance,
			Operators.SUBTRACT);

		static final RefExpr adminReturnFeesExpr = new RefExpr(Field.adminReturnFees);

		static final BinaryExpr adminFeesPlusCredits = new BinaryExpr(
			adminReturnFeesExpr,
			new RefExpr(Field.creditsDiscounts),
			Operators.ADD);
	}

	// Identifier of each node in the Change Order Form decision tree
	@SuppressWarnings("unused")
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
		final static String transfer_donation = "transfer_donation";
		final static String transfer_fee_waived_note = "transfer_fee_waived_note";
		final static String assignee_info = "assignee_info";
		final static String orig_contract_date_1 = "orig_contract_date_1";
		final static String orig_contract_date_2 = "orig_contract_date_2";
		final static String admin_fee_waived_1 = "admin_fee_waived_1";
		final static String admin_fee_waived_2 = "admin_fee_waived_2";
		final static String admin_fee_waived_3 = "admin_fee_waived_3";
		final static String admin_fee_waived_4 = "admin_fee_waived_4";
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

	// Description associated with each node in the decision tree
	@SuppressWarnings("unused")
	private static class Desc {
		final static String prerequisites_note = "Before beginning the Change Order process, make sure you have the Patron’s original contract, and have performed all necessary verifications within HMIS (if applicable).";
		final static String change_order_type_choice = "What does the patron wish to do?";
		final static String curr_contract_value = "Is the original contract paid in full?";
		private final static String currContractBalance = "Please enter the outstanding contract balance.";
		final static String curr_contract_balance_1 = currContractBalance;
		final static String curr_contract_balance_2 = currContractBalance;
		final static String curr_contract_balance_3 = currContractBalance;
		final static String paid_in_full_choice_1 = "paid_in_full_choice_1";
		final static String paid_in_full_choice_2 = "paid_in_full_choice_2";
		private final static String cashReceipt = "Since the contract must be paid in full for this type of transaction, a cash receipt reflecting the outstanding balance must be included with the change order packet.";
		final static String cash_receipt_1 = cashReceipt;
		final static String cash_receipt_2 = cashReceipt;
		final static String return_inv_choice = "Is cemetery inventory (plot, crypt, niche) to be returned?";
		final static String property_owner_choice = "Does the property Owner(s) differ from the contract Patron(s)?";
		final static String property_owner_sig_note = "Since a disinterment is subject to the discretion of the property owners, they must be the signatory(s) on the change order.";
		final static String disint_type_choice = "Please select the type of disinterment that is to occur:";
		private final static String disintFee = "A disinterment fee applies to this transaction.";
		final static String disint_fee_1 = disintFee;
		final static String disint_fee_2 = disintFee;
		final static String disint_fee_3 = disintFee;
		final static String disint_fee_4 = disintFee;
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
		final static String orig_contract = "orig_contract_num_1";
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
		final static String transfer_donation = "transfer_donation";
		final static String transfer_fee_waived_note = "transfer_fee_waived_note";
		final static String assignee_info = "assignee_info";
		final static String orig_contract_date_1 = "orig_contract_date_1";
		final static String orig_contract_date_2 = "orig_contract_date_2";
		final static String admin_fee_waived_1 = "admin_fee_waived_1";
		final static String admin_fee_waived_2 = "admin_fee_waived_2";
		final static String admin_fee_waived_3 = "admin_fee_waived_3";
		final static String admin_fee_waived_4 = "admin_fee_waived_4";
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

	// Names of fillable form fields in the Change Order form PDF
	@SuppressWarnings("unused")
	private static class Field {
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
		final static String itemCode1 = itemCodeBase + "1";
		final static String itemCode2 = itemCodeBase + "2";
		final static String itemCode3 = itemCodeBase + "3";
		final static String itemCode4 = itemCodeBase + "4";
		final static String itemCode5 = itemCodeBase + "5";
		final static String descriptionBase = "Description ";
		final static String description1 = descriptionBase + "1";
		final static String description2 = descriptionBase + "2";
		final static String description3 = descriptionBase + "3";
		final static String description4 = descriptionBase + "4";
		final static String description5 = descriptionBase + "5";
		final static String extendedPriceBase = "Extended Price ";
		final static String extendedPrice1 = extendedPriceBase + "1";
		final static String extendedPrice2 = extendedPriceBase + "2";
		final static String extendedPrice3 = extendedPriceBase + "3";
		final static String extendedPrice4 = extendedPriceBase + "4";
		final static String extendedPrice5 = extendedPriceBase + "5";
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
