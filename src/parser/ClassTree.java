package parser;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class ClassTree {
    private ClassNode root;

    private int depth;

    public ClassTree(ClassRepresentation rootData) {
        root = new ClassNode(rootData);
    }

    public void addChild(ClassNode n) {
        root.addChild(n);
    }

    public ClassNode getRoot() {
        return root;
    }

    public void computeDepth() {
        depth = root.computeDepth(0);
    }

    public int getDepth() {
        return depth;
    }

    public int getNbChildAtDepth(int depth) {
        return root.getNbChildAtDepth(depth);
    }

    public ArrayList<ClassNode> getNodesAtDepth(int depth) {
        ArrayList<ClassNode> list = new ArrayList<ClassNode>();
        root.getNodesAtDepth(depth, list);
        return list;
    }

    public int getHeight(Graphics2D g) {
        int maxH = 0;
        int res = 0;
        int temp;
        ArrayList<ClassNode> currentNodeAtDepth;
        for (int i = 1; i <= depth; i++) {
            temp = 0;
            currentNodeAtDepth = getNodesAtDepth(i);
            for (int j = 0; j < currentNodeAtDepth.size(); j++) {
                temp = currentNodeAtDepth.get(j).getClassRepresentation().evaluateHeight(g);
                if (temp > maxH) {
                    maxH = temp;
                }
            }
            res += maxH + 10;
        }
        return res;
    }

    public int getWidth(Graphics2D g) {
        int maxW = 0;
        int temp;
        ArrayList<ClassNode> currentNodeAtDepth;
        for (int i = 1; i <= depth; i++) {
            temp = 0;
            currentNodeAtDepth = getNodesAtDepth(i);
            for (int j = 0; j < currentNodeAtDepth.size(); j++) {
                temp += currentNodeAtDepth.get(j).getClassRepresentation().evaluateWidth(g);
            }
            if (temp > maxW) {
                maxW = temp;
            }
        }
        return maxW;
    }

    public Graphics2D draw(Graphics2D g, int x, int y) {

        int maxH = 0;
        int temp = 0;
        ArrayList<ClassNode> currentNodeAtDepth;

        int currentW, currentH;

        currentW = x;
        currentH = y;

        for (int i = 1; i <= depth; i++) {
            currentNodeAtDepth = getNodesAtDepth(i);
            for (ClassNode c : currentNodeAtDepth) {
                c.getClassRepresentation().draw(g, currentW, currentH);
                currentW += 5 + c.getClassRepresentation().evaluateWidth(g);
                temp = c.getClassRepresentation().evaluateHeight(g);
                if (temp > maxH) {
                    maxH = temp;
                }
            }
            currentW = 0;
            currentH += maxH + 5;
        }
        return g;
    }
}
