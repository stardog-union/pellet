package com.clarkparsia.pellet.server.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Edgar Rodriguez-Diaz
 */
public class ShutdownServlet extends HttpServlet {



	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
	                                                                                           IOException {
		this.doGet(req, resp);
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
	                                                                                          IOException {

	}
}
