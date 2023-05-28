package br.com.metarmophic.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.metarmophic.pdf.dto.PdfPropertiesDTO;
import br.com.metarmophic.pdf.log.Logger;
import br.com.metarmophic.pdf.service.MergeService;
import lombok.Data;

@Data
@RestController
@RequestMapping("/merge")
public class ControllerMerge {
	
	@Autowired
	private MergeService mergeService = new MergeService();
	
	@PostMapping
	public ResponseEntity<byte[]> merge(@RequestParam("files") MultipartFile[] files, @RequestParam("config") String json) {
		
		ResponseEntity<byte[]> response = null;
		
		try {
			PdfPropertiesDTO properties = new ObjectMapper().readValue(json, PdfPropertiesDTO.class);
			Logger.getLog().debug("Iniciando servico de merge.");
			byte[] bytes = this.getMergeService().merge(files, properties);
			
			response = ResponseEntity.ok()
		                .contentType(MediaType.APPLICATION_PDF)
		                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + this.getNameResultFile() + "\"")
		                .body(bytes);
			
		} catch (Exception e) {
			Logger.getLog().info("Falha ao realizar o merge.");
			
			if (Logger.getLog().isDebugEnabled()) {
				e.printStackTrace();				
			}
			
			response = ResponseEntity.internalServerError().build();
		}
		
		return response;
	}
	
	/**
	 * Retorna um nome unico para o arquivo de resultado.
	 * @return String
	 */
	private String getNameResultFile() {
		String result = System.currentTimeMillis()+ ".pdf";
		
		return result;
	}
	
}
