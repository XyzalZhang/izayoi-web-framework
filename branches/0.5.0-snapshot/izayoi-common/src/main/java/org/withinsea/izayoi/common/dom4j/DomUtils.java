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

package org.withinsea.izayoi.common.dom4j;

import org.dom4j.Branch;
import org.dom4j.Node;
import org.dom4j.Text;
import org.dom4j.tree.AbstractBranch;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-20
 * Time: 16:18:08
 */
public class DomUtils {

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

    public static DefaultText replaceBy(Node node, String text) throws InvocationTargetException, IllegalAccessException {
        DefaultText newNode = new DefaultText(text);
        replaceBy(node, newNode);
        return newNode;
    }

    public static Node replaceBy(Node node, Node newNode) throws InvocationTargetException, IllegalAccessException {
        insertBefore(newNode, node);
        node.detach();
        return newNode;
    }

    public static Node replaceBy(List<Node> nodes, Node newNode) throws InvocationTargetException, IllegalAccessException {
        insertBefore(newNode, nodes.get(0));
        for (Node node : nodes) {
            node.detach();
        }
        return newNode;
    }

    public static void surroundInsideBy(Branch branch, String prefix, String suffix) throws InvocationTargetException, IllegalAccessException {
        prepend(branch, prefix);
        append(branch, suffix);
    }

    @SuppressWarnings("unchecked")
    public static void surroundInsideBy(Branch branch, Branch wrapper) throws InvocationTargetException, IllegalAccessException {
        surroundBy(new ArrayList<Node>((List<Node>) branch.content()), wrapper);
    }

    public static void surroundBy(Node node, String prefix, String suffix) throws InvocationTargetException, IllegalAccessException {
        insertBefore(prefix, node);
        insertAfter(suffix, node);
    }

    public static DefaultElement surroundBy(Node node, String name) throws InvocationTargetException, IllegalAccessException {
        DefaultElement wrapper = new DefaultElement(name);
        surroundBy(node, wrapper);
        return wrapper;
    }

    public static Branch surroundBy(Node node, Branch wrapper) throws InvocationTargetException, IllegalAccessException {
        return surroundBy(Arrays.asList(node), wrapper);
    }

    public static Branch surroundBy(List<Node> nodes, Branch wrapper) throws InvocationTargetException, IllegalAccessException {
        replaceBy(nodes, wrapper);
        wrapper.setContent(nodes);
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
    public static <T> Collection<T> selectTypedNodes(Class<T> type, Branch root, boolean preorder) {
        Set<T> nodes = new LinkedHashSet<T>();
        if (preorder && type.isInstance(root)) {
            nodes.add((T) root);
        }
        for (Node node : (List<Node>) root.content()) {
            if (node instanceof Branch) {
                nodes.addAll(selectTypedNodes(type, (Branch) node, preorder));
            } else if (type.isInstance(node)) {
                nodes.add((T) node);
            }
        }
        if (!preorder && type.isInstance(root)) {
            nodes.add((T) root);
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

    public static boolean isDetached(Node node) {
        return (node.getParent() == null && node.getDocument() == null);
    }
}