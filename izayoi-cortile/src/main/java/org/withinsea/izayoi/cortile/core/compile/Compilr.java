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

package org.withinsea.izayoi.cortile.core.compile;

import org.withinsea.izayoi.cortile.core.exception.CortileException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 2:22:29
 */
public interface Compilr {

    public static class Result {

        private final Map<String, String> targets = new HashMap<String, String>();
        private final Set<String> relativeTemplatePaths = new HashSet<String>();

        public Map<String, String> getTargets() {
            return targets;
        }

        public Set<String> getRelativeTemplatePaths() {
            return relativeTemplatePaths;
        }
    }

    String mapEntrancePath(String templatePath);

    Result compile(String templatePath, String templateCode) throws CortileException;
}