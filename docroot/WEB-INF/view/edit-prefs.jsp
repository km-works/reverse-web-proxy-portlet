<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ page import="java.util.Map" %>

<portlet:defineObjects />

<%
	Map<String, String> prefsMap = (Map<String, String>)request.getAttribute("prefs");
	String editPrefsURL = (String)request.getAttribute("actionURL");
%>

<h3 class="portlet-section-header">KM-WORKS Reverse Web Proxy Portlet Preferences</h3>

<form id="<portlet:namespace/>editForm" action="<%= editPrefsURL %>" method="post">
  
  <table>
    <tbody> 
      <tr>
        <td>Remote Start URL:&nbsp;</td>
        <td><input name="<portlet:namespace/>SRC" value="<%= prefsMap.get("SRC") %>" style="width: 400px"/></td>
      </tr>    
      <tr>
        <td>Remote Base URL:&nbsp;</td>
        <td><input name="<portlet:namespace/>PROXYREMOTEURL" value="<%= prefsMap.get("PROXYREMOTEURL") %>" style="width: 400px" /></td>
      </tr>    
      <tr>
        <td>Local Proxy URL:&nbsp;</td>
        <td><input name="<portlet:namespace/>PROXYLOCALPATH" value="<%= prefsMap.get("PROXYLOCALPATH") %>" style="width: 400px" /></td>
      </tr>    
      <tr>
        <td>Height:&nbsp;</td>
        <td><input name="<portlet:namespace/>HEIGHT" value="<%= prefsMap.get("HEIGHT") %>" style="width: 100px" /></td>
      </tr>    
      <tr>
        <td>Width:&nbsp;</td>
        <td><input name="<portlet:namespace/>WIDTH" value="<%= prefsMap.get("WIDTH") %>" style="width: 100px" /></td>
      </tr>    
      <tr>
        <td>Scrolling:&nbsp;</td>
        <td><input name="<portlet:namespace/>SCROLLING" value="<%= prefsMap.get("SCROLLING") %>" style="width: 100px" /></td>
      </tr>    
    </tbody>
  </table>
      
	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
	
</form>
