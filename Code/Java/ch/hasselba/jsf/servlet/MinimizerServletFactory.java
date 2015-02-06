package ch.hasselba.jsf.servlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.ibm.designer.runtime.domino.adapter.ComponentModule;
import com.ibm.designer.runtime.domino.adapter.IServletFactory;
import com.ibm.designer.runtime.domino.adapter.ServletMatch;

public class MinimizerServletFactory implements IServletFactory {

	private static final String SERVLET_WIDGET_CLASS = "ch.hasselba.jsf.servlet.MinimizerServlet";
	private static final String SERVLET_WIDGET_NAME = "JS Minimizer";

	private ComponentModule module;

	public void init(ComponentModule module) {
		this.module = module;
	}

	public ServletMatch getServletMatch(String contextPath, String path)
			throws ServletException {

		String servletPath = "";

		if (path.contains("/minimizer")) {
			String pathInfo = path;
			return new ServletMatch(getWidgetServlet(), servletPath, pathInfo);
		}

		return null;
	}

	public Servlet getWidgetServlet() throws ServletException {
		return module.createServlet(SERVLET_WIDGET_CLASS, SERVLET_WIDGET_NAME,
				null);
	}

}