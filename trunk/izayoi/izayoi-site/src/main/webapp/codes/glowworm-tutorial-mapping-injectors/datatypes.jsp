<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    Map<String, Object> glowworm =
            (Map<String, Object>) request.getAttribute("glowworm");
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
    <% for (Map<String, Object> type :
            (List<Map<String, Object>>) request.getAttribute("types")) { %>
    <li>
        <%= type.get("extnames") %>
        <%= type.get("typename") %>
        <% if ((Boolean) type.get("deserializer")) { %>
        (deserializer injector)
        <% } %>
        <% if (type.get("typename").toString().matches(recommendTypes)) { %>
        [recommend]
        <% } %>
    </li>
    <% } %>
</ul>
