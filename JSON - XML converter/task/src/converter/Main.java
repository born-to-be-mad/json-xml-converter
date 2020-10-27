package converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String SOURCE_FILE = "./test.txt";

    public static void main(String[] args) throws IOException {
        String input = readInput();

        if (XmlReader.isXml(input)) {
            System.out.println(XmlReader.read(input));
        } else if (JsonReader.isJson(input)) {
            System.out.println(JsonReader.read(input));
        }

/*        Converter converter = new Converter(input);
        System.out.println(converter.getResult());*/

        //XML xml = new XML(input);
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
    private final List<Node> children = new ArrayList<>();

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
}

class XmlReader {
    private static final Pattern PATTERN_XML_BEGINNING = Pattern.compile("(?s)\\A\\s*<\\s*[a-z_]\\w+");

    private static final Pattern PATTERN_OPENING_TAG = Pattern.compile(
            "(?is)\\s*<\\s*([a-z_]\\w+)\\s*([a-z_]\\w+\\s*=\\s*\".*?\")*\\s*(>|/>)"
    );

    private static final Pattern PATTERN_ATTRIBUTE = Pattern.compile("(?is)([a-z_]\\w+)\\s*=\\s*\"(.*?)\"");

    public static Node read(String src) {
        Node root = new Node();
        readTags(src, root, 0);
        return root;
    }

    private static int readTags(String src, Node parent, int start) {
        Matcher tagMatcher = PATTERN_OPENING_TAG.matcher(src);
        Matcher attrMatcher;
        Matcher enclosingTagMatcher;
        Node element;

        int i = start;
        while (tagMatcher.find(i)) {
            element = parent.addChild(tagMatcher.group(1));

            if (tagMatcher.group(2) != null) {
                attrMatcher = PATTERN_ATTRIBUTE.matcher(tagMatcher.group(2));
                while (attrMatcher.find()) {
                    element.setAttribute(attrMatcher.group(1), attrMatcher.group(2));
                }
            }

            i = tagMatcher.end();
            if (">".equals(tagMatcher.group(3))) {
                enclosingTagMatcher = Pattern
                    .compile(String.format("(?s)(.*?)<\\s*\\/%s\\s*>", element.getName()))
                    .matcher(src);

                if (isXml(src, i)) {
                    i = readTags(src, element, i);
                }

                if (!enclosingTagMatcher.find(i)) {
                    throw new RuntimeException("Enclosing tag expected.");
                }

                if (!element.hasChildren()) {
                    element.setValue(enclosingTagMatcher.group(1));
                }

                i = enclosingTagMatcher.end();
            }
        }

        return i;
    }

    public static boolean isXml(String src) {
        return isXml(src, 0);
    }

    private static boolean isXml(String src, int start) {
        return PATTERN_XML_BEGINNING.matcher(src.substring(start)).find();
    }
}

class JsonReader {
    private static final Pattern PATTERN_JSON_BEGINNING =
        Pattern.compile("(?s)^\\s*\\{\\s*[\"}]");
    private static final Pattern PATTERN_JSON_OBJECT_OPEN =
        Pattern.compile("(?s)^\\s*\\{\\s*");
    private static final Pattern PATTERN_JSON_OBJECT_CLOSE =
        Pattern.compile("(?s)^\\s*}\\s*,?");
    private static final Pattern PATTERN_JSON_OBJECT_ATTR_NAME =
        Pattern.compile("(?s)^\\s*\"(.*?)\"\\s*:\\s*");
    private static final Pattern PATTERN_JSON_OBJECT_ATTR_VALUE =
        Pattern.compile("(?s)^\\s*(\"(.*?)\"|(\\d+\\.?\\d*)|(null)),?");

    private static final Pattern PATTERN_XML_ATTRIBUTE =
        Pattern.compile("(?i)^[#@][a-z_][.\\w]*");
    private static final Pattern PATTERN_XML_IDENTIFIER =
        Pattern.compile("(?i)^[a-z_][.\\w]*");

    public static Node read(String src) {
        Node root = new Node();
        readObject(src, root, 0);
        return root;
    }

