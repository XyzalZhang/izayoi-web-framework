<%--
  ~ The contents of this file are subject to the Mozilla Public License
  ~ Version 1.1 (the "License"); you may not use this file except in compliance
  ~ with the License. You may obtain a copy of the License at
  ~
  ~ http://www.mozilla.org/MPL/
  ~
  ~ Software distributed under the License is distributed on an "AS IS"
  ~ basis, WITHOUT WARRANTY OF
  ~
  ~ ANY KIND, either express or implied. See the License for the specific language governing rights and
  ~
  ~ limitations under the License.
  ~
  ~ The Original Code is the IZAYOI web framework.
  ~
  ~ The Initial Developer of the Original Code is
  ~
  ~   Mo Chen <withinsea@gmail.com>
  ~
  ~ Portions created by the Initial Developer are Copyright (C) 2009-2010
  ~ the Initial Developer. All Rights Reserved.
  --%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
    Map<String, Object> glowworm = (Map<String, Object>) request.getAttribute("global");
    String recommendTypes = request.getAttribute("recommendTypes").toString();
%>

<p>
    <strong>inject type:</strong> ${INJECT_TYPE}<br />
    <strong>required jdk:</strong> jdk ${requiredJdkVersion}+<br />
    <strong>generate time:</strong> ${time}
</p>

<h1>${title}</h1>

<h2>${subtitle}</h2>

<p>${description}</p>

<ul>
    <% for (Map<String, Object> type : (List<Map<String, Object>>) request.getAttribute("types")) { %>
    <li>
        <%= type.get("extnames") %> <strong><%= type.get("typename") %>
    </strong>
        <% if ((Boolean) type.get("deserializer")) { %>(deserializer injector)<% } %>
        <% if (type.get("typename").toString().matches(recommendTypes)) { %>[recommend]<% } %>
    </li>
    <% } %>
</ul>
