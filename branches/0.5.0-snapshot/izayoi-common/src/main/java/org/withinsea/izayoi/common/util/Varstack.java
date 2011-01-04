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

package org.withinsea.izayoi.common.util;

import javax.script.Bindings;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-22
 * Time: 16:58:19
 */
public class Varstack implements Bindings, Map<String, Object> {

    protected final Deque<Map<String, Object>> stack = new LinkedList<Map<String, Object>>();
    protected final Vars constants = new Vars();

    public Varstack() {
        this(Collections.<String, Object>emptyMap());
    }

    public Varstack(Map<String, Object> constants) {
        push(new Vars());
        this.constants.putAll(constants);
    }

    public Vars getConstants() {
        return constants;
    }

    public void push() {
        push(new Vars());
    }

    public void push(Map<String, Object> vars) {
        stack.push(vars);
    }

    @SuppressWarnings("unchecked")
    public void push(Object... pairs) {
        if (pairs.length == 1 && pairs[0] instanceof Map) {
            push((Map<String, Object>) pairs[0]);
        } else {
            push(new Vars(pairs));
        }
    }

    public Map<String, Object> pop() {
        return stack.pop();
    }

    public Map<String, Object> peek() {
        return stack.peek();
    }

    public int size() {
        return keySet().size();
    }

    public boolean isEmpty() {
        if (!constants.isEmpty()) {
            return false;
        }
        for (Map<String, Object> vars : stack) {
            if (!vars.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean containsKey(Object key) {
        if (constants.containsKey(key)) {
            return true;
        }
        for (Map<String, Object> vars : stack) {
            if (vars.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(Object value) {
        if (constants.containsValue(value)) {
            return true;
        }
        for (Map<String, Object> vars : stack) {
            if (vars.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    public Object get(Object key) {
        if (constants.containsKey(key.toString())) {
            return constants.get(key);
        }
        for (Map<String, Object> vars : stack) {
            if (vars.containsKey(key.toString())) {
                return vars.get(key);
            }
        }
        return null;
    }

    public Object put(String key, Object value) {
        return stack.peek().put(key, value);
    }

    public Object remove(Object key) {
        return stack.peek().remove(key);
    }

    public void putAll(Map<? extends String, ?> m) {
        stack.peek().putAll(m);
    }

    public void clear() {
        stack.peek().clear();
    }

    public Set<String> keySet() {
        Set<String> set = new HashSet<String>();
        set.addAll(constants.keySet());
        for (Map<String, Object> vars : stack) {
            set.addAll(vars.keySet());
        }
        return set;
    }

    public Collection<Object> values() {
        Set<Object> set = new HashSet<Object>();
        set.addAll(constants.values());
        for (Map<String, Object> vars : stack) {
            set.addAll(vars.values());
        }
        return set;
    }

    public Set<Entry<String, Object>> entrySet() {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<Map<String, Object>> iter = stack.descendingIterator();
        while (iter.hasNext()) {
            map.putAll(iter.next());
        }
        map.putAll(constants);
        return map.entrySet();
    }
}