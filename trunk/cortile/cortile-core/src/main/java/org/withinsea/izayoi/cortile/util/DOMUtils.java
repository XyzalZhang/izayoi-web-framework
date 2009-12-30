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

package org.withinsea.izayoi.cortile.util;

import org.dom4j.Branch;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.tree.AbstractBranch;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-20
 * Time: 16:18:08
 */
public class DOMUtils {

    protected static final Method ADD_NODE_AT; static {
        try {
            ADD_NODE_AT = AbstractBranch.class.getDeclaredMethod("addNode", int.class, Node.class);
            ADD_NODE_AT.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Branch parent(Node node) {
        Branch parent = node.getParent();
        if (parent == null) parent = node.getDocument();
        return parent;
    }

    public static DefaultText insertBefore(String text, Node node) throws InvocationTargetException, IllegalAccessException {
        DefaultText newNode = new DefaultText(text);
        insertBefore(newNode, node);
        return newNode;
    }

    public static Node insertBefore(Node newNode, Node node) throws InvocationTargetException, IllegalAccessException {
        Branch parent = parent(node);
        ADD_NODE_AT.invoke(parent, parent.indexOf(node), newNode);
        return newNode;
    }

    public static DefaultText insertAfter(String text, Node node) throws InvocationTargetException, IllegalAccessException {
        DefaultText newNode = new DefaultText(text);
        insertAfter(newNode, node);
        return newNode;
    }

    public static Node insertAfter(Node newNode, Node node) throws InvocationTargetException, IllegalAccessException {
        Branch parent = parent(node);
        ADD_NODE_AT.invoke(parent, parent.indexOf(node) + 1, newNode);
        return newNode;
    }

    public static DefaultText replace(Node node, String text) throws InvocationTargetException, IllegalAccessException {
        DefaultText newNode = new DefaultText(text);
        replace(node, newNode);
        return newNode;
    }

    public static Node replace(Node node, Node newNode) throws InvocationTargetException, IllegalAccessException {
        insertBefore(newNode, node);
        node.detach();
        return newNode;
    }

    public static void surroundInside(Branch branch, String prefix, String suffix) throws InvocationTargetException, IllegalAccessException {
        prepend(branch, prefix);
        append(branch, suffix);
    }

    public static void surround(Node node, String prefix, String suffix) throws InvocationTargetException, IllegalAccessException {
        insertBefore(prefix, node);
        insertAfter(suffix, node);
    }

    public static DefaultElement surround(Node node, String name) throws InvocationTargetException, IllegalAccessException {
        DefaultElement wrapper = new DefaultElement(name);
        surround(node, wrapper);
        return wrapper;
    }

    public static Branch surround(Node node, Branch wrapper) throws InvocationTargetException, IllegalAccessException {
        replace(node, wrapper);
        wrapper.clearContent();
        wrapper.add(node);
        return wrapper;
    }

    public static DefaultText prepend(Branch parent, String text) throws InvocationTargetException, IllegalAccessException {
        DefaultText newNode = new DefaultText(text);
        prepend(parent, newNode);
        return newNode;
    }

    public static Node prepend(Branch parent, Node newNode) throws InvocationTargetException, IllegalAccessException {
        ADD_NODE_AT.invoke(parent, 0, newNode);
        return newNode;
    }

    public static DefaultText append(Branch parent, String text) throws InvocationTargetException, IllegalAccessException {
        DefaultText newNode = new DefaultText(text);
        append(parent, newNode);
        return newNode;
    }

    public static Node append(Branch parent, Node newNode) throws InvocationTargetException, IllegalAccessException {
        parent.add(newNode);
        return newNode;
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> selectTypedNodes(Class<T> type, Branch root) {
        Set<T> nodes = new LinkedHashSet<T>();
        if (type.isInstance(root)) {
            nodes.add((T) root);
        }
        for (Node node : (List<Node>) root.content()) {
            if (node instanceof Branch) {
                nodes.addAll(selectTypedNodes(type, (Branch) node));
            } else if (type.isInstance(node)) {
                nodes.add((T) node);
            }
        }
        return nodes;
    }

    @SuppressWarnings("unchecked")
    public static void mergeTexts(Branch root) {
        List<Node> nodes = (List<Node>) root.content();
        for (int i = nodes.size() - 2; i >= 0; i--) {
            Node node = nodes.get(i);
            Node next = nodes.get(i + 1);
            if (node instanceof Text && next instanceof Text) {
                node.setText(node.getText() + next.getText());
                next.detach();
            }
        }
        for (Node node : (List<Node>) root.content()) {
            if (node instanceof Branch) {
                mergeTexts((Branch) node);
            }
        }
    }
}