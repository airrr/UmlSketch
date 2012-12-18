package parser;

import java.util.ArrayList;

public class MethodRepresentation {

    private String name;

    private String typeReturn;

    private String view;

    private ArrayList<String> typeArgs;

    private boolean isStatic = false;

    private boolean isAbstract = false;

    private boolean isFinal = false;

    public MethodRepresentation() {
        typeArgs = new ArrayList<String>();
    }

    public MethodRepresentation(String n, String strReturn, String v, ArrayList<String> args) {
        name = new String(n);
        typeReturn = new String(strReturn);
        typeArgs = new ArrayList<String>();
        view = new String(v);
        for (String s : args) {
            typeArgs.add(new String(s));
        }
    }

    public void setFinal() {
        isFinal = true;
    }

    public void setStatic() {
        isStatic = true;
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

    public void setReturn(String r) {
        typeReturn = new String(r);
    }

    public void addArg(String a) {
        typeArgs.add(new String(a));
    }

    public void setView(int a) {
        switch (a) {
            case 0:

            case 3:
                view = new String("private");
                break;
            case 1:
                view = new String("public");
                break;
            case 2:
                view = new String("protected");
                break;
        }
    }

    @Override
    public String toString() {
        String s = new String();

        s += view + " ";
        if (isAbstract) {
            s += "abstract ";
        }
        if (isStatic) {
            s += "static ";
        }
        if (isFinal) {
            s += "final ";
        }
        if (typeReturn != null) {
            s += typeReturn + " ";
        }

        s += name + ' ';

        s += "(";
        for (int i = 0; i < typeArgs.size() - 1; i++) {
            s += typeArgs.get(i) + ", ";
        }
        s += typeArgs.get(typeArgs.size() - 1);
        s += ")" + '\n';
        return s;
    }

}
