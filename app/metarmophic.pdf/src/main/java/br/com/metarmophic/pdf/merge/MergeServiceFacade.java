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
	public Boolean mergeFiles(List<InputStream> files) {
		
		//TODO Melhorar o código como um todo.
        String title = "Merged document";
        String creator = "David Rusycki";
        String subject = "Metamorphic.pdf";
        
        try (COSStream cosStream = new COSStream();ByteArrayOutputStream mergedPDFOutputStream = new ByteArrayOutputStream())
        {
            PDFMergerUtility pdfMerger = createPDFMergerUtility(files, mergedPDFOutputStream);
            PDDocumentInformation pdfDocumentInfo = createPDFDocumentInfo(title, creator, subject);            
            
            pdfMerger.setDestinationDocumentInformation(pdfDocumentInfo);
        
            LOG.info("Juntando " + files.size() + " arquivos em 1 PDF");
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMixed(1000000));
            LOG.info("PDF merged com sucesso, tamanho = {" + mergedPDFOutputStream.size() + "} bytes");
        
            this.setFileResult(mergedPDFOutputStream);
        }
        catch (IOException e)
        {
        	LOG.info("PDF merge problem");
        	LOG.info(e.getMessage());
        	e.printStackTrace();
        	return false;
        }
        finally
        {
        	files.forEach(IOUtils::closeQuietly);
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

}
