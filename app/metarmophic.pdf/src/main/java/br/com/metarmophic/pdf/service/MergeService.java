package br.com.metarmophic.pdf.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.metarmophic.pdf.dto.PdfPropertiesDTO;
import br.com.metarmophic.pdf.entitie.MetamorphicPdfPropertiesEntity;
import br.com.metarmophic.pdf.exception.FailMergeProcessException;
import br.com.metarmophic.pdf.log.Logger;
import br.com.metarmophic.pdf.merge.MergeServiceFacade;
import lombok.Data;

@Data
@Service
public class MergeService {
	
	private MergeServiceFacade mergeServiceFacade = new MergeServiceFacade();
	
	/**
	 * Realiza validações e tratamentos para chamar a classe Merge.
	 * @param files - Lista de arquivos
	 * @param properties - Propriedades para o arquivo final
	 * @return byte[]
	 * @throws IOException 
	 * @throws FailMergeProcessException 
	 */
	public byte[] merge(@RequestParam("files") MultipartFile[] files, PdfPropertiesDTO properties) throws IOException, FailMergeProcessException {
		this.prepareData(files, properties);
		
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
	 * Prepara e valida os dados.
	 * @return Boolean
	 */
	private Boolean prepareData(MultipartFile[] files, PdfPropertiesDTO properties) {
		/* Valida o formato dos arquivos recebidos*/
		this.validatePdfFormat(files);
		
		/* Define as informações para o pdf */
		MetamorphicPdfPropertiesEntity propertiesEntity = new MetamorphicPdfPropertiesEntity(properties);
		propertiesEntity.setDatesForMerge();
		propertiesEntity.loadDefaultPropertiesOnNullables();
		
		return true;
	}
	
	/**
	 * Valida o formato dos arquivos recebidos
	 * @param files
	 * @return Boolean
	 */
	private Boolean validatePdfFormat(MultipartFile[] files) {
		Logger.getLog().debug("Validando arquivos.");
		//TODO validar se o tipo é pdf.		
		Logger.getLog().debug("Arquivos validados com sucesso!");
		
		return true;
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
