package com.teste.pdfbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

/**
 * Hello world!
 *
 */
public class App 
{
	
	private static final Log LOG = LogFactory.getLog(App.class);
	
    public static void main( String[] args ) throws IOException
    {
    	InputStream file1 = new FileInputStream(new File("C:\\Users\\David\\Documents\\dev\\java\\metamorphic-pdf\\app\\teste\\resources\\doc1.pdf"));
    	InputStream file2 = new FileInputStream(new File("C:\\Users\\David\\Documents\\dev\\java\\metamorphic-pdf\\app\\teste\\resources\\doc2.pdf"));
    	
        String title = "David é lindo";
        String creator = "David Rusycki";
        String subject = "Não sei o que é isso";

        List<InputStream> filesList = new ArrayList<InputStream>();
        filesList.add(file1);
        filesList.add(file2);
        
        try (COSStream cosStream = new COSStream();ByteArrayOutputStream mergedPDFOutputStream = new ByteArrayOutputStream())
        {
        	OutputStream resultado = new FileOutputStream(new File("C:\\Users\\David\\Documents\\dev\\java\\metamorphic-pdf\\app\\teste\\resources\\resultado.pdf"));
        	
            PDFMergerUtility pdfMerger = createPDFMergerUtility(filesList, mergedPDFOutputStream);
            PDDocumentInformation pdfDocumentInfo = createPDFDocumentInfo(title, creator, subject);
            PDMetadata xmpMetadata = createXMPMetadata(cosStream, title, creator, subject);            
            
            pdfMerger.setDestinationDocumentInformation(pdfDocumentInfo);
            pdfMerger.setDestinationMetadata(xmpMetadata);
        
            LOG.info("Juntando " + filesList.size() + " arquivos em 1 PDF");
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMixed(1000000));
            LOG.info("PDF merged com sucesso, tamanho = {" + mergedPDFOutputStream.size() + "} bytes");
        
            resultado.write(mergedPDFOutputStream.toByteArray());
            resultado.close();
        }
        catch (BadFieldValueException | TransformerException e)
        {
            throw new IOException("PDF merge problem", e);
        }
        finally
        {
        	filesList.forEach(IOUtils::closeQuietly);
        }
        
    }
    
    /**
     * Cria o utilitário responsável por fazer o merge, indicando os arquivos e para onde o resultado do merge deve ir.
     * @param filesList
     * @param mergedPDFOutputStream
     * @return PDFMergerUtility
     */
    private static PDFMergerUtility createPDFMergerUtility(List<InputStream> filesList, ByteArrayOutputStream mergedPDFOutputStream)
    {
        LOG.info("Iniciando o Utilitário de Merge");
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.addSources(filesList);
        pdfMerger.setDestinationStream(mergedPDFOutputStream);
        return pdfMerger;
    }

    /**
     * Cria o objeto de informações do pdf
     * @param title
     * @param creator
     * @param subject
     * @return PDDocumentInformation
     */
    private static PDDocumentInformation createPDFDocumentInfo(String title, String creator, String subject)
    {
        LOG.info("Setando as informações do documento. (title, author, subject) para o PDF Merge.");
        PDDocumentInformation documentInformation = new PDDocumentInformation();
        documentInformation.setTitle(title);
        documentInformation.setCreator(creator);
        documentInformation.setSubject(subject);
        return documentInformation;
    }

    /**
     * 
     * @param cosStream
     * @param title
     * @param creator
     * @param subject
     * @return
     * @throws BadFieldValueException
     * @throws TransformerException
     * @throws IOException
     */
    private static PDMetadata createXMPMetadata(COSStream cosStream, String title, String creator, String subject) throws BadFieldValueException, TransformerException, IOException
    {
        LOG.info("Setando o XMP metadata (title, author, subject) para o PDF merged.");
        XMPMetadata xmpMetadata = XMPMetadata.createXMPMetadata();

        // PDF/A-1b properties
        PDFAIdentificationSchema pdfaSchema = xmpMetadata.createAndAddPFAIdentificationSchema();
        pdfaSchema.setPart(1);
        pdfaSchema.setConformance("B");

        // Dublin Core properties
        DublinCoreSchema dublinCoreSchema = xmpMetadata.createAndAddDublinCoreSchema();
        dublinCoreSchema.setTitle(title);
        dublinCoreSchema.addCreator(creator);
        dublinCoreSchema.setDescription(subject);

        // XMP Basic properties
        XMPBasicSchema basicSchema = xmpMetadata.createAndAddXMPBasicSchema();
        Calendar creationDate = Calendar.getInstance();
        basicSchema.setCreateDate(creationDate);
        basicSchema.setModifyDate(creationDate);
        basicSchema.setMetadataDate(creationDate);
        basicSchema.setCreatorTool(creator);

        // Create and return XMP data structure in XML format
        try (ByteArrayOutputStream xmpOutputStream = new ByteArrayOutputStream();
                OutputStream cosXMPStream = cosStream.createOutputStream())
        {
            new XmpSerializer().serialize(xmpMetadata, xmpOutputStream, true);
            cosXMPStream.write(xmpOutputStream.toByteArray());
            return new PDMetadata(cosStream);
        }
    }

}
