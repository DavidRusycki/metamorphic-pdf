package br.com.metarmophic.pdf.dto;

import lombok.Data;

@Data
public class HealthDTO {

	private Long time;
	private String status;
	
	public HealthDTO() {
		this.time = System.currentTimeMillis();
		this.status = "running";
	}
	
}
