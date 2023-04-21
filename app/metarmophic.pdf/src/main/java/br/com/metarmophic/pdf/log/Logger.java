package br.com.metarmophic.pdf.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Logger {

	private static final Log LOG = LogFactory.getLog(Logger.class);
	
	/**
	 * Retorna a instância do Logger 
	 * @return Logger
	 */
	public static Log getLog() {
		return LOG;
	}
	
}
