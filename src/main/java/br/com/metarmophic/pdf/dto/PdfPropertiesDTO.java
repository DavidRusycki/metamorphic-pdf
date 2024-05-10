package br.com.metarmophic.pdf.dto;

import java.util.Calendar;
import java.util.TimeZone;

import lombok.Data;

@Data
public class PdfPropertiesDTO {

	private String title = null;
	private String creator = null;
	private String subject = null;
	private String author = null;
	private String producer = null;
	private Calendar creationDate = null;
	private Calendar modificationDate = null;
	
}
