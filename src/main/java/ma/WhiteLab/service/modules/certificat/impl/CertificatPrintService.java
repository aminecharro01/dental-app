package ma.WhiteLab.service.modules.certificat.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import ma.WhiteLab.service.modules.certificat.dto.CertificatDTO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CertificatPrintService {
    
    public ByteArrayInputStream generatePdf(CertificatDTO cert) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Titre
            document.add(new Paragraph("CERTIFICAT MEDICAL"));
            document.add(new Paragraph(" "));
            
            // Contenu
            document.add(new Paragraph("Je soussigné, Docteur " + (cert.getNomMedecin() != null ? cert.getNomMedecin() : "Principal")));
            document.add(new Paragraph("Certifie avoir examiné le patient: " + (cert.getNomPatient() != null ? cert.getNomPatient() : "Inconnu")));
            document.add(new Paragraph("Et prescrit un repos de " + cert.getDureeRepos() + " jours."));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Du: " + cert.getDateDebut()));
            document.add(new Paragraph("Au: " + cert.getDateFin()));
            
            document.close();
            
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