    private static int readObject(String src, Node parent, int start) {
        Matcher objectOpenMatcher = PATTERN_JSON_OBJECT_OPEN
            .matcher(src)
            .region(start, src.length())
            .useAnchoringBounds(true);

        if (!objectOpenMatcher.find()) {
            return start;
        }

        int index = objectOpenMatcher.end();

        Matcher attributeMatcher = PATTERN_JSON_OBJECT_ATTR_NAME
            .matcher(src)
            .useAnchoringBounds(true)
            .region(index, src.length());

        Matcher valueMatcher = PATTERN_JSON_OBJECT_ATTR_VALUE
            .matcher(src)
            .useAnchoringBounds(true);

        Matcher objectCloseMatcher = PATTERN_JSON_OBJECT_CLOSE
            .matcher(src)
            .useAnchoringBounds(true);

        Node node;
        while (attributeMatcher.find()) {
            index = attributeMatcher.end();
            node = new Node(attributeMatcher.group(1));
            if (isJson(src, index)) {
                index = readObject(src, node, index);

                if (isXmlAttributes(node)) {
                    Node child;
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

            } else {
                valueMatcher.region(index, src.length());
                if (!valueMatcher.find()) {
                    throw new RuntimeException("Attribute value expected.");
                }

                if (valueMatcher.group(2) != null) { // string
                    node.setValue(valueMatcher.group(2));

                } else if (valueMatcher.group(3) != null) { // number
                    node.setValue(valueMatcher.group(3));

                } else if (valueMatcher.group(4) != null) { // null
                    node.setValue(null);

                } else {
                    throw new RuntimeException("Unknown attribute value.");

                }
                index = valueMatcher.end();
            }
            attributeMatcher.region(index, src.length());

            parent.addChild(node);
        }

        objectCloseMatcher.region(index, src.length());
        if (!objectCloseMatcher.find()) {
            throw new RuntimeException("Object end expected.");
        }

        return objectCloseMatcher.end();
    }

    public static boolean isJson(String src) {
        return isJson(src, 0);
    }

    private static boolean isJson(String src, int start) {
        return PATTERN_JSON_BEGINNING
            .matcher(src)
            .region(start, src.length())
            .useAnchoringBounds(true)
            .find();
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
}

/*
class Converter {

    String json;
    String xml;
    boolean isXmlSource;

    public Converter(String input) {
        String inputNoLineTerminators = input.replaceAll("\\r", "")
                                             .replaceAll("\\n", "");
        this.isXmlSource = isXml(inputNoLineTerminators);
        parse(inputNoLineTerminators);
    }

    public String getResult() {
        return isXmlSource ? json : xml;
    }

    private static boolean isXml(String input) {
        return input.trim().charAt(0) == '<';
    }

    private void parse(String input) {
        if (isXmlSource) {
            xml = input;
            json = parseToJson(input);
        } else {
            json = input;
            xml = parseToXml(input);
        }
    }

    private String parseToXml(String input) {
        return new JsonReader(input).toNode().toXml();
    }

    private String parseToJson(String input) {
        return new XmlReader(input).toNode().toJson();
    }
}

/**
 * Node that can be represented as JSON/XML string.
 */
/*
class Node {
    String element;
    String content;
    Map<String, String> attributes;

    public String toXml() {
        String strOfAttributes = toXmlStr(attributes);
        if (content == null || content.equalsIgnoreCase("null")) {
            return format("<%1$s %2$s />", element, strOfAttributes);
        } else {
            return format("<%1$s %3$s>%2$s</%1$s>", element, content, strOfAttributes);
        }
    }

    private String toXmlStr(Map<String, String> attributes) {
        return attributes.entrySet().stream()
                         .map(entry -> format("%s = \"%s\"", entry.getKey().substring(1), entry.getValue()))
                         .collect(joining(" "));
    }

    public String toJson() {
        String contentValue = content == null ? "null" : ("\"" + content + "\"");
        if (attributes.isEmpty()) {
            return format("{\"%1$s\":%2$s}", element, contentValue);
        } else {
            String strOfAttributes = toJsonStr(attributes);
            return format("{\"%1$s\": {%3$s, \"#%1$s\": %2$s}}", element, contentValue, strOfAttributes);
        }
    }

    private String toJsonStr(Map<String, String> attributes) {
        return attributes.entrySet().stream()
                         .map(entry -> format("\"@%s\" : \"%s\"", entry.getKey(), entry.getValue()))
                         .collect(joining(", "));
    }

    @Override
    public String toString() {
        return "Node{" +
               "element='" + element + '\'' +
               ", content='" + content + '\'' +
               ", attributes=" + attributes +
               '}';
    }
}

interface Convertable {

    String convert(String content);

    void logTo(PrintStream out);

    class Factory {
        public static Convertable of(String input) {
            return input.charAt(0) == '<' ? new XML2JSON() : new JSON2XML();
        }
    }
}

class JSON2XML implements Convertable {
    private Pattern objectPattern = Pattern.compile("\\s*\\{\\s*(.*)\\s*\\}\\s*");
    private Pattern propertyNamePattern = Pattern.compile("\\s*\"([\\w|@|#]*)\"\\s*:\\s*");
    private Pattern propertyValuePattern = Pattern.compile("\\s*:\\s*\"*(.*)[$|\"?\\s*]");
    private Pattern propertiesPattern = Pattern.compile("(?!\\B\\{[^\\}]*),(?![^\\{]*\\}\\B)");
    private StringBuilder builder = new StringBuilder();
    private PrintStream out;

    @Override
    public String convert(String content) {
        var value = readContent(content);
        var properties = propertiesPattern.split(value);
        var keyValuePair = readProperty(properties[0]);
        writeRecursively(keyValuePair[0], keyValuePair[1]);
        return builder.toString();
    }

    @Override
    public void logTo(PrintStream out) {
        this.out = out;
    }

    private void println(String fmt, String... params) {
        if (out != null) {
            out.printf(fmt + "\n", params);
        }
    }

    private String readContent(String content) {
        var objectMatcher = objectPattern.matcher(content.replaceAll("\\s", ""));
        objectMatcher.find();
        return objectMatcher.group(1);
    }

    private String[] readProperty(String content) {
        content = !content.strip().endsWith("\"") ? content.strip() + "\n" : content.strip();
        var keyMatcher = propertyNamePattern.matcher(content);
        keyMatcher.find();
        var key = keyMatcher.group(1);
        var valueMatcher = propertyValuePattern.matcher(content);
        valueMatcher.find();
        var value = valueMatcher.group(1).strip();
        value = "null".equals(value) ? null : value;
        return new String[] {key, value};
    }

    private void writeRecursively(String name, String value, String... attributes) {
        var elementType = ElementType.of(value);
        switch (elementType) {
        case LITERAL:
        case STRING:
            writeLiteral(name, value, attributes);
            break;
        case OBJECT:
            writeElement(name, value);
        }
    }

    private void writeElement(String name, String value) {
        var properties = propertiesPattern.split(readContent(value));
        var content = Arrays.stream(properties)
                            .filter(p -> p.startsWith("\"#"))
                            .findAny();
        var attributes = Arrays.stream(properties)
                               .filter(p -> p.startsWith("\"@"))
                               .toArray(String[]::new);
        var elements = Arrays.stream(properties)
                             .filter(p -> !p.startsWith("\"@") && !p.startsWith("\"#"))
                             .toArray(String[]::new);
        if (content.isEmpty() && elements.length == 0) {
            writeSimpleElement(name, attributes);
        } else {
            writeBeginElement(name, attributes);
            content.ifPresent(s -> writeValue(readProperty(s)[1]));
            for (var element : elements) {
                var keyValuePair = readProperty(element);
                writeRecursively(keyValuePair[0], keyValuePair[1]);
            }
            writeEndElement(name);
        }
    }

    private void writeLiteral(String name, String value, String... attributes) {
        if (value == null || value.length() == 0) {
            writeSimpleElement(name, attributes);
        } else {
            writeBeginElement(name, attributes);
            writeValue(value);
            writeEndElement(name);
        }
    }

    private void writeBeginElement(String elementName, String... attributes) {
        builder.append("<");
        builder.append(elementName.startsWith("#") ? elementName.substring(1) : elementName);
        writeAttributes(attributes);
        builder.append(">");
    }

    private void writeEndElement(String elementName) {
        builder.append("</");
        builder.append(elementName.startsWith("#") ? elementName.substring(1) : elementName);
        builder.append(">");
    }

    private void writeSimpleElement(String elementName, String... attributes) {
        builder.append("<");
        builder.append(elementName.startsWith("#") ? elementName.substring(1) : elementName);
        writeAttributes(attributes);
        builder.append("/>");
    }

    private void writeAttributes(String[] attributes) {
        if (attributes.length > 0) {
            builder.append(" ");
            for (var i = 0; i < attributes.length; i++) {
                String attribute = attributes[i];
                writeAttribute(attribute);
                if (i < attributes.length - 1) {
                    builder.append(" ");
                }
            }
        }
    }

    private void writeAttribute(String attribute) {
        var keyValuePair = attribute.replace("\"", "").split(":");
        builder.append(keyValuePair[0].strip().substring(1));
        builder.append(" = ");
        builder.append("\"");
        builder.append(keyValuePair[1].strip());
        builder.append("\"");
    }

    private void writeValue(String value) {
        builder.append(value == null ? "" : value);
    }

    private enum ElementType {
        OBJECT,
        ARRAY,
        STRING,
        LITERAL;

        public static ElementType of(String elementValue) {
            if (elementValue.charAt(0) == '{') {
                return ElementType.OBJECT;
            }
            if (elementValue.charAt(0) == '[') {
                return ElementType.ARRAY;
            }
            if (elementValue.charAt(0) == '"') {
                return ElementType.STRING;
            }
            return ElementType.LITERAL;
        }
    }
}

class XML2JSON implements Convertable {
    private Pattern simpleElementPattern = Pattern.compile("\\s*\\<(.*?)\\/\\>\\s*");
    private Pattern elementNameAndAttributesPattern = Pattern.compile("\\<?\\/?(\\w*)(.*)($|\\>)");
    private Pattern attributesPartsPattern = Pattern.compile("\\s*(\\w*)\\s*=\\s*\\\"(\\w*)\\\"\\s*");
    private Pattern elementStartingPattern = Pattern.compile("\\s*\\<\\/?(.*?)\\/?\\>\\s*");
    private Pattern elementContentPattern = Pattern.compile("\\>(.*)\\<");
    private Pattern elementsPartsPattern = Pattern.compile("(\\<.*?\\>)|(.+?(?=\\<|$))");
    private Pattern elementClosingPattern = Pattern.compile("\\<\\/(.*?)\\>|\\<(.*?)\\/\\>");
    private StringBuilder builder = new StringBuilder();
    private PrintStream out;

    @Override
    public String convert(String content) {
        writeBeginObject();
        var elements = readElements(content
                                        .replace("\r", "")
                                        .replace("\n", ""));
        var keyValuePair = readElement(elements.get(0));
        writeRecursively(null,
                         keyValuePair[0],
                         keyValuePair[1],
                         keyValuePair[2]);
        writeEndObject();
        return builder.toString();
    }

    @Override
    public void logTo(PrintStream out) {
        this.out = out;
    }

    private void println(String fmt, String... params) {
        if (out != null) {
            out.printf(fmt + "\n", params);
        }
    }

    private List<String> readElements(String elements) {
        var result = new ArrayList<String>();
        var partsMatcher = elementsPartsPattern.matcher(elements);
        partsMatcher.find();
        var parts = Stream.concat(
            Stream.of(partsMatcher.group().strip()),
            partsMatcher.results().map(r -> r.group().strip()))
                          .filter(r -> r.length() > 0)
                          .collect(toList());
        var currentElement = new StringBuilder();
        var currentElementName = "";
        for (var part : parts) {
            // If starting element
            var isOpeningTag = currentElement.length() == 0;
            if (isOpeningTag) {
                var elementNameAndAttributesMatcher = elementNameAndAttributesPattern.matcher(part);
                if (elementNameAndAttributesMatcher.find()) {
                    currentElementName = elementNameAndAttributesMatcher.group(1);
                }
            }
            // If literal
            var isLiteral = !part.contains("<");
            if (isLiteral) {
                var isInElement = currentElement.length() > 0;
                if (isInElement) {
                    currentElement.append(part);
                } else {
                    result.add(part);
                    currentElement.setLength(0);
                }
                continue;
            }
            // At this point, the part will compose the current element no matter what
            currentElement.append(part);
            // If closing element
            var closingMatcher = elementClosingPattern.matcher(part);
            var isClosingTag = closingMatcher.find();
            if (isClosingTag) {
                var elementNameAndAttributesMatcher = elementNameAndAttributesPattern.matcher(part);
                if (elementNameAndAttributesMatcher.find()) {
                    var closingElementName = elementNameAndAttributesMatcher.group(1);
                    if (currentElementName.equals(closingElementName)) {
                        result.add(currentElement.toString());
                        currentElement.setLength(0);
                    }
                }
            }
        }
        return result;
    }

    private String[] readElement(String element) {
        var elementMatcher = elementContentPattern.matcher(element);
        if (!elementMatcher.find()) {
            var tagMatcher = simpleElementPattern.matcher(element);
            tagMatcher.find();
            var tag = tagMatcher.group(1);
            var nameAndAttributes = extractNameAndAttributes(tag);
            return new String[] {nameAndAttributes[0], null, nameAndAttributes[1].strip()};
        } else {
            var tagMatcher = elementStartingPattern.matcher(element);
            tagMatcher.find();
            var tag = tagMatcher.group(1);
            var nameAndAttributes = extractNameAndAttributes(tag);
            var content = extractContent(element);
            return new String[] {nameAndAttributes[0], content, nameAndAttributes[1].strip()};
        }
    }

    private String extractContent(String element) {
        var contentMatcher = elementContentPattern.matcher(element);
        contentMatcher.find();
        var content = contentMatcher.group(1);
        content = content == null ? "null" : content;
        return content;
    }

    private String[] extractNameAndAttributes(String tag) {
        var nameAndAttributes = new String[2];
        var nameAndAttributesMatcher = elementNameAndAttributesPattern.matcher(tag);
        nameAndAttributesMatcher.find();
        nameAndAttributes[0] = nameAndAttributesMatcher.group(1);
        nameAndAttributes[1] = nameAndAttributesMatcher.groupCount() == 3
                               ? nameAndAttributesMatcher.group(2)
                               : null;
        return nameAndAttributes;
    }

    private void writeRecursively(String parentPath, String name, String content, String attributes) {
        var elementType = ValueType.of(content);
        if (attributes != null && attributes.length() > 0) {
            elementType = ValueType.OBJECT;
        }
        switch (elementType) {
        case LITERAL:
        case STRING:
            writeString(parentPath, name, content);
            break;
        case OBJECT:
            writeObject(parentPath, name, content, attributes);
        }
    }

    private void writeObject(String parentPath, String name, String value, String attributes) {
        var path = computePath(parentPath, name);
        logElement(path, name, value, attributes);

        builder.append("\"");
        builder.append(name);
        builder.append("\"");
        builder.append(":");
        var valueType = ValueType.of(value);
        var children = valueType == ValueType.LITERAL || valueType == ValueType.STRING
                       ? List.<String[]>of()
                       : readElements(value.strip())
                           .stream()
                           .map(this::readElement)
                           .collect(toList());
        if (children.size() > 0 || (attributes != null && attributes.length() > 0)) {
            valueType = ValueType.OBJECT;
        }
        if (children.size() > 1 && (attributes == null || attributes.length() == 0)) {
            valueType = ValueType.ARRAY;
        }
        if (attributes != null && attributes.length() > 0) {
            var element = new ArrayList<String[]>();
            element.add(new String[] {"#" + name, value, null});
            var attrs = readAttributes(attributes);
            children = Stream
                .of(element.stream(), attrs.stream())
                .reduce(Stream::concat)
                .get()
                .collect(toList());
        }
        switch (valueType) {
        case OBJECT:
            writeBeginObject();
            break;
        case ARRAY:
            writeBeginArray();
            break;
        }
        for (var i = 0; i < children.size(); i++) {
            if (valueType == ValueType.ARRAY) {
                writeBeginObject();
            }
            var keyValuePair = children.get(i);
            writeRecursively(path,
                             keyValuePair[0],
                             keyValuePair[1],
                             keyValuePair[2]);
            if (valueType == ValueType.ARRAY) {
                writeEndObject();
            }
            if (i < children.size() - 1) {
                builder.append(",");
            }
        }
        switch (valueType) {
        case OBJECT:
            writeEndObject();
            break;
        case ARRAY:
            writeEndArray();
            break;
        }
    }

    private void logElement(String path, String name, String value, String attributes) {
        if (!name.startsWith("#") && !name.startsWith("@")) {
            println("Element:");
            println("path = %s", path);
            var valueType = ValueType.of(value);
            switch (valueType) {
            case STRING:
                println("value = \"%s\"", value);
                break;
            case LITERAL:
                println("value = %s", value);
                break;
            }
            if (attributes != null && attributes.length() > 0) {
                println("attributes:");
                var attrs = readAttributes(attributes);
                for (var attr : attrs) {
                    println("%s = \"%s\"", attr[0].substring(1), attr[1]);
                }
            }
            println("");
        }
    }

    private String computePath(String parent, String name) {
        return name.startsWith("#")
               ? parent
               : parent != null
                 ? parent + ", " + name
                 : name;
    }

    private List<String[]> readAttributes(String attributes) {
        var result = new ArrayList<String[]>();
        var matcher = attributesPartsPattern.matcher(attributes);
        while (matcher.find()) {
            result.add(new String[] {"@" + matcher.group(1), matcher.group(2), null});
        }
        return result;
    }

    private void writeString(String parentPath, String name, String value) {
        var path = computePath(parentPath, name);
        logElement(path, name, value, null);

        builder.append("\"");
        builder.append(name);
        builder.append("\"");
        builder.append(":");
        if (value == null) {
            builder.append("null");
        } else {
            builder.append("\"");
            builder.append(value);
            builder.append("\"");
        }
    }

    private void writeBeginObject() {
        builder.append("{");
    }

    private void writeEndObject() {
        builder.append("}");
    }

    private void writeBeginArray() {
        builder.append("[");
    }

    private void writeEndArray() {
        builder.append("]");
    }

    private enum ValueType {
        OBJECT,
        ARRAY,
        STRING,
        LITERAL;

        public static ValueType of(String valueType) {
            if (valueType == null || valueType.equals("null")) {
                return ValueType.LITERAL;
            }
            if (valueType.length() == 0 || valueType.charAt(0) == '"') {
                return ValueType.STRING;
            }
            if (valueType.charAt(0) == '<') {
                return ValueType.OBJECT;
            }
            return ValueType.STRING;
        }
    }
}

class XML {
    private String JSONString;
    private String XMLString;

    public XML(String XMLString) {
        this.XMLString = cleanXMLString(XMLString);
        parse(this.XMLString, null);
    }

    private String cleanXMLString(String XMLString) {
        String cleanXML;
        //matches the space between two tags
        cleanXML = XMLString.replaceAll("(?<=>)\\s+?(?=<)", "");
        //matches the space(s) before />
        cleanXML = cleanXML.replaceAll("\\s+?(?=/>)", "");
        return cleanXML;
    }

    private void parse(String xmlDocument, Deque<String> parents) {
        //matches <tag>text</tag> or <tag/> or <tag></tag>
        //matches all the characters between <> or <  />
        Pattern wholeTagPattern = Pattern.compile("<(\\w+).*?(\\/>|>.*?<\\/\\1>)");
        Matcher wholeTagMatcher = wholeTagPattern.matcher(xmlDocument);

        if (wholeTagMatcher.find()) {
            int endPosition;
            Deque<String> parentsStack;
            do {
                parentsStack = parents == null ? new ArrayDeque<>() : parents;

                String wholeTag = wholeTagMatcher.group();
                endPosition =
                    wholeTagMatcher.end(); //the end position of the match. To see if it checked the whole string

                //matches the opening tag (and the key in group 1)
                Pattern tagPattern = Pattern.compile("(?<=<)(\\w+).*?\\/?(?=>)");
                Matcher tagMatcher = tagPattern.matcher(wholeTag);

                boolean isFound = tagMatcher.find();
                String key = tagMatcher.group(1);
                String tag = tagMatcher.group();

                XMLTag xmlTag = new XMLTag(tag, key, wholeTag);
                xmlTag.setParents(parentsStack);
                System.out.println(xmlTag.toString());

                if (xmlTag.HasChild()) { //if there are childs inside this tag, recursively parse them.
                    //Matches the text inside <tag>text</tag>
                    Pattern textInsideMatchedTagPattern = Pattern.compile("(?<=<" + tag + ">).*?(?=<\\/" + key + ")");
                    Matcher textInsideMatchedTagMatcher = textInsideMatchedTagPattern.matcher(wholeTag);
                    textInsideMatchedTagMatcher.find();
                    parentsStack.offer(key);

                    parse(textInsideMatchedTagMatcher.group(), parentsStack);
                    parentsStack.pollLast();
                }
                wholeTagMatcher.find();
            } while (endPosition < xmlDocument.length());
        }
    }
}

class XMLTag {
    private String key; //this is just the key (the one that appears in the </key> tag
    private String value;
    private boolean hasChild, hasAttributes;
    private String XMLString;
    private String tag; //this is the complete tag, for example <key attr1 = "value1" attr2 = "value2> without the <>
    private LinkedHashMap<String, String> attributesMap;
    private Deque<String> parents;

    public XMLTag(String tag, String key, String XMLString) {
        this.tag = tag;
        this.hasAttributes = hasAttributes();
        this.key = key;
        this.XMLString = XMLString;
        parseTag();
    }

    public boolean hasAttributes() {
        //matches the pattern key attribute1 = "value1" .. attributeN = "valueN" or "value1" .. attributeN = "valueN" /
        Pattern pattern = Pattern.compile("\\w*?=\\s*?\".*?\"\\s*?\\/?");
        Matcher matcher = pattern.matcher(this.tag);

        return matcher.find();
    }

    private void parseTag() {
        this.hasChild = hasChild();

        if (this.hasChild) {
            this.value = null;
        } else {
            //check if tag is self closing a.k.a <tag/>
            if (this.XMLString.matches("<.*?\\/>")) {
                this.value = null;
            } else {
                //matches the text in <tag>text</tag>
                Pattern tagPattern = Pattern.compile("(?<=<" + this.tag + ">).*?(?=<\\/" + this.key + ">)");
                Matcher tagMatcher = tagPattern.matcher(this.XMLString);

                if (tagMatcher.find()) {
                    this.value = tagMatcher.group();
                } else {
                    this.value = "";
                }
            }
        }

        if (this.hasAttributes()) {
            Pattern attributePattern = Pattern.compile("\\w*\\s*?=\\s*?\"\\w*?\""); //matches attribute = "value"
            Matcher attributeMatcher = attributePattern.matcher(this.tag);

            this.attributesMap = new LinkedHashMap<>();

            while (attributeMatcher.find()) {
                Pattern attributeKeyPattern = Pattern.compile("\\w*(?=\\s*?\\=\\s*?)"); //matches a key followed by =
                Matcher attributeKeyMatcher = attributeKeyPattern.matcher(attributeMatcher.group());

                attributeKeyMatcher.find();
                String attributeKey = attributeKeyMatcher.group();

                Pattern attributeValuePattern =
                    Pattern.compile("(?<=\\\")\\w*(?=\\\")"); //matches a word enclosed in ""
                Matcher attributeValueMatcher = attributeValuePattern.matcher(attributeMatcher.group());

                attributeValueMatcher.find();
                String attributeValue = attributeValueMatcher.group();

                attributesMap.put(attributeKey, attributeValue);
            }
        }
    }

    private boolean hasChild() {
        //matches the text in <tag>text</tag> or <tag/> or <tag></tag> whichever comes first.
        Pattern tagPattern = Pattern.compile("((?<=<" + this.tag + ">).+?(?=<\\/" + this.key + ">)|(?<=<)" + this.tag +
                                             "(?=\\s*?\\/\\s*?>)|(?<=<)" + this.tag + "(?=><\\/" + this.key + ">))");
        Matcher tagMatcher = tagPattern.matcher(this.XMLString);

        if (tagMatcher.find()) {
            Pattern pattern = Pattern.compile("<.*?>"); //matches a tag
            Matcher matcher = pattern.matcher(tagMatcher.group());

            if (matcher.find()) {
                return true; //if it finds a tag inside the provided tag, returns true. Else returns false.
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder elementString = new StringBuilder();

        elementString.append("Element:\n");
        elementString.append("path = ");

        for (String parent : this.parents) {
            elementString.append(parent);
            elementString.append(", ");
        }
        elementString.append(this.key + "\n");

        if (!this.hasChild) {
            elementString.append("value = ");
            if (this.value != null) {
                elementString.append("\"" + this.value + "\"\n");
            } else {
                elementString.append("null\n");
            }
        }

        if (this.hasAttributes) {
            elementString.append("attributes:\n");

            for (String key : attributesMap.keySet()) {
                elementString.append(key + " = \"" + this.attributesMap.get(key) + "\"\n");
            }
        }

        return elementString.toString();
    }

    public boolean HasChild() {
        return hasChild;
    }

    public void setParents(Deque<String> parents) {
        this.parents = parents;
    }
}

abstract class Reader {
    static final String ELEMENT_NAME = "elementName";
    static final String ATTRIBUTES = "attributes";
    static final String CONTENT = "content";
    static final String ATTRIBUTE_NAME = "attributeName";
    static final String ATTRIBUTE_VALUE = "attributeValue";

    protected final String input;

    protected Reader(String input) {
        this.input = input;
    }

    public Node toNode() {
        Node node = new Node();
        node.element = getNodeElementName().trim();
        node.content = getNodeContent() == null ? null : getNodeContent().trim();
        node.attributes = extractAttributes(getNodeAttributes());
        return node;
    }

    protected abstract String getNodeElementName();

    protected abstract String getNodeContent();

    protected abstract String getNodeAttributes();

    protected abstract Pattern getAttributePairsPattern();

    private Map<String, String> extractAttributes(String strOfAttributes) {
        if (strOfAttributes == null || strOfAttributes.isBlank()) {
            return Collections.emptyMap();
        }
        return parseAttributes(strOfAttributes);
    }

    private Map<String, String> parseAttributes(String strOfAttributes) {
        Map<String, String> attributes = new HashMap<>();
        Matcher matcher = getAttributePairsPattern().matcher(strOfAttributes);
        while (matcher.find()) {
            attributes.put(matcher.group(ATTRIBUTE_NAME).trim(), matcher.group(ATTRIBUTE_VALUE).trim());
        }
        return attributes;
    }
}

class XmlReader extends Reader {
    private final Pattern full = Pattern.compile(
        format("<(?<%s>\\w+)(?<%s>.*)>(?<%s>.*)<.*>", ELEMENT_NAME, ATTRIBUTES, CONTENT)
    );

    private final Pattern empty = Pattern.compile(
        format("<(?<%s>\\w+)(?<%s>.*)/>", ELEMENT_NAME, ATTRIBUTES)
    );

    private final Pattern attributes = Pattern.compile(
        format("\\s*(?<%s>\\w+)\\s*=\\s*\"(?<%s>\\w+)\"\\s*", ATTRIBUTE_NAME, ATTRIBUTE_VALUE)
    );

    private final Pattern neverMatchingPattern = Pattern.compile("^@$");

    private final Matcher nameMatcher;
    private final Matcher contentMatcher;
    private final Matcher attributeMatcher;

    public XmlReader(String xmlInput) {
        super(xmlInput);
        boolean isFullNode = isFullNode(xmlInput);
        this.nameMatcher = determineElementNameMatcher(isFullNode);
        this.contentMatcher = determineContentMatcher(isFullNode);
        this.attributeMatcher = determineAttributeMatcher(isFullNode);
    }

    private boolean isFullNode(String xml) {
        return full.matcher(xml).matches();
    }

    private Matcher determineElementNameMatcher(boolean isFullNode) {
        return isFullNode ? full.matcher(input) : empty.matcher(input);
    }

    private Matcher determineContentMatcher(boolean isFullNode) {
        return isFullNode ? full.matcher(input) : neverMatchingPattern.matcher(input);
    }

    private Matcher determineAttributeMatcher(boolean isFullNode) {
        return isFullNode ? full.matcher(input) : empty.matcher(input);
    }

    @Override
    protected String getNodeElementName() {
        return nameMatcher.matches() ? nameMatcher.group(ELEMENT_NAME) : null;
    }

    @Override
    protected String getNodeContent() {
        return contentMatcher.matches() ? contentMatcher.group(CONTENT) : null;
    }

    @Override
    protected String getNodeAttributes() {
        return attributeMatcher.matches() ? attributeMatcher.group(ATTRIBUTES) : null;
    }

    @Override
    protected Pattern getAttributePairsPattern() {
        return attributes;
    }
}

class JsonReader extends Reader {
    Pattern withAttributesNameAndContent = Pattern.compile(
        format(
            "\\{\\s*\"(?<%s>\\w+)\"\\s*:\\s*\\{[\\s\\p{ASCII}]*(\"#.*?\"\\s+:\\s+\"?(?<%s>(null|[^\"]*))\"?)"
            + "[\\s\\p{ASCII}]*}\\s*}\\s*",
            ELEMENT_NAME,
            CONTENT));
    Pattern withAttributesAttributes = Pattern.compile(
        format("[\\s\\p{ASCII}]*\\{[\\s\\p{ASCII}]*\\{(?<%s>[\\s\\p{ASCII}]*)}[\\s\\p{ASCII}]*}[\\s\\p{ASCII}]*",
               ATTRIBUTES));
    Pattern attributes = Pattern.compile(
        format("\\s*\"(?<%s>[^#\"]*)\"\\s*:\\s\"?(?<%s>null|\\d+|[^\"]*)\"?\\s*",
               ATTRIBUTE_NAME, ATTRIBUTE_VALUE));
    Pattern noAttributes = Pattern.compile(
        format("\\{\\s*\"(?<%s>\\w+)\"\\s*:\\s*\"?(?<%s>(null|[^\"]*))\"?\\s*}",
               ELEMENT_NAME, CONTENT));
    private final Pattern neverMatchingPattern = Pattern.compile("^@$");

    private final Matcher nameMatcher;
    private final Matcher contentMatcher;
    private final Matcher attributeMatcher;

    public JsonReader(String jsonInput) {
        super(jsonInput);
        boolean haveAttributes = isWithAttributes(jsonInput);
        this.nameMatcher = determineNameMatcher(haveAttributes);
        this.contentMatcher = determineContentMatcher(haveAttributes);
        this.attributeMatcher = determineAttributesMatcher(haveAttributes);
    }

    private boolean isWithAttributes(String jsonInput) {
        return !noAttributes.matcher(jsonInput).matches();
    }

    private Matcher determineNameMatcher(boolean haveAttributes) {
        return haveAttributes ? withAttributesNameAndContent.matcher(input) : noAttributes.matcher(input);
    }

    private Matcher determineContentMatcher(boolean haveAttributes) {
        return haveAttributes ? withAttributesNameAndContent.matcher(input) : noAttributes.matcher(input);
    }

    private Matcher determineAttributesMatcher(boolean haveAttributes) {
        return haveAttributes ? withAttributesAttributes.matcher(input) : neverMatchingPattern.matcher(input);
    }

    @Override
    protected String getNodeElementName() {
        return nameMatcher.matches() ? nameMatcher.group(ELEMENT_NAME) : null;
    }

    @Override
    protected String getNodeContent() {
        return contentMatcher.matches() ? contentMatcher.group(CONTENT) : null;
    }

    @Override
    protected String getNodeAttributes() {
        return attributeMatcher.matches() ? attributeMatcher.group(ATTRIBUTES) : null;
    }

    @Override
    protected Pattern getAttributePairsPattern() {
        return attributes;
    }
}*/
