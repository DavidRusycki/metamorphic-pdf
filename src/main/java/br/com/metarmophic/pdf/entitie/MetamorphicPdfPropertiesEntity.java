package br.com.metarmophic.pdf.entitie;

import java.util.Calendar;
import java.util.TimeZone;

import br.com.metarmophic.pdf.dto.PdfPropertiesDTO;
import lombok.Data;

@Data
public class MetamorphicPdfPropertiesEntity {

	private PdfPropertiesDTO pdfPropertiesDTO;

	/**
	 * Construtor da entidade
	 * @param properties
	 */
	public MetamorphicPdfPropertiesEntity(PdfPropertiesDTO properties) {
		this.setPdfPropertiesDTO(properties);
	}
	
	/**
	 * Carregar as propriedades padrões do Metamorphic dentro do DTO de propriedades do PDF.
	 * @return Boolean
	 */
	public Boolean loadDefaultPropertiesOnNullables() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

		PdfPropertiesDTO properties = this.getPdfPropertiesDTO();
		properties.setAuthor          (this.getDefault(properties.getAuthor()          , "DocEasy"));
		properties.setCreator         (this.getDefault(properties.getCreator()         , "DocEasy"));
		properties.setProducer        (this.getDefault(properties.getProducer()        , "DocEasy"));
		properties.setSubject         (this.getDefault(properties.getSubject()         , "Merge document"));
		properties.setTitle           (this.getDefault(properties.getTitle()           , "Merged document"));
		properties.setModificationDate(this.getDefault(properties.getModificationDate(), calendar));
		properties.setCreationDate    (this.getDefault(properties.getCreationDate()    , calendar));
		
		return true;
	}

	/**
	 * Define as propriedades de data para um processo de merge.
	 * @return Boolean - Indica o sucesso da operação.
	 */
	public Boolean setDatesForMerge() {
		Calendar calendar = this.getCurrentCalendar();		
		
		PdfPropertiesDTO properties = this.getPdfPropertiesDTO();
		properties.setModificationDate(calendar);
		properties.setCreationDate(calendar);
		
		return true;
	}
	
	/**
	 * Retorna o Calendar atual.
	 * @return Calendar
	 */
	private Calendar getCurrentCalendar() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

		return calendar;
	}
	
	/**
	 * Verifica se o primeiro parâmetro existe e retorna ele caso sim.
	 * @param <T>
	 * @param valor1
	 * @param valor2
	 * @return <T>
	 */
	private <T> T getDefault(T valor1, T valor2) {
		if (valor1 != null) {
			return valor1;
		}
		return valor2;
	}
	
}
