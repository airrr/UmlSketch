package parser;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class ClassRepresentation {

    private boolean isInterface = false;

    private boolean isAbstract = false;

    private boolean isEnum = false;

    private String view = new String("private");

    private String name;

    private ArrayList<MethodRepresentation> methods;

    private ArrayList<AttributeRepresentation> attributs;

    private String parent;

    private ArrayList<String> interfaces;

    public ClassRepresentation() {
        methods = new ArrayList<MethodRepresentation>();
        attributs = new ArrayList<AttributeRepresentation>();
        interfaces = new ArrayList<String>();
    }

    public ClassRepresentation(String n, String v) {
        view = new String(v);
        name = new String(n);
        methods = new ArrayList<MethodRepresentation>();
        attributs = new ArrayList<AttributeRepresentation>();
        interfaces = new ArrayList<String>();
    }

    public void addMethod(MethodRepresentation m) {
        methods.add(m);
    }

    public void addAttribute(AttributeRepresentation a) {
        attributs.add(a);
    }

    public void setInterface() {
        isInterface = true;
    }

    public void setEnum() {
        isEnum = true;
    }

    public boolean isEnum() {
        return isEnum == true;
    }

    public void setAbstract() {
        isAbstract = true;
    }

    public void setName(String n) {
        name = new String(n);
    }

    public void setView(String v) {
        view = new String(v);
    }

    public String getView() {
        return view;
    }

    public void setParent(String s) {
        parent = new String(s);
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public void addInterface(String s) {
        interfaces.add(new String(s));
    }

    public boolean hasParent() {
        return !(parent == null);
    }

    private String getAttString() {
        String aStr = new String();

        for (AttributeRepresentation a : attributs) {
            aStr += a.toString();
        }
        return aStr;
    }

    private String getMethodString() {
        String mStr = new String();
        for (MethodRepresentation m : methods) {
            mStr += m.toString();
        }
        return mStr;
    }

    private String getNameString() {
        String fullName = new String();
        fullName += view + " ";
        if (isAbstract) {
            fullName += "Abstract ";
        }
        if (isInterface) {
            fullName += "Interface ";
        }
        else {
            if (isEnum) {
                fullName += "Enum ";
            }
            else {
                fullName += "Class ";
            }
        }
        fullName += name + '\n';

        if (hasParent()) {
            fullName += "extends " + parent + '\n';
        }
        if (interfaces.size() > 0) {
            fullName += "implements ";
            for (int i = 0; i < interfaces.size() - 1; i++) {
                fullName += (interfaces.get(i) + ", ");
            }
            fullName += interfaces.get(interfaces.size() - 1);
        }
        return fullName;
    }

    public int evaluateHeight(Graphics2D g) {
        int h = 0;

        String fullName = getNameString();
        String aStr = getAttString();
        String mStr = getMethodString();

        h += getStringHeight(g, fullName, Constants.classNameFont);
        h += getStringHeight(g, aStr, Constants.classAttributeFont);
        h += getStringHeight(g, mStr, Constants.classMethodFont);
        return h;
    }

    public int evaluateWidth(Graphics2D g) {
        int maxW = 0;
        int temp;

        String fullName = getNameString();
        String aStr = getAttString();
        String mStr = getMethodString();

        temp = getStringWidth(g, fullName, Constants.classNameFont);
        if (temp > maxW) {
            maxW = temp;
        }
        temp = getStringWidth(g, aStr, Constants.classAttributeFont);
        if (temp > maxW) {
            maxW = temp;
        }
        temp = getStringWidth(g, mStr, Constants.classMethodFont);
        if (temp > maxW) {
            maxW = temp;
        }
        return maxW;
    }

    public int getStringWidth(Graphics2D g, String text, Font f) {
        int width = 0;
        int temp;
        FontMetrics metrics = g.getFontMetrics(f);

        for (String line : text.split("\n")) {
            if ((temp = metrics.stringWidth(line)) > width) {
                width = temp;
            }

        }
        return width + 10;
    }

    public int getStringHeight(Graphics2D g, String text, Font f) {
        int height = 0;

        for (String line : text.split("\n")) {
            height += g.getFontMetrics(f).getHeight();

        }

        return height + 10;
    }

    public int drawString(Graphics2D g, String text, int x, int y) {
        int height = 0;

        for (String line : text.split("\n")) {
            height += g.getFontMetrics().getHeight();
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
        }
        return height;
    }

    public Graphics2D draw(Graphics2D g, int x, int y) {
        String fullName = getNameString();
        String aStr = getAttString();
        String mStr = getMethodString();

        Rectangle2D bounds = new Rectangle();
        int currentY = y + 20;
        int height;

        // Drawing Name
        int width = evaluateWidth(g);
        g.setFont(Constants.classNameFont);
        height = drawString(g, fullName, x, currentY) + 5;

        bounds.setRect(x - 5, currentY, width, height);
        g.draw(bounds);
        currentY += bounds.getHeight();

        // Drawing attributes
        g.setFont(Constants.classAttributeFont);
        height = drawString(g, aStr, x, currentY) + 5;
        bounds.setRect(x - 5, currentY, width, height);
        g.draw(bounds);
        currentY += bounds.getHeight();

        // Drawing methods
        g.setFont(Constants.classMethodFont);
        height = drawString(g, mStr, x, currentY) + 5;
        bounds.setRect(x - 5, currentY, width, height);
        g.draw(bounds);

        return g;
    }

    @Override
    public String toString() {
        String s = new String();
        if (isInterface) {
            s += "Interface \n";
        }
        else {
            s += "Class \n";
        }
        s += "Name : " + name + '\n';
        s += "View : " + view + '\n';
        if (isAbstract) {
            s += "Is Abstract" + '\n';
        }
        if (parent != null) {
            s += "Extends " + parent + '\n';
        }
        if (interfaces.size() > 0) {
            s += "Implements ";
            for (String inter : interfaces) {
                s += (inter + " ");
            }
            s += '\n';
        }
        if (methods.size() > 0) {
            s += "----------------" + '\n';
            s += "Methods : " + '\n';
            s += "----------------" + '\n';
            for (MethodRepresentation m : methods) {
                s += m.toString();
                s += "oooooo" + '\n';
            }
        }
        if (attributs.size() > 0) {
            s += "----------------" + '\n';
            s += "Attributes : " + '\n';
            s += "----------------" + '\n';
            for (AttributeRepresentation m : attributs) {
                s += m.toString();
                s += "oooooo" + '\n';
            }
        }
        return s;
    }

}
