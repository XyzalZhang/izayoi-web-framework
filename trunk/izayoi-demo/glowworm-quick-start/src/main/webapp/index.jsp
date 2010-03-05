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

<h1>${project}</h1>

<p>Hello, <%= request.getAttribute("user") %>!</p>

<ul>
    <li><a href="dependency.jsp">dependency.jsp</a></li>
    <li><a href="blog/user1337/develop/index.jsp">blog/user1337/develop/index.jsp</a></li>
    <li><a href="script.jsp.data.groovy">script.jsp (groovy)</a></li>
    <li><a href="script.jsp.data.mvel2">script.jsp (mvel2)</a></li>
    <li><a href="script.jsp.data.rb">script.jsp (ruby)</a></li>
</ul>
