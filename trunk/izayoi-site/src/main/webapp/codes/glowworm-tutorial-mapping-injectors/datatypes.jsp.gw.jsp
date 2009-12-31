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