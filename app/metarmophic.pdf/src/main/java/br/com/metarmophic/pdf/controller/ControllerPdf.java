package br.com.metarmophic.pdf.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import br.com.metarmophic.pdf.service.MergeService;
import lombok.Data;

@RestController
@RequestMapping("/pdf")
public class ControllerPdf {
	
	@GetMapping
	@RequestMapping("/health")
	public HealthDTO health() {
		return new HealthDTO();
	}
	
}
