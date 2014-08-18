package models.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import models.data.FilledFormFields;
import models.data.FilledFormFields.FilledFormField;
import models.forms.CMSForm;

public class PDFFormFiller {
	public File fillForm(CMSForm form, FilledFormFields formFields) {
		String formFileName = form.getFormFileName();
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
				pdfDoc.save(formFileName + "_filled.pdf");
			}
		} catch (IOException | COSVisitorException e) {
			throw new RuntimeException(e);
		}
		return new File(formFileName + "_filled.pdf");
	}
}
