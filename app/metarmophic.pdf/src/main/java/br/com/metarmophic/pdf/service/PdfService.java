package br.com.metarmophic.pdf.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
		List<MultipartFile> list = Arrays.asList(files).stream().collect(Collectors.toList());
		
		try {
			this.getMergeService().mergeFiles(list.get(0).getInputStream(), list.get(1).getInputStream());
		
			File fileSaida = File.createTempFile("merge", null);
			
			FileOutputStream mesmo = new FileOutputStream(fileSaida);
			InputStream saida = new FileInputStream(fileSaida);

			ByteArrayOutputStream bfile = this.getMergeService().getFileResult();
			
			mesmo.write(bfile.toByteArray());
			mesmo.flush();
			mesmo.close();
			fileSaida.deleteOnExit();
			
			return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_PDF)
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "merged.pdf" + "\"")
	                .body(saida.readAllBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}
	
	
}
