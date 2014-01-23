package it.bioko.http.rest;

import java.nio.charset.Charset;

import org.junit.Test;

public class EncodingTest {

	private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final String TEST_STRING = new String("Ciao: àèéòù".getBytes(ISO_8859_1), ISO_8859_1);
	

	@Test
	public void fromISO_8859_1ToUTF_8() {
		byte[] byteContent = TEST_STRING.getBytes(ISO_8859_1);
		String iso88591 = new String(byteContent, ISO_8859_1);
		System.out.println("Encoded(" + ISO_8859_1 + "):" + iso88591);
		String utf8 = new String(iso88591.getBytes(UTF_8), UTF_8);
		System.out.println("Encoded(" + UTF_8 + "):" + utf8);		
	}

}
