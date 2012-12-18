package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClassNode implements Iterable<ClassNode> {
    private ClassRepresentation data;

    private List<ClassNode> children;

    private int depth = 0;

    public ClassNode(ClassRepresentation c) {
        data = c;
        children = new ArrayList<ClassNode>();
    }

    public void addChild(ClassNode c) {
        children.add(c);

    }

    public ArrayList<ClassNode> getChild() {
        return (ArrayList<ClassNode>) children;
    }

    @Override
    public Iterator<ClassNode> iterator() {
        return children.iterator();
    }

    public ClassRepresentation getClassRepresentation() {
        return data;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public int computeDepth(int parent) {
        depth = parent + 1;
        int maxDepth = depth, a;
        for (ClassNode cn : children) {
            a = cn.computeDepth(depth);
            if (a > maxDepth) {
                maxDepth = a;
            }
        }
        return maxDepth;
    }

    public int getDepth() {
        return depth;
    }

    public int getNbChildAtDepth(int depth2) {
        int res = 0;
        if (depth2 > depth) {
            for (ClassNode cn : children) {
                res += cn.getNbChildAtDepth(depth2);
            }
        }
        else {
            if (depth == depth2) {
                res = 1;
            }
        }
        return res;
    }

    public void getNodesAtDepth(int depth2, ArrayList<ClassNode> list) {
        if (depth2 > depth) {
            for (ClassNode cn : children) {
                cn.getNodesAtDepth(depth2, list);
            }
        }
        else {
            if (depth == depth2) {
                list.add(this);
            }
        }
    }
}
