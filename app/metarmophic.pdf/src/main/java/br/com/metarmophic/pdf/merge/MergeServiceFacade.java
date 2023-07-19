package br.com.metarmophic.pdf.merge;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import br.com.metarmophic.pdf.dto.PdfPropertiesDTO;
import br.com.metarmophic.pdf.log.Logger;
import lombok.Data;

@Data
@Service
public class MergeServiceFacade {

	/**
	 * Arquivo de resultado do merge.
	 */
	private ByteArrayOutputStream fileResult = null;
	
	/**
	 * Realiza o processamento para junção
	 * @param fileList
	 * @param properties
	 * @return ByteArrayOutputStream
	 */
	public ByteArrayOutputStream merge(List<InputStream> fileList, PdfPropertiesDTO properties) {
		Boolean sucesso = this.mergeFiles(fileList, properties);
		
		ByteArrayOutputStream bfile = null;
		if (sucesso) {
			bfile = this.getFileResult();
		}
		
		return bfile;
	}	
	
	/**
	 * Método principal para realizar o merge de dois documentos.
	 * @param file1
	 * @param file2
	 * @return Boolean - Indica se o processamento ocorreu com sucesso.
	 */
	private Boolean mergeFiles(List<InputStream> files, PdfPropertiesDTO properties) {
		//TODO Implementar um esquema de logs das operações executadas.
		Logger.getLog().debug("Instanciando recursos para juncao.");
		
        try (ByteArrayOutputStream mergedPDFOutputStream = new ByteArrayOutputStream())
        {
            PDFMergerUtility pdfMerger = this.createPDFMergerUtility(files, mergedPDFOutputStream);
            PDDocumentInformation pdfDocumentInfo = this.createPDFDocumentInfo(properties);            
            pdfMerger.setDestinationDocumentInformation(pdfDocumentInfo);
        
            Logger.getLog().debug("Juntando " + files.size() + " arquivos");
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMixed(1000000));
        
            this.setFileResult(mergedPDFOutputStream);
        }
        catch (Exception e)
        {
        	this.setFileResult(null);
        	Logger.getLog().debug("Problema ao realizar o merge");
        	Logger.getLog().debug(e.getMessage());
        	if (Logger.getLog().isDebugEnabled()) {
        		e.printStackTrace();        		
        	}
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
    private PDFMergerUtility createPDFMergerUtility(List<InputStream> filesList, ByteArrayOutputStream mergedPDFOutputStream) {
    	Logger.getLog().debug("Instanciando informacoes do documento.");
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.addSources(filesList);
        pdfMerger.setDestinationStream(mergedPDFOutputStream);
        
        return pdfMerger;
    }

    /**
     * Cria o objeto de informações do pdf
     * @param properties - Objeto de Propriedades
     * @return PDDocumentInformation
     */
    private PDDocumentInformation createPDFDocumentInfo(PdfPropertiesDTO properties) {
    	Logger.getLog().debug("Definindo as propriedades do documento.");
        PDDocumentInformation documentInformation = new PDDocumentInformation();
        documentInformation.setTitle(properties.getTitle());
        documentInformation.setCreator(properties.getCreator());
        documentInformation.setSubject(properties.getSubject());
        documentInformation.setAuthor(properties.getAuthor());
        documentInformation.setProducer(properties.getProducer());
        documentInformation.setCreationDate(properties.getCreationDate());
        documentInformation.setModificationDate(properties.getModificationDate());
        
        return documentInformation;
    }

}


