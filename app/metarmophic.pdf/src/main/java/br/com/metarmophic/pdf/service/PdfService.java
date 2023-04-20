package br.com.metarmophic.pdf.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.metarmophic.pdf.dto.HealthDTO;
import br.com.metarmophic.pdf.merge.MergeServiceFacade;
import lombok.Data;

@RestController
@RequestMapping("/pdf")
@Data
public class PdfService {
	
	private MergeServiceFacade mergeService = new MergeServiceFacade();
	
	@GetMapping
	@RequestMapping("/health")
	public HealthDTO health() {
		return new HealthDTO();
	}
	
	@PostMapping
	@RequestMapping("/merge")
	public ResponseEntity<byte[]> merge(@RequestParam("files") MultipartFile[] files) {
		//TODO validar se o tipo é pdf.
		
		
		//TODO tentar facilitar o merge utilizando apenas o Utility
		try {
			List<InputStream> list = this.getListInputStreamFiles(files);
			this.getMergeService().mergeFiles(list);
			ByteArrayOutputStream bfile = this.getMergeService().getFileResult();
			
			String resultFileName = UUID.randomUUID().toString();
			
			return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_PDF)
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resultFileName +".pdf" + "\"")
	                .body(bfile.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
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
