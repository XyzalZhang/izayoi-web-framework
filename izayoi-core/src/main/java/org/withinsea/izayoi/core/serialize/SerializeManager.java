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

package org.withinsea.izayoi.core.serialize;

import org.withinsea.izayoi.core.exception.IzayoiException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-8
 * Time: 14:27:05
 */
public interface SerializeManager {

    public String findType(String contentType);

    public boolean isSerializable(String type);

    public void serialize(Class<?> claz, Object obj, String asType, OutputStream os, String encoding) throws IzayoiException;

    public void serialize(Class<?> claz, Object obj, String asType, Writer writer) throws IzayoiException;

    public <T> T deserialize(Class<T> claz, String asType, InputStream is, String encoding) throws IzayoiException;

    public <T> T deserialize(Class<T> claz, String asType, Reader reader) throws IzayoiException;
}