package br.com.metarmophic.pdf.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import br.com.metarmophic.pdf.exception.FailMergeProcessException;
import br.com.metarmophic.pdf.merge.MergeServiceFacade;

public class MergeServiceTests {
	
	@Test
	public void mergeReturnsNullThrowException() 
	{
		//arrange
		MergeServiceFacade serviceFacade = mock(MergeServiceFacade.class);
		MergeService service = mock(MergeService.class);
		when(serviceFacade.merge(any(), any())).thenReturn(null);
		when(service.prepareData(any(), any())).thenReturn(true);
		Exception exception = assertThrows(FailMergeProcessException.class, () -> {
			service.merge(null, null);
	    });
		
		// act
		String expectedMessage = "Falha ao realizar o merge.";
	    String actualMessage = exception.getMessage();

		// assert
	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
}
