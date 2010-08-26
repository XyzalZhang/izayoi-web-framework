<%@ page import="org.withinsea.izayoi.core.interpret.Vars" %>
<%@ page import="java.util.Date" %>
<%!
    Date aSpringManagedObject;

    Vars execute() {
        return new Vars(
                "injectedDate", aSpringManagedObject
        );
    }
%>                           