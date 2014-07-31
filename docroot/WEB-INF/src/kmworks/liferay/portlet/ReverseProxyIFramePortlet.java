package kmworks.liferay.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletPreferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.portals.applications.webcontent.portlet.IFrameGenericPortlet;
import org.apache.portals.messaging.PortletMessaging;


public class ReverseProxyIFramePortlet extends IFrameGenericPortlet {
	
	private static final Log _log = LogFactory.getLog(ReverseProxyIFramePortlet.class);
  
  private final Map<String, String> attributes = new HashMap<>();
  private final Map<String, String> maxAttributes = new HashMap<>();
	private String editJsp;
	
  @Override
  public void init(PortletConfig config) throws PortletException {
    super.init(config);
    
    editJsp = getInitParameter("edit-jsp");
    
    attributes.put("TITLE", "");
    attributes.put("SRC", "http://localhost");
    attributes.put("PROXYREMOTEURL", "");
    attributes.put("PROXYLOCALPATH", "");
    attributes.put("HEIGHT", "800");
    attributes.put("WIDTH", "100%");

    attributes.put("FRAMEBORDER", "0");
    attributes.put("MARGINHEIGHT", "5");
    attributes.put("MARGINWIDTH", "5");
    attributes.put("CLASS", "");
    attributes.put("ID", "");
    attributes.put("NAME", "");
    attributes.put("AUTORESIZE", "false");
    attributes.put("VISITLASTPAGE", "false");
    attributes.put("HANDLERSCRIPT", "");
    attributes.put("SCROLLING", "NO");
    attributes.put("STYLE", "");
    
    maxAttributes.put("HEIGHT", "600");
    maxAttributes.put("WIDTH", "100%");
    maxAttributes.put("SCROLLING", "AUTO");
    maxAttributes.put("STYLE", "");
  }
	
  @Override
  public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {

    response.setContentType("text/html");

    PortletPreferences prefs = request.getPreferences();
    Map<String, String[]> map = prefs.getMap();
    Map<String, String> prefsMap = new HashMap<>();
    for (String key : map.keySet()) {
      String[] prefValue = map.get(key);
      prefsMap.put(key, prefValue == null || prefValue[0] == null ? "" : prefValue[0]);
    }
    request.setAttribute("prefs", prefsMap);

    PortletURL editPrefsURL = response.createActionURL();
    editPrefsURL.setParameter("action", "editPrefs");
    request.setAttribute("actionURL", editPrefsURL.toString());

    forward(editJsp, request, response);
  }
  
  /**
   * Render IFRAME content
   * @param request
   * @param response
   * @throws javax.portlet.PortletException
   * @throws java.io.IOException
   */
  @Override
  public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
    PortletPreferences prefs = request.getPreferences();
    String source = getURLSource(request, response, prefs);
    // generate HTML IFRAME content
    StringWriter content = new StringWriter(4096);

    // fix JS2-349
    content.append("<table class='iframePortletTableContainer' width='100%'><tbody class='iframePortletTbodyContainer'><tr><td>");

    content.append("<iframe");

    // special case source
    content.append(' ').append("src").append("=\"").append(source).append('"');

    appendAttribute(prefs, content, "CLASS");
    appendAttribute(prefs, content, "FRAMEBORDER");
    appendAttribute(prefs, content, "ID");
    appendAttribute(prefs, content, "MARGINHEIGHT");
    appendAttribute(prefs, content, "MARGINWIDTH");
    appendAttribute(prefs, content, "NAME");

    if (BooleanUtils.toBoolean(getAttributePreference(prefs, "AUTORESIZE"))) {
      appendAttribute(prefs, content, "AUTORESIZE");
    }

    if (BooleanUtils.toBoolean(getAttributePreference(prefs, "VISITLASTPAGE"))) {
      appendAttribute(prefs, content, "VISITLASTPAGE");
    }

    if (request.getWindowState().equals(WindowState.MAXIMIZED)) {
      appendMaxAttribute(prefs, content, "HEIGHT");
      appendMaxAttribute(prefs, content, "WIDTH");
      appendMaxAttribute(prefs, content, "SCROLLING");
      appendMaxAttribute(prefs, content, "STYLE");
    } else {
      appendAttribute(prefs, content, "HEIGHT");
      appendAttribute(prefs, content, "WIDTH");
      appendAttribute(prefs, content, "SCROLLING");
      appendAttribute(prefs, content, "STYLE");
    }

    content.append('>');
    content.append("<p style=\"text-align: center\"><a href=\"").append(source).append("\">").append(source).append("</a></p>");
    content.append("</iframe>");

    // end fix JS2-349
    content.append("</td></tr></tbody></table>");

