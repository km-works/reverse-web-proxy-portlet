<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page language="java" import="javax.portlet.PortletRequest,org.apache.portals.messaging.PortletMessaging,org.apache.portals.applications.webcontent.portlet.IFrameGenericPortlet" session="true" %>
<%
String url = request.getParameter("URL");

if (url != null && !"".equals(url.trim()))
{
    PortletRequest portletRequest = (PortletRequest) request.getAttribute("javax.portlet.request");
    
    if (portletRequest != null)
    {
        PortletMessaging.publish(portletRequest, IFrameGenericPortlet.IFRAME_SRC_URL, url.trim());
    }
}
%>
