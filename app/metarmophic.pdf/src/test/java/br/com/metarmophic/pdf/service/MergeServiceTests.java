package br.com.metarmophic.pdf.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import br.com.metarmophic.pdf.exception.FailMergeProcessException;
import br.com.metarmophic.pdf.exception.FileContentTypeNotSupportedException;
import br.com.metarmophic.pdf.merge.MergeServiceFacade;
import br.com.metarmophic.pdf.merge.MergeServiceValidation;

@ExtendWith(MockitoExtension.class)
public class MergeServiceTests {

	@InjectMocks
	MergeService service;
	
	@Mock
	MergeServiceFacade mergeServiceFacade;
	
	@Mock
	MergeServiceValidation mergeServiceValidation;
	
	@Test
	public void T001_merge_processFailure_throwException() {
		//arrange
		MergeService service = this.getDefaultMergeService();
		MultipartFile[] files = this.getSimpleArrayMultipartFiles();
		Mockito.when(mergeServiceFacade.merge(anyList(), any())).thenReturn(null);

		// act
		Exception exception = assertThrows(FailMergeProcessException.class, () -> {
			service.merge(files, null);
	    });
		
		// assert
		String expectedMessage = "Falha ao realizar o merge.";
		String actualMessage = exception.getMessage();
		
	    assertEquals(expectedMessage, actualMessage);
	}

	@Test
	public void T002_merge_emptyFileList_throwException() {
		//arrange
		MergeService service = this.getDefaultMergeService();
		MultipartFile[] files = {};

		// act
		Exception exception = assertThrows(IOException.class, () -> {
			service.merge(files, null);
	    });
		
		// assert
		String expectedMessage = "Não foi possível recuperar a lista de arquivos.";
		String actualMessage = exception.getMessage();
		
	    assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	public void T003_merge_processSucess_returnsByteArray() {		
		try {
			//arrange
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bytes.write("teste".getBytes());
			MergeService service = this.getDefaultMergeService();
			MultipartFile[] files = this.getSimpleArrayMultipartFiles();
			Mockito.when(mergeServiceFacade.merge(anyList(), any())).thenReturn(bytes);
			
			// act
			byte[] result = service.merge(files, null);
			
			// assert
		    assertEquals(new String(result), new String(bytes.toByteArray()));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		} 
	}
	
	/**
	 * Retorna um array de MultipartFile.
	 * @return MultipartFile[]
	 */
	private MultipartFile[] getSimpleArrayMultipartFiles() {
		MultipartFile[] files = {new MockMultipartFile("teste.pdf", "pdf".getBytes())}; 
		
		return files;
	}
	
	/**
	 * Retorna o objeto padrão.
	 * @return MergeService
	 */
	private MergeService getDefaultMergeService() {
		return service;
	}
	
}