    // set required content type and write HTML IFRAME content
    response.setContentType("text/html");
    response.getWriter().print(content.toString());
  }
  
  @Override
  public void processAction(ActionRequest request, ActionResponse response)
          throws IOException, PortletException {

    String action = request.getParameter("action");
    
    if ("editPrefs".equals(action)) {
      PortletPreferences prefs = request.getPreferences();
      for (String key : prefs.getMap().keySet()) {
        String value = request.getParameter(key);
        prefs.setValue(key, value);
      }
      prefs.store();

    } else {
      String url = request.getParameter("URL");
      if (!StringUtils.isBlank(url)) {
        PortletMessaging.publish(request, IFrameGenericPortlet.IFRAME_SRC_URL, url.trim());
      }
    }

    response.setPortletMode(PortletMode.VIEW);
  }
	
	private void forward(String path, RenderRequest renderRequest, RenderResponse renderResponse)
	    throws IOException, PortletException {
		
	    PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(path);
	    if (portletRequestDispatcher == null) {
	        _log.error(path + " is not a valid forward location");
	    }
	    else {
	        portletRequestDispatcher.include(renderRequest, renderResponse);
	    }
	}
  
  private String getAttributePreference(PortletPreferences prefs, String attribute) {
    return this.getMappedAttributePreference(prefs, attribute, attributes);
  }

  private String getMaxAttributePreference(PortletPreferences prefs, String attribute) {
    return this.getMappedAttributePreference(prefs, "MAX-" + attribute, maxAttributes);
  }

  private String getMappedAttributePreference(PortletPreferences prefs, String attribute, Map<String, String> map) {
    return prefs.getValue(attribute, map.get(attribute));
  }

  private void appendAttribute(PortletPreferences prefs, StringWriter content, String attribute, Map<String, String> map) {
    String value;

    if (map == maxAttributes) {
      value = getMaxAttributePreference(prefs, attribute);
    } else {
      value = getAttributePreference(prefs, attribute);
    }

    if (value == null || value.length() == 0) {
      return;
    }
    content.append(' ').append(attribute.toLowerCase()).append("=\"").append(value).append('"');
  }

  private void appendAttribute(PortletPreferences prefs, StringWriter content, String attribute) {
    appendAttribute(prefs, content, attribute, attributes);
  }

  private void appendMaxAttribute(PortletPreferences prefs, StringWriter content, String attribute) {
    appendAttribute(prefs, content, attribute, maxAttributes);
  }

  @Override
  public String getURLSource(RenderRequest request, RenderResponse response, PortletPreferences prefs) {
    String[] srcReplaceValues = {request.getServerName(), Integer.toString(request.getServerPort()), request.getContextPath()};

    String source = (String) PortletMessaging.receive(request, IFrameGenericPortlet.IFRAME_SRC_URL);

    if (source == null) {
      source = StringUtils.replaceEach(getAttributePreference(prefs, "SRC"), IFrameGenericPortlet.SRC_REPLACE_KEYS, srcReplaceValues);
    }

    // Sometimes, iframe's SRC attribute can be set to a local url to allow cross-domain scripting.
    // If proxy remote URL and its corresponding local path are set, then the proxy remote URL prefix should be replaced by the local path.
    String proxyRemoteURL = StringUtils.replaceEach(getAttributePreference(prefs, "PROXYREMOTEURL"), 
            IFrameGenericPortlet.SRC_REPLACE_KEYS, srcReplaceValues);
    String proxyLocalPath =  StringUtils.replaceEach(getAttributePreference(prefs, "PROXYLOCALPATH"), 
            IFrameGenericPortlet.SRC_REPLACE_KEYS, srcReplaceValues);

    if (StringUtils.isNotEmpty(proxyRemoteURL) 
            && StringUtils.isNotEmpty(proxyLocalPath) 
            && StringUtils.startsWith(source, proxyRemoteURL)) {
      source = proxyLocalPath + source.substring(proxyRemoteURL.length());
    }

		String userName = getUserName(request);
		if (source == null || userName == null) {
			return source;
		} else {
			return appendParam(source, "user", userName);
		}
	}
    
	private String getUserName(RenderRequest request) {
		long userId = 0L;
		User user = null;
		try {
			userId = ServiceContextFactory.getInstance(request).getUserId();
			if (userId != 0L) {
				user = UserLocalServiceUtil.getUserById(userId);
			}
		} catch (PortalException | SystemException e) {} 
		if (userId == 0L || user == null) {
			return null;
		} else {
			return user.getScreenName();
		}
	}
	
	private String appendParam(String url, String name, String value) {
		final int pos = url.indexOf('?');
		StringBuilder sb = new StringBuilder();
		sb.append(url).append(pos<0 ? '?' : '&').append(name).append('=').append(value);
		return sb.toString();
	}
  
}
