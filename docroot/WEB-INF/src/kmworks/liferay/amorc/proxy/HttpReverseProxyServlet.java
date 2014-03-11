package kmworks.liferay.amorc.proxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.liferay.portal.service.ServiceContextFactory;

public class HttpReverseProxyServlet extends org.apache.portals.applications.webcontent.proxy.impl.DefaultHttpReverseProxyServlet{

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
