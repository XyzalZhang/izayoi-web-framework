<%@ page import="org.withinsea.izayoi.common.util.Vars" %>
<%!
    String name;
    Vars execute() {
        return new Vars(
                "name", (name == null) ? "withinsea" : name
        );
    }
%>