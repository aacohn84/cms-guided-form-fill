package core.pdf;

import java.io.File;
import java.io.IOException;

import models.FilledFormFields;
import models.FilledFormFields.FilledFormField;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import core.forms.CMSForm;
import play.Logger;

public class PDFFormFiller {
	public File fillForm(CMSForm form, FilledFormFields formFields, File pdf) {
		String formFileName = form.getFormFileName();
		try {
			// load PDF form
			PDDocument pdfDoc = PDDocument.load(formFileName);
			PDAcroForm acroForm = pdfDoc.getDocumentCatalog().getAcroForm();
			
			// fill each field
			for (FilledFormField formField : formFields) {
				PDField pdField = acroForm.getField(formField.name);
				if (pdField == null) {
					Logger.error("The specified field [" + formField.name
							+ "] does not exist within this form ["
							+ formFileName + "]. The field will be ignored.");
				} else {
					pdField.setValue(formField.value);
				}
			}
			pdfDoc.save(pdf);
			Logger.debug("Filled PDF @ " + pdf.getAbsolutePath());
		} catch (IOException | COSVisitorException e) {
			throw new RuntimeException(e);
		}
		return pdf;
	}
}
