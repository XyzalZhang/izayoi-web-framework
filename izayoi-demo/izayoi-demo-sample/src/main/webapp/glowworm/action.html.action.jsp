<%@ page import="org.withinsea.izayoi.core.interpret.Vars" %>
<%@ page import="java.util.Arrays" %>
<%!
    Object execute() {

        return Arrays.<Object>asList(

                new Vars(
                        "injectedObject", new java.util.Date()
                ),

                "forward: action-target.html"
        );
    }
%>
