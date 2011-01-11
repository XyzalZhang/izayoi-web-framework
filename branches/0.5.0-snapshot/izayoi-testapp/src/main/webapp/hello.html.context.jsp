<%@ page import="org.withinsea.izayoi.common.util.Vars" %>
<%!
    String helloName;
    Vars execute() {
        return new Vars(
                "helloName", (helloName == null) ? "izayoi" : helloName
        );
    }
%>