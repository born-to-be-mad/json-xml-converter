package converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String SOURCE_FILE = "./test.txt";

    public static void main(String[] args) throws IOException {
        String input = readInput();

        ReaderWriter[] formats = new ReaderWriter[] {
            new XmlFormat(),
            new JsonFormat()
        };

        for (int i = 0; i < formats.length; i++) {
            if (formats[i].check(input)) {
                Node node = formats[i].read(input);
                if (node == null) {
                    return;
                }
                System.out.println(node.toFormat(formats[(i + 1) % formats.length]));
            }
        }
    }

    private static String readInput() throws IOException {
        return Files.readString(Paths.get(SOURCE_FILE));
    }
}

class Node {
    private String name;
    private Node parent;
    private String value;

    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<Node> children = new LinkedList<>();

    public Node() {
        this(null);
    }

    public Node(String name) {
        this(name, null);
    }

    public Node(String name, Node parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getParent() {
        return parent;
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Node addChild(String name) {
        return addChild(new Node(name));
    }

    public Node addChild(Node child) {
        child.setParent(this);
        children.add(child);
        return child;
    }

    public Node removeChild(Node child) {
        int i = children.indexOf(child);
        if (i < 0) {
            return null;
        }
        child.setParent(null);
        return children.remove(i);
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getPath() {
        StringBuilder path = new StringBuilder();
        Node node = this;
        boolean first = true;
        while (node != null) {
            if (node.getName() != null) {
                if (first) {
                    first = false;
                } else {
                    path.insert(0, ", ");
                }
                path.insert(0, node.getName());
            }
            node = node.getParent();
        }
        return path.toString();
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public Map<String, Node> getChildrenMap() {
        Map<String, Node> map = new LinkedHashMap<>();
        for (Node child : children) {
            map.put(child.getName(), child);
        }
        return map;
    }

    public List<Node> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        if (getName() != null) {
            out.append(String.format("Element:\npath = %s\n", getPath()));

            if (value == null) {
                if (children.isEmpty()) {
                    out.append("value = null\n");
                }
            } else {
                out.append(String.format("value = \"%s\"\n", value));
            }

            if (!attributes.isEmpty()) {
                out.append("attributes:\n");
                for (Map.Entry<String, String> attr : attributes.entrySet()) {
                    out.append(String.format("%s = \"%s\"\n",
                                             attr.getKey(),
                                             attr.getValue() == null ? "" : attr.getValue()));
                }
            }
        }

        for (Node child : children) {
            out.append("\n").append(child);
        }

        return out.toString();
    }

    public String toFormat(ReaderWriter rw) {
        return rw.write(this);
    }

}

class Tokenizer {
    private class TokenMatcher {
        private final Matcher matcher;

        public TokenMatcher(Pattern pattern) {
            matcher = pattern.matcher(source)
                             .useAnchoringBounds(true);
        }

        public boolean find() {
            if (check()) {
                cursor = matcher.end();
                return true;
            }
            return false;
        }

        public boolean check() {
            return matcher.region(cursor, source.length()).find();
        }

        public Matcher getMatcher() {
            return matcher;
        }
    }

    private int cursor;
    private Matcher matcher;
    private final String source;
    private final Map<Pattern, TokenMatcher> tokenMatchers = new HashMap<>();

    public Tokenizer(String source) {
        this(source, 0);
    }

    public Tokenizer(String source, int cursor) {
        this.source = source;
        this.cursor = cursor;
    }

    public boolean next(Pattern pattern) {
        TokenMatcher tokenMatcher = getTokenMatcher(pattern);
        matcher = tokenMatcher.find() ? tokenMatcher.getMatcher() : null;
        return matcher != null;
    }

    public boolean check(Pattern pattern) {
        TokenMatcher tokenMatcher = getTokenMatcher(pattern);
        matcher = tokenMatcher.check() ? tokenMatcher.getMatcher() : null;
        return matcher != null;
    }

    public boolean next(String pattern) {
        TokenMatcher tokenMatcher = new TokenMatcher(Pattern.compile(pattern));
        matcher = tokenMatcher.find() ? tokenMatcher.getMatcher() : null;
        return matcher != null;
    }

    public boolean check(String pattern) {
        TokenMatcher tokenMatcher = new TokenMatcher(Pattern.compile(pattern));
        matcher = tokenMatcher.check() ? tokenMatcher.getMatcher() : null;
        return matcher != null;
    }

    private TokenMatcher getTokenMatcher(Pattern pattern) {
        if (!tokenMatchers.containsKey(pattern)) {
            tokenMatchers.put(pattern, new TokenMatcher(pattern));
        }
        return tokenMatchers.get(pattern);
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public Matcher getMatcher() {
        return matcher;
    }
}

interface ReaderWriter {
    boolean check(String src);

    Node read(String src);

    String write(Node node);
}

class JsonFormat implements ReaderWriter {
    private static final Pattern JSON_BEGINNING = Pattern.compile("(?s)^\\s*\\{\\s*[\"}]");

    private static final Pattern OBJECT_BEGINNING = Pattern.compile("(?s)^\\s*\\{\\s*");
    private static final Pattern OBJECT_ENDING = Pattern.compile("(?s)^\\s*}\\s*,?");
    private static final Pattern OBJECT_ATTRIBUTE_ID = Pattern.compile("(?s)^\\s*\"(.*?)\"\\s*:\\s*");
    private static final Pattern OBJECT_ATTRIBUTE_VALUE =
        Pattern.compile("(?s)^\\s*(\"(.*?)\"|(\\d+\\.?\\d*)|(null)),?");

    private static final Pattern PATTERN_XML_ATTRIBUTE = Pattern.compile("(?i)^[#@][a-z_][.\\w]*");
    private static final Pattern PATTERN_XML_IDENTIFIER = Pattern.compile("(?i)^[a-z_][.\\w]*");

    @Override
    public boolean check(String src) {
        return JSON_BEGINNING.matcher(src).find();
    }

    @Override
    public Node read(String src) {
        return readObject(new Tokenizer(src), new Node());
    }

    @Override
    public String write(Node node) {
        return writeNode(new StringBuilder(), node).toString();
    }

    private static StringBuilder writeNode(StringBuilder out, Node node) {
        if (node.hasAttributes()) {
            out.append("{\n");

            for (Map.Entry<String, String> elem : node.getAttributes().entrySet()) {
                out.append(String.format("\"@%s\" : \"%s\",\n", elem.getKey(), elem.getValue()));
            }

            if (node.hasChildren()) {
                out.append(String.format("\"#%s\": ", node.getName()));
                writeChildren(out, node);
            } else if (node.getValue() == null) {
                out.append(String.format("\"#%s\" : null\n", node.getName()));
            } else {
                out.append(String.format("\"#%s\" : \"%s\"\n", node.getName(), node.getValue()));
            }

            out.append(" }");

        } else if (node.hasChildren()) {
            writeChildren(out, node);

        } else if (node.getValue() == null) {
            out.append("null");

        } else {
            out.append(String.format("\"%s\"", node.getValue()));

        }

        return out;
    }

    private static void writeChildren(StringBuilder out, Node node) {
        if (node.hasChildren()) {
            out.append("{\n");
        }

        Node child;
        for (int i = 0; i < node.getChildren().size(); i++) {
            child = node.getChildren().get(i);
            out.append(String.format("\"%s\" : ", child.getName()));
            writeNode(out, child);
            if (i != node.getChildren().size() - 1) {
                out.append(",\n");
            }
        }

        if (node.hasChildren()) {
            out.append(" }");
        }
    }

    private static boolean isObject(Node node) {
        return node.getName() == null || node.hasChildren() || node.hasAttributes();
    }

    private static Node readObject(Tokenizer tokenizer, Node parent) {
        if (!tokenizer.next(OBJECT_BEGINNING)) {
            return null;
        }

        Node node;
        Matcher matcher;
        while (tokenizer.next(OBJECT_ATTRIBUTE_ID)) {
            node = new Node(tokenizer.getMatcher().group(1));
            if (tokenizer.check(JSON_BEGINNING)) {
                readObject(tokenizer, node);
                processObject(node);

            } else if (tokenizer.next(OBJECT_ATTRIBUTE_VALUE)) {
                matcher = tokenizer.getMatcher();
                if (matcher.group(2) != null) { // string
                    node.setValue(matcher.group(2));
                } else if (matcher.group(3) != null) { // number
                    node.setValue(matcher.group(3));
                } else if (matcher.group(4) != null) { // null
                    node.setValue(null);
                } else {
                    throw new RuntimeException("Unknown attribute value.");
                }
            } else {
                throw new RuntimeException("Attribute value expected.");
            }
            parent.addChild(node);
        }

        if (!tokenizer.next(OBJECT_ENDING)) {
            throw new RuntimeException("Object end expected.");
        }

        return parent;
    }

    private static boolean isValidXmlAttribute(String name) {
        return name != null && PATTERN_XML_ATTRIBUTE.matcher(name).matches();
    }

    private static boolean isValidXmlIdentifier(String name) {
        return name != null && PATTERN_XML_IDENTIFIER.matcher(name).matches();
    }

    private static boolean isXmlAttributes(Node node) {
        Map<String, Node> map = node.getChildrenMap();
        if (!map.containsKey("#" + node.getName())) {
            return false;
        }
        for (Map.Entry<String, Node> elem : map.entrySet()) {
            if (!isValidXmlAttribute(elem.getKey())) {
                return false;
            }
            if (elem.getKey().charAt(1) == '@' && elem.getValue().hasChildren()) {
                return false;
            }
        }
        return true;
    }

    private static void processObject(Node node) {
        Node child;
        if (isXmlAttributes(node)) {
            for (Map.Entry<String, Node> elem : node.getChildrenMap().entrySet()) {
                child = elem.getValue();
                if (elem.getKey().charAt(0) == '#') {
                    if (child.hasChildren()) {
                        node.removeChild(child);
                        for (Node subChild : child.getChildrenMap().values()) {
                            node.addChild(subChild);
                        }
                    } else {
                        child = node.removeChild(elem.getValue());
                        node.setValue(child.getValue());
                    }
                } else {
                    child = node.removeChild(elem.getValue());
                    node.setAttribute(child.getName().substring(1), child.getValue());
                }
            }

        } else {
            Map<String, Node> childrenMap = node.getChildrenMap();
            for (Map.Entry<String, Node> elem : childrenMap.entrySet()) {
                if (isValidXmlAttribute(elem.getKey())) {
                    if (childrenMap.containsKey(elem.getKey().substring(1))) {
                        node.removeChild(elem.getValue());
                    } else {
                        elem.getValue().setName(elem.getValue().getName().substring(1));
                    }
                } else if (!isValidXmlIdentifier(elem.getKey())) {
                    node.removeChild(elem.getValue());
                }
            }
            if (!node.hasChildren()) {
                node.setValue("");
            }
        }

    }
}

class XmlFormat implements ReaderWriter {
    private static final Pattern XML_BEGINNING = Pattern.compile("(?s)^\\s*<\\s*[a-z_]\\w+");
    private static final Pattern TAG_OPEN =
        Pattern.compile("(?is)^\\s*<\\s*([a-z_]\\w+)\\s*([a-z_]\\w+\\s*=\\s*\".*?\")*\\s*(>|/>)");
    private static final Pattern ATTRIBUTES = Pattern.compile("(?is)([a-z_]\\w+)\\s*=\\s*\"(.*?)\"");

    @Override
    public boolean check(String src) {
        return XML_BEGINNING.matcher(src).find();
    }

    @Override
    public Node read(String src) {
        return readTags(new Tokenizer(src), new Node());
    }

    @Override
    public String write(Node node) {
        return writeNode(new StringBuilder(), node).toString();
    }

    private static StringBuilder writeNode(StringBuilder out, Node node) {
        String nodeName = node.getName();
        if (nodeName == null) {
            if (node.getChildren().size() > 1) {
                out.append("<root>\n");
            }
            for (Node child : node.getChildren()) {
                writeNode(out, child);
            }
            if (node.getChildren().size() > 1) {
                out.append("</root>\n");
            }
            return out;
        }
        out.append(String.format("<%s", nodeName));
        for (Map.Entry<String, String> elem : node.getAttributes().entrySet()) {
            out.append(String.format(" %s = \"%s\"", elem.getKey(), elem.getValue() == null ? "" : elem.getValue()));
        }
        if (node.hasChildren()) {
            out.append(">\n");
            for (Node child : node.getChildren()) {
                writeNode(out, child);
            }
            out.append(String.format("</%s>\n", nodeName));
        } else if (node.getValue() == null) {
            out.append("/>\n");
        } else {
            out.append(">");
            out.append(node.getValue());
            out.append(String.format("</%s>\n", nodeName));
        }
        return out;
    }

    private static Node readTags(Tokenizer tokenizer, Node parent) {
        Node node;
        Matcher matcher;
        while (tokenizer.next(TAG_OPEN)) {
            matcher = tokenizer.getMatcher();
            node = parent.addChild(matcher.group(1));
            readAttributes(matcher.group(2), node);

            if (">".equals(matcher.group(3))) {
                if (tokenizer.check(XML_BEGINNING)) {
                    readTags(tokenizer, node);
                }
                if (!tokenizer.next(String.format("(?s)^(.*?)<\\s*\\/%s\\s*>", node.getName()))) {
                    throw new RuntimeException("Enclosing tag expected.");
                }
                if (!node.hasChildren()) {
                    node.setValue(tokenizer.getMatcher().group(1));
                }
            }
        }
        return parent;
    }

    private static void readAttributes(String src, Node node) {
        if (src == null) {
            return;
        }
        Matcher matcher;
        Tokenizer tokenizer = new Tokenizer(src);
        while (tokenizer.next(ATTRIBUTES)) {
            matcher = tokenizer.getMatcher();
            node.setAttribute(matcher.group(1), matcher.group(2));
        }
    }
}
