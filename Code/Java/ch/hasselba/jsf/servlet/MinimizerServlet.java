package ch.hasselba.jsf.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import ch.hasselba.napi.NAPIUtils;

import com.ibm.xsp.webapp.DesignerFacesServlet;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class MinimizerServlet extends DesignerFacesServlet implements
		Serializable {

	private static final long serialVersionUID = -1L;

	@Override
	public void service(ServletRequest servletRequest,
			ServletResponse servletResponse) throws ServletException,
			IOException {

		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse res = (HttpServletResponse) servletResponse;
		ServletOutputStream out = servletResponse.getOutputStream();

		try {
			res.setContentType("application/x-javascript");
			res.setCharacterEncoding("utf-8");
			res.addHeader("Content-Encoding", "gzip");

			// load the js requested files
			StringBuffer fileData = new StringBuffer();
			String tmpFile = "";
			String paramFiles = req.getParameter("files");
			String[] files = paramFiles.split(" ");

			// and add them to a buffer
			for (String file : files) {

				try {
					tmpFile = NAPIUtils.loadFile("DEV01", "WebContentDemo.nsf",
							file);
					fileData.append(tmpFile);
				} catch (Exception e) {
					// ignore errors
					e.printStackTrace();
				}

			}

			// Compress the JS Code with compressor
			StringWriter sWriter = new StringWriter();
			compress(stringBufferToInputStreamReader(fileData), sWriter);

			// and GZIP it
			ByteArrayOutputStream obj = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			gzip.write(sWriter.toString().getBytes("UTF-8"));
			gzip.close();

			// send it to the client
			out.write(obj.toByteArray());

		} catch (Exception e) {
			e.printStackTrace(new PrintStream(out));
		} finally {
			out.close();
		}
	}

	/**
	 * Helper to convert a StringBuffer to an InputStreamReader
	 * 
	 * @param strBuffer
	 *            the StringBuffer to convert
	 * @return the converted InputStreamReader
	 */
	public static InputStreamReader stringBufferToInputStreamReader(
			final StringBuffer strBuffer) {
		return new InputStreamReader(new ByteArrayInputStream(strBuffer
				.toString().getBytes()));
	}

	/**
	 * compresses the JS code using YUI
	 * 
	 * @param in
	 *            the InputStreamReader containing the JS
	 * @param out
	 *            the Writer Object
	 * @throws EvaluatorException
	 * @throws IOException
	 */
	public static void compress(final InputStreamReader in, Writer out)
			throws EvaluatorException, IOException {
		JavaScriptCompressor compressor = new JavaScriptCompressor(in,
				new ErrorReporter() {
					public void warning(String message, String sourceName,
							int line, String lineSource, int lineOffset) {

						System.out.println("\n[WARNING]");
						if (line < 0) {
							System.out.println(" " + message);
						} else {
							System.out.println(" " + line + ':' + lineOffset
									+ ':' + message);
						}
					}

					public void error(String message, String sourceName,
							int line, String lineSource, int lineOffset) {
						System.out.println("[ERROR] ");
						if (line < 0) {
							System.out.println(" " + message);
						} else {
							System.out.println(" " + line + ':' + lineOffset
									+ ':' + message);
						}
					}

					public EvaluatorException runtimeError(String message,
							String sourceName, int line, String lineSource,
							int lineOffset) {
						error(message, sourceName, line, lineSource, lineOffset);
						return new EvaluatorException(message);
					}
				});

		// call YUI
		compressor.compress(out, 0, true, false, false, false);
	}
}