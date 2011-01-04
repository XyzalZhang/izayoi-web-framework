/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF
 *
 * ANY KIND, either express or implied. See the License for the specific language governing rights and
 *
 * limitations under the License.
 *
 * The Original Code is the IZAYOI web framework.
 *
 * The Initial Developer of the Original Code is
 *
 *   Mo Chen <withinsea@gmail.com>
 *
 * Portions created by the Initial Developer are Copyright (C) 2009-2010
 * the Initial Developer. All Rights Reserved.
 */

package org.withinsea.izayoi.rosace.core.kernel;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-16
 * Time: 3:46:19
 */
public class RosaceConstants {

    protected static final String PREFIX = (RosaceConstants.class.getCanonicalName() + ".").replaceAll("\\.", "_");

    /* specified names */
    public static final String VARIABLE_VARSTACK = "_varstack";
    public static final String VARIABLE_WRITER = "_outwriter";
    public static final String TAGNAME_ANONYMOUS = "_ANONYMOUS";

    /* percompiletime attributes */
    public static final String ATTR_LOCKED = PREFIX + "LOCKED";
    public static final String ATTR_ELTYPE = PREFIX + "ELTYPE";
    public static final String ATTR_IMPORTS = PREFIX + "IMPORTS";

    /* runtime attributes */
    public static final String ATTR_TEMPLATE_PATH = PREFIX + "ATTR_TEMPLATE_PATH";
    public static final String ATTR_INCLUDE_SUPPORT = PREFIX + "ATTR_INCLUDE_SUPPORT";
    public static final String ATTR_INCLUDE_SECTION = PREFIX + "ATTR_INCLUDE_SECTION";
    public static final String ATTR_INCLUDED_CONTEXT = PREFIX + "ATTR_INCLUDED_CONTEXT";
}
