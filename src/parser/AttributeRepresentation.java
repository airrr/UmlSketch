package parser;

/**
 * 
 * 
 */
public class AttributeRepresentation {
    private String name;

    private String view;

    private String type;

    private boolean isFinal = false;

    private boolean isStatic = false;

    public AttributeRepresentation() {

    }

    public AttributeRepresentation(String n, String v) {
        name = new String(n);
        view = new String(v);
    }

    public void setName(String n) {
        name = new String(n);
    }

    public void setView(String n) {
        view = new String(n);
    }

    public void setType(String n) {
        type = new String(n);
    }

    public void setFinal() {
        isFinal = true;
    }

    public void setStatic() {
        isStatic = true;
    }

    @Override
    public String toString() {
        String s = new String();
        if (view == null) {
            view = "private";
        }
        s += view + " ";
        if (isStatic) {
            s += "static ";
        }
        if (isFinal) {
            s += "final ";
        }

        s += type + " " + name + '\n';

        return s;
    }
}
