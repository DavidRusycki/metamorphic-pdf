package br.com.metarmophic.pdf.merge;

import java.io.ByteArrayOutputStream;
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
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

import lombok.Data;

@Data
public class MergeServiceFacade {

	/**
	 * Logger do serviço de merge
	 */
	private static final Log LOG = LogFactory.getLog(MergeServiceFacade.class);
	
	/**
	 * Arquivo de resultado do merge.
	 */
	private ByteArrayOutputStream fileResult = null;
	
	/**
	 * Método principal para realizar o merge de dois documentos.
	 * @param file1
	 * @param file2
	 * @return Boolean - Indica se o processamento ocorreu com sucesso.
	 */
	public Boolean mergeFiles(InputStream file1, InputStream file2) {
		
		//TODO Melhorar o código como um todo.
        String title = "Merged document";
        String creator = "David Rusycki";
        String subject = "Metamorphic.pdf";

        List<InputStream> filesList = new ArrayList<InputStream>();
        filesList.add(file1);
        filesList.add(file2);
        
        try (COSStream cosStream = new COSStream();ByteArrayOutputStream mergedPDFOutputStream = new ByteArrayOutputStream())
        {
            PDFMergerUtility pdfMerger = createPDFMergerUtility(filesList, mergedPDFOutputStream);
            PDDocumentInformation pdfDocumentInfo = createPDFDocumentInfo(title, creator, subject);
            PDMetadata xmpMetadata = createXMPMetadata(cosStream, title, creator, subject);            
            
            pdfMerger.setDestinationDocumentInformation(pdfDocumentInfo);
            pdfMerger.setDestinationMetadata(xmpMetadata);
        
            LOG.info("Juntando " + filesList.size() + " arquivos em 1 PDF");
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMixed(1000000));
            LOG.info("PDF merged com sucesso, tamanho = {" + mergedPDFOutputStream.size() + "} bytes");
        
            this.setFileResult(mergedPDFOutputStream);
        }
        catch (BadFieldValueException | TransformerException | IOException e)
        {
        	LOG.info("PDF merge problem");
        	LOG.info(e.getMessage());
        	e.printStackTrace();
        	return false;
        }
        finally
        {
        	filesList.forEach(IOUtils::closeQuietly);
        }
		
		return true;
	}
    
    /**
     * Cria o utilitário responsável por fazer o merge, indicando os arquivos e para onde o resultado do merge deve ir.
     * @param filesList
     * @param mergedPDFOutputStream
     * @return PDFMergerUtility
     */
    private PDFMergerUtility createPDFMergerUtility(List<InputStream> filesList, ByteArrayOutputStream mergedPDFOutputStream)
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
    private PDDocumentInformation createPDFDocumentInfo(String title, String creator, String subject)
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
    private PDMetadata createXMPMetadata(COSStream cosStream, String title, String creator, String subject) throws BadFieldValueException, TransformerException, IOException
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
