package br.com.metarmophic.pdf.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.metarmophic.pdf.dto.PdfPropertiesDTO;
import br.com.metarmophic.pdf.entitie.MetamorphicPdfPropertiesEntity;
import br.com.metarmophic.pdf.exception.FailMergeProcessException;
import br.com.metarmophic.pdf.exception.FileContentTypeNotSupportedException;
import br.com.metarmophic.pdf.log.Logger;
import br.com.metarmophic.pdf.merge.MergeServiceFacade;
import br.com.metarmophic.pdf.merge.MergeServiceValidation;
import lombok.Data;

@Data
@Service
public class MergeService {
	
	@Autowired
	private MergeServiceFacade mergeServiceFacade;
	
	@Autowired
	private MergeServiceValidation mergeServiceValidation;
	
	/**
	 * Realiza validações e tratamentos para chamar a classe Merge.
	 * @param files - Lista de arquivos
	 * @param properties - Propriedades para o arquivo final
	 * @return byte[]
	 * @throws IOException 
	 * @throws FailMergeProcessException 
	 * @throws FileContentTypeNotSupportedException 
	 */
	public byte[] merge(@RequestParam("files") MultipartFile[] files, PdfPropertiesDTO properties) throws FailMergeProcessException, FileContentTypeNotSupportedException, IOException {
		mergeServiceValidation.validate(files);
		mergeServiceValidation.prepareData(properties);
		
		List<InputStream> list = this.getListInputStreamFiles(files);
		Logger.getLog().debug("Iniciando processo de merge.");
		ByteArrayOutputStream bfile = this.getMergeServiceFacade().merge(list, properties);
		
		if (bfile == null) {
			String falha = "Falha ao realizar o merge.";
			Logger.getLog().info(falha);
			throw new FailMergeProcessException(falha);
		}
		
		Logger.getLog().info("Juncao realizada com sucesso!");
		
		return bfile.toByteArray();
	}
	
	/**
	 * Monta a lista de InputStream de arquivos.
	 * @param files
	 * @return List<InputStream>
	 * @throws IOException
	 */
	private List<InputStream> getListInputStreamFiles(MultipartFile[] files) throws IOException {
		List<InputStream> list = new ArrayList<InputStream>();
		
		for (MultipartFile multipartFile : files) {
			list.add(multipartFile.getInputStream());
		}
		
		if (list.size() <= 0) {
			throw new IOException("Não foi possível recuperar a lista de arquivos.");
		}
		
		return list;
	}  
	
}
