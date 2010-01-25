<%@ page import="java.util.*, java.util.regex.Pattern" %>
<%!
    public static class TypeItem extends HashMap<String, Object> {

        private String typename;
        private String[] extnames;
        private boolean deserializer;

        public TypeItem(String typename, String[] extnames, boolean deserializer) {
            this.put("typename", typename);
            this.put("extnames", Arrays.asList(extnames));
            this.put("deserializer", deserializer);
        }
    }
%>
<%

    request.setAttribute("INJECT_TYPE", "JSP");

    request.setAttribute("title", "glowworm tutorial");
    request.setAttribute("subtitle", "datatypes");
    request.setAttribute("description", "a tutorial about supported data types in glowworm.");
    request.setAttribute("requiredJdkVersion", 1.6);

    request.setAttribute("recommendTypes", Pattern.compile("JSON|MVEL2"));
    request.setAttribute("time", new Date());

    List<TypeItem> types = new ArrayList<TypeItem>();

    types.add(new TypeItem("JSON", new String[]{"json", "js"}, true));
    types.add(new TypeItem("Properties", new String[]{"properties"}, true));
    types.add(new TypeItem("JSP", new String[]{"jsp"}, false));
    types.add(new TypeItem("MVEL2", new String[]{"mvel2"}, false));
    types.add(new TypeItem("Text", new String[]{"txt"}, true));

    request.setAttribute("types", types);

%>