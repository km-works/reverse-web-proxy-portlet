/*
 * Copyright (C) 2005-2014 Christian P. Lerch <christian.p.lerch [at] gmail [dot] com>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 */
package kmworks.liferay.proxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpReverseProxyServlet 
        extends org.apache.portals.applications.webcontent.proxy.impl.DefaultHttpReverseProxyServlet{

	private static final long serialVersionUID = 1L;
	private static final String MSG_FORBIDDEN = "Diese Seite kann nur angezeigt werden, wenn Sie im Portal angemeldet sind";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	if (hasSession(request)) {
    		super.service(request, response);
    	} else {
    		response.sendError(HttpServletResponse.SC_FORBIDDEN, MSG_FORBIDDEN);
    	}
    }
    
	private boolean hasSession(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) return false;
		if (!hasCookie(cookies, "JSESSIONID")) return false;
		if (!hasCookie(cookies, "COMPANY_ID")) return false;
		if (!hasCookie(cookies, "ID")) return false;
		if (!hasCookie(cookies, "USER_UUID")) return false;
		if (!hasCookie(cookies, "GUEST_LANGUAGE_ID")) return false;
		return true;
	}
	
	private boolean hasCookie(Cookie[] cookies, String name) {
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(name)) {
        return true;
      }
    }
		return false;
	}

}
