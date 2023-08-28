package br.com.metarmophic.pdf.merge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.metarmophic.pdf.dto.PdfPropertiesDTO;
import br.com.metarmophic.pdf.entitie.MetamorphicPdfPropertiesEntity;
import br.com.metarmophic.pdf.exception.FileContentTypeNotSupportedException;

@Service
public class MergeServiceValidation {

	private Logger log = LoggerFactory.getLogger(MergeServiceValidation.class);
	
	/**
	 * Prepara os dados.
	 * @return Boolean
	 * @throws FileContentTypeNotSupportedException 
	 */
	public Boolean prepareData(PdfPropertiesDTO properties) {
		/* Define as informações para o pdf */
		MetamorphicPdfPropertiesEntity propertiesEntity = new MetamorphicPdfPropertiesEntity(properties);
		propertiesEntity.setDatesForMerge();
		propertiesEntity.loadDefaultPropertiesOnNullables();
		
		return true;
	}
	
	/**
	 * Valida os arquivos recebidos.
	 * @param files
	 * @return Boolean
	 * @throws FileContentTypeNotSupportedException
	 */
	public Boolean validate(MultipartFile[] files) throws FileContentTypeNotSupportedException {
		this.validatePdfFormat(files);
		
		return true;
	}

	/**
	 * Valida o formato dos arquivos recebidos
	 * @param files
	 * @return Boolean
	 * @throws FileContentTypeNotSupportedException 
	 */
	private Boolean validatePdfFormat(MultipartFile[] files) throws FileContentTypeNotSupportedException {
		log.debug("Validando arquivos.");

		for (MultipartFile multipartFile : files) {
			if (!multipartFile.getContentType().toString().equals(MediaType.APPLICATION_PDF.toString())) {
				throw new FileContentTypeNotSupportedException("File content type not supported :/");
			}
		}
		
		log.debug("Arquivos validados com sucesso!");
		
		return true;
	}
	
	
}
