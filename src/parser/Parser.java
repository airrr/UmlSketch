package parser;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Parser {

	private File directory;

	private String outputDir = "/home/ividev3/UML/";

	private ArrayList<ClassTree> classes = new ArrayList<ClassTree>();

	public Parser(String dir) {
		if (dir == null) {
			throw new IllegalArgumentException("Dossier introuvable");
		}
		directory = new File(dir);

	}

	public void run() {
		System.out.println("Processing directory ...");
		System.out.println("Parsing files ... ");
		ArrayList<ClassRepresentation> cr = new ArrayList<ClassRepresentation>();
		if (directory.isFile()) {
			try {
				processFile(directory, cr);
			} catch (Exception e) {
				System.out.println("Exception caught for file : " + directory.getName());
			}
		} else {
			for (File f : directory.listFiles()) {
				if (f.isDirectory()) {
					processFolder(f, cr);
				} else {
					try {
						processFile(f, cr);
					} catch (Exception e) {
						System.out.println("Exception caught for file : " + f.getName());
					}
				}
			}
		}
		buildClassTree(cr);

		drawClassTreeSingleFile(classes);
	}

	private void drawClassTree(ArrayList<ClassTree> cl) {
		System.out.println("Drawing class trees ... ");

		BufferedImage b = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) b.getGraphics();
		int w, h, number = 0;
		for (ClassTree c : cl) {
			h = c.getHeight(g);
			w = c.getWidth(g);

			BufferedImage out = new BufferedImage(w + 20, h + 20, BufferedImage.TYPE_INT_RGB);
			Graphics2D gout = (Graphics2D) out.getGraphics();
			c.draw(gout, 20, 10);

			File f = new File(outputDir + number + ".jpg");
			try {
				ImageIO.write(out, "jpg", f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			number++;
		}
		System.out.println("Class trees drawn in " + outputDir);
	}

	public void drawClassTreeSingleFile(ArrayList<ClassTree> cl) {
		System.out.println("Drawing class trees ... ");

		BufferedImage b = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) b.getGraphics();
		int h = 0;
		int w = 0;
		int wMax = 1600;
		int hEnd = 0;
		for (ClassTree c : cl) {
			if ((w + c.getWidth(g) + 20) < wMax) {
				w += c.getWidth(g) + 20;
				if (h + c.getHeight(g) > hEnd) {
					hEnd = h + c.getHeight(g);
				}
			} else {
				w = 0;
				h += c.getHeight(g);
			}

		}

		BufferedImage out = new BufferedImage(wMax, hEnd, BufferedImage.TYPE_INT_RGB);
		Graphics2D gout = (Graphics2D) out.getGraphics();
		gout.setBackground(Color.WHITE);
		h = 0;
		w = 0;
		for (ClassTree c : cl) {
			if ((w + c.getWidth(g) + 20) < wMax) {
				w += c.getWidth(g) + 20;
			} else {
				w = 0;
				h += c.getHeight(g);
			}
			c.draw(gout, w, h);
		}

		File f = new File(outputDir + "Diag.jpg");
		try {
			ImageIO.write(out, "jpg", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Class trees drawn in " + outputDir);
	}

	private void buildClassTree(ArrayList<ClassRepresentation> cr) {
		System.out.println("Building classes trees ...");
		int i = 0, temp;
		ClassRepresentation currentClass;
		while (cr.size() > 0) {
			i = 0;
			// Cleaning classes without extensions / implementations
			while (i < cr.size()) {
				currentClass = cr.get(i);
				if (!currentClass.hasParent()) {

					ClassTree ct = new ClassTree(currentClass);
					cr.remove(currentClass);
					checkChildren(currentClass.getName(), ct.getRoot(), cr);
					ct.computeDepth();
					classes.add(ct);
				}
				i++;
			}
			// Cleaning classes having a father which have father not inside the directory

			i = 0;
			while (i < cr.size()) {
				currentClass = cr.get(i);

				String parent = currentClass.getParent();
				if ((temp = findHeadParent(parent, cr, i)) != i) {
					currentClass = cr.get(temp);
					ClassTree ct = new ClassTree(currentClass);
					cr.remove(currentClass);
					checkChildren(currentClass.getName(), ct.getRoot(), cr);
					ct.computeDepth();
					classes.add(ct);
				} else {
					ClassTree ct = new ClassTree(currentClass);
					cr.remove(currentClass);
					checkChildren(currentClass.getName(), ct.getRoot(), cr);
					ct.computeDepth();
					classes.add(ct);
				}
				i++;
			}

		}
		System.out.println("Class tree built");
	}

	private int findHeadParent(String parentName, ArrayList<ClassRepresentation> cr, int currentId) {

		for (int i = 0; i < cr.size(); i++) {
			if (cr.get(i).getName().equals(parentName)) {
				if (cr.get(i).hasParent()) {
					return findHeadParent(cr.get(i).getParent(), cr, i);
				} else {
					return i;
				}
			}
		}
		return currentId;
	}

	private void checkChildren(String parentName, ClassNode ct, ArrayList<ClassRepresentation> cr) {
		for (ClassRepresentation cl : cr) {
			if (cl.hasParent()) {
				if (cl.getParent().equals(parentName)) {
					ct.addChild(new ClassNode(cl));
				}
			}
		}
		for (ClassNode cn : ct) {
			cr.remove(cn.getClassRepresentation());
			checkChildren(cn.getClassRepresentation().getName(), cn, cr);
		}
	}

	private void processFolder(File currentFile, ArrayList<ClassRepresentation> cr) {
		for (File f : currentFile.listFiles()) {
			if (f.isDirectory()) {
				processFolder(f, cr);
			} else {
				try {
					processFile(f, cr);
				} catch (Exception e) {
					System.out.println("Exception caught for file : " + f.getName());
				}
			}
		}
	}

	private void processFile(File f, ArrayList<ClassRepresentation> cr) throws Exception {
		String fileName = f.getName();

		String n[] = fileName.split("\\.");
		if (n.length == 1) {

			return;
		}
		if (!n[n.length - 1].equals("java")) {
			return;
		}
		// We assume the class is built correctly
		// Building reader
		BufferedReader buff = null;
		try {
			buff = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

		String all = new String();
		String temp;
		boolean commentLine = false;
		try {
			while ((temp = buff.readLine()) != null) {
				temp = temp.replaceAll("^[\t ]+", "");
				temp = temp.replaceAll("//.*", " ");
				if (temp.matches("/\\*.*?")) {
					commentLine = true;
				}
				if (temp.matches("([^*]|(\\*([^/]|[\r\n])))*?\\*/")) {
					commentLine = false;
					continue;
				}

				if (temp.matches("import.*?;") || temp.matches("package.*?;") || temp.matches("[\t ]+")
						|| temp.matches("\\*.*?") || commentLine) {
					continue;
				} else {
					all += temp.replaceAll("/\\*(.|[\n])+\\*/", "");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (StackOverflowError e) {

		}
		processClass(all, cr);

	}

	public static String removeCharAt(String s, int pos) {
		StringBuffer buf = new StringBuffer(s.length() - 1);
		buf.append(s.substring(0, pos)).append(s.substring(pos + 1));
		return buf.toString();
	}

	public void processClass(String all, ArrayList<ClassRepresentation> cr) {
		ClassRepresentation currentClass = new ClassRepresentation();
		String[] w = all.split("\\{");
		String classHead = w[0];
		String classBody = new String("");
		classBody = concatStrings(w, classBody, 1, w.length, "{");

		if (classBody.length() >= 2) {
			classBody = removeCharAt(classBody, classBody.length() - 1);
			classBody = removeCharAt(classBody, classBody.length() - 1);
		}

		processClassHeader(currentClass, classHead);
		if (currentClass.isEnum()) {
			processEnumBody(currentClass, classBody);
		} else {
			processClassBody(currentClass, classBody);
		}
		cr.add(currentClass);
	}

	private void processEnumBody(ClassRepresentation res, String s) {
		s = cleanInsideBrackets(s);
		s = s.replaceAll("\\(.*\\)|;", "");
		String[] words = s.split(",");
		for (String w : words) {
			AttributeRepresentation att = new AttributeRepresentation();
			att.setName(w);
			att.setType("int");
			att.setView("public");
			res.addAttribute(att);
		}
	}

	private void processClassBody(ClassRepresentation res, String s) {
		s = cleanInsideBrackets(s);
		String[] words = s.split("\\=.*?;|[ ]*;");
		for (String str : words) {
			if (str.isEmpty()) {
				continue;
			}
			if (str.contains("(")) {
				processMethod(res, str);

			} else {
				if (str.split(" ").length > 1) {
					processAttribute(res, str);
				}
			}
		}
	}

	private void processAttribute(ClassRepresentation res, String str) {
		String[] words, temp;
		String ww = new String();
		str = str.replaceAll("<.*>", "");
		str = str.replaceAll("  ", " ");

		if (str.contains(",")) {

			words = str.split(",");
			processAttribute(res, words[0]);

			temp = words[0].split(" ");
			ww = concatStrings(temp, ww, 0, temp.length - 1, " ");
			for (int i = 1; i < words.length; i++) {
				ww = ww + words[i];
				processAttribute(res, ww);
			}
			return;
		}
		AttributeRepresentation att = new AttributeRepresentation();

		words = str.split(" ");
		for (String w : words) {
			if (isStatic(w)) {
				att.setStatic();
			}
			if (isFinal(w)) {
				att.setFinal();
			}
		}
		if (isView(words[0]) > 0) {
			att.setView(words[0]);
			att.setType(words[words.length - 2]);
			att.setName(words[words.length - 1]);
		} else {
			att.setView("private");
			att.setType(words[words.length - 2]);
			att.setName(words[words.length - 1]);
		}
		res.addAttribute(att);
	}

	private void processMethod(ClassRepresentation res, String str) {
		MethodRepresentation method = new MethodRepresentation();
		// Cleaning any parameterized types
		String currentW = new String();
		currentW = str.replaceAll("<.*?>", "");

		String[] words = currentW.split("\\(");

		// Process MethodHead
		String mHead = words[0];

		String[] header = mHead.split(" ");
		ArrayList<String> head = new ArrayList<String>(header.length);
		for (int i = 0; i < header.length; i++) {
			head.add(header[i]);
		}
		ArrayList<String> toRemove = new ArrayList<String>();
		int typeView = 0;
		// Check visibility
		for (String current : head) {
			if ((isView(current)) > 0) {
				typeView = isView(current);
				toRemove.add(current);
			}
			if (isAbstract(current)) {
				method.setAbstract();
				toRemove.add(current);
			}
			if (isStatic(current)) {
				method.setStatic();
				toRemove.add(current);
			}
			if (isFinal(current)) {
				method.setFinal();
				toRemove.add(current);
			}
		}

		for (String astr : toRemove) {
			head.remove(astr);
		}

		if (head.size() == 1) {
			method.setName(head.get(0));
		} else {
			method.setReturn(head.get(0));
			method.setName(head.get(1));
		}

		method.setView(typeView);

		// Process MethodBody

		currentW = words[1];

		currentW = currentW.replaceAll("\\)", "");

		words = currentW.split(",");

		for (String w : words) {
			method.addArg(w);
		}
		res.addMethod(method);

	}

	private String cleanInsideBrackets(String s) {
		int nbBrack = 0;
		int i = 0;
		int length = s.length();
		StringBuffer buf = new StringBuffer(s.length());
		while (i < length) {

			if (s.charAt(i) == '{') {
				nbBrack++;
			}
			if (nbBrack == 0) {
				buf.append(s.charAt(i));
			}
			if (s.charAt(i) == '}') {
				nbBrack--;
				if (nbBrack == 0) {
					buf.append(';');
				}
			}
			i++;
		}

		return buf.toString();

	}

	private void processClassHeader(ClassRepresentation res, String s) {
		String[] words = s.split(" ");
		int classFound = 0;
		int classType;
		String currentW;
		for (int i = 0; i < words.length; i++) {
			currentW = words[i];

			// Has keyword modifier ?
			if (isAbstract(currentW)) {
				res.setAbstract();
			}

			// Has view modifier
			if (isView(currentW) > 0) {
				res.setView(currentW);
				continue;
			}
			// Found class/interface keyword, next word is className
			if ((classType = isClass(currentW)) > 0) {
				classFound = i;
				if (classType == 2) {
					res.setInterface();
				}
				if (classType == 3) {
					res.setEnum();
				}
				break;
			}
		}
		currentW = new String();

		currentW = concatStrings(words, currentW, classFound + 1, words.length, " ");

		// Remove any type parameters (boring)

		words = currentW.split("<.*?>");

		currentW = concatStrings(words, currentW, " ");
		currentW = currentW.replaceAll("  ", " ");
		words = currentW.split(" ");

		// Class name

		res.setName(words[0]);

		if (words.length > 1) {
			if (words[1].equals("extends")) {
				res.setParent(words[2]);
				if (words.length > 3) {
					if (words[3].equals("implements")) {
						for (int i = 4; i < words.length; i++) {
							res.addInterface(words[i]);
						}
					}
				}
			}

			if (words[1].equals("implements")) {
				for (int i = 3; i < words.length; i++) {
					res.addInterface(words[i]);
				}
			}
		}
	}

	private String concatStrings(String s[], String res, String toAdd) {
		res = "";
		for (String st : s) {
			res += (st + toAdd);
		}
		return res;

	}

	private String concatStrings(String s[], String res, int startIndex, int endIndex, String toAdd) {

		for (int i = startIndex; i < endIndex; i++) {
			res += (s[i] + toAdd);
		}
		return res;
	}

	private boolean isAbstract(String s) {
		if (s == null) {
			return false;
		}

		return s.equals("abstract");
	}

	private boolean isStatic(String s) {
		if (s == null) {
			return false;
		}

		return s.equals("static");
	}

	private boolean isFinal(String s) {
		return s.equals("final");
	}

	private int isClass(String s) {
		if (s == null) {
			return 0;
		}
		if (s.equals("class")) {
			return 1;
		}
		if (s.equals("interface")) {
			return 2;
		}
		if (s.equals("enum")) {
			return 3;
		}
		return 0;
	}

	private int isView(String s) {
		if (s == null) {
			return 0;
		}
		if (s.equals("public")) {
			return 1;
		}
		if (s.equals("protected")) {
			return 2;
		}
		if (s.equals("private")) {
			return 3;
		}
		return 0;
	}

	public static void main(String args[]) {

		Parser p = new Parser("/home/ividev3/Dev/mainWorkspace/seedlist/SeedList/src/org/ividence/seedlist/core/");
		p.run();

		/*
		 * String test =
		 * "public PrincipalAxis(MeshAccessor0D<? extends Geometry> mesh) {if (mesh == null) {throw new InvalidParameterException();}if (mesh.getMesh() == null) {throw new InvalidParameterException();}acc = mesh;nbPoints = mesh.nbElements();org = new Point3d(0, 0, 0);bary = new Point3d(0, 0, 0);u = new Vector3d();v = new Vector3d();}"
		 * ;
		 * test = test.replaceAll("\\{(.*|\n)\\}", "");
		 * String[] words = test.split("\\=.*?;|;");
		 * for (String w : words) {
		 * System.out.println(w);
		 * }
		 */
	}
}
