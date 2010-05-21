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

package org.withinsea.izayoi.core.code;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-15
 * Time: 1:52:34
 */
public class MemoryCode implements Code {

    protected final String code;
    protected final String type;

    public MemoryCode(String code, String type) {
        this.code = code;
        this.type = type;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public long getLastModified() {
        return -1;
    }

    @Override
    public Path getPath() {
        return null;
    }
}
