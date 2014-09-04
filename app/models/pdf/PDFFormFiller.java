package models.pdf;

import java.io.File;
import java.io.IOException;

import models.data.FilledFormFields;
import models.data.FilledFormFields.FilledFormField;
import models.forms.CMSForm;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import play.Logger;

public class PDFFormFiller {
	public File fillForm(CMSForm form, FilledFormFields formFields) {
		String formFileName = form.getFormFileName();
		File temp;
		try {
			// load PDF form
			PDDocument pdfDoc = PDDocument.load(formFileName);
			PDAcroForm acroForm = pdfDoc.getDocumentCatalog().getAcroForm();
			
			// fill each field
			for (FilledFormField formField : formFields) {
				PDField pdField = acroForm.getField(formField.name);
				if (pdField == null) {
					throw new RuntimeException("The specified field ["
							+ formField.name
							+ "] does not exist within this form ["
							+ formFileName + "]");
				}
				pdField.setValue(formField.value);
			}
			temp = File.createTempFile("Change_Order_Form_Filled", ".pdf");
			pdfDoc.save(temp);
			Logger.debug("Filled PDF @ " + temp.getAbsolutePath());
		} catch (IOException | SecurityException | COSVisitorException e) {
			throw new RuntimeException(e);
		}
		return temp;
	}
}
