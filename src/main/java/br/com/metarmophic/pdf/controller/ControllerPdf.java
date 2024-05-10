package br.com.metarmophic.pdf.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.metarmophic.pdf.dto.HealthDTO;

@RestController
@RequestMapping("/pdf")
@CrossOrigin(origins = "*")
public class ControllerPdf {
	
	@GetMapping
	@RequestMapping("/health")
	public HealthDTO health() {
		return new HealthDTO();
	}
	
}
