package converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

public class Main {

    private static final String SOURCE_FILE = "./test.txt";

    public static void main(String[] args) throws IOException {
        String input = readInput();
        Converter converter = new Converter(input);
        String output = converter.isXmlSource ? converter.json : converter.xml;
        System.out.println(output);
    }

    private static String readInput() throws IOException {
        return Files.readString(Paths.get(SOURCE_FILE));
    }
}

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
class Node {
    String element;
    String content;
    Map<String, String> attributes;

    public String toXml() {
        String strOfAttributes = toXmlStr(attributes);
        if (content == null || content.equalsIgnoreCase("null")) {
            return String.format("<%1$s %2$s />", element, strOfAttributes);
        } else {
            return String.format("<%1$s %3$s>%2$s</%1$s>", element, content, strOfAttributes);
        }
    }

    private String toXmlStr(Map<String, String> attributes) {
        return attributes.entrySet().stream()
                         .map(entry -> String.format("%s = \"%s\"", entry.getKey().substring(1), entry.getValue()))
                         .collect(joining(" "));
    }

    public String toJson() {
        String contentValue = content == null ? "null" : ("\"" + content + "\"");
        if (attributes.isEmpty()) {
            return String.format("{\"%1$s\":%2$s}", element, contentValue);
        } else {
            String strOfAttributes = toJsonStr(attributes);
            return String.format("{\"%1$s\": {%3$s, \"#%1$s\": %2$s}}", element, contentValue, strOfAttributes);
        }
    }

    private String toJsonStr(Map<String, String> attributes) {
        return attributes.entrySet().stream()
                         .map(entry -> String.format("\"@%s\" : \"%s\"", entry.getKey(), entry.getValue()))
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
        String.format("<(?<%s>\\w+)(?<%s>.*)>(?<%s>.*)<.*>", ELEMENT_NAME, ATTRIBUTES, CONTENT)
    );

    private final Pattern empty = Pattern.compile(
        String.format("<(?<%s>\\w+)(?<%s>.*)/>", ELEMENT_NAME, ATTRIBUTES)
    );

    private final Pattern attributes = Pattern.compile(
        String.format("\\s*(?<%s>\\w+)\\s*=\\s*\"(?<%s>\\w+)\"\\s*", ATTRIBUTE_NAME, ATTRIBUTE_VALUE)
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
    Pattern withAttributesNameAndContent = Pattern.compile(String.format(
        "\\{\\s*\"(?<%s>\\w+)\"\\s*:\\s*\\{[\\s\\p{ASCII}]*(\"#.*?\"\\s+:\\s+\"?(?<%s>(null|[^\"]*))\"?)[\\s\\p{ASCII}]*}\\s*}\\s*",
        ELEMENT_NAME, CONTENT));
    Pattern withAttributesAttributes = Pattern.compile(String.format(
        "[\\s\\p{ASCII}]*\\{[\\s\\p{ASCII}]*\\{(?<%s>[\\s\\p{ASCII}]*)}[\\s\\p{ASCII}]*}[\\s\\p{ASCII}]*",
        ATTRIBUTES));
    Pattern attributes = Pattern.compile(String.format(
        "\\s*\"(?<%s>[^#\"]*)\"\\s*:\\s\"?(?<%s>null|\\d+|[^\"]*)\"?\\s*",
        ATTRIBUTE_NAME, ATTRIBUTE_VALUE));
    Pattern noAttributes = Pattern.compile(String.format(
        "\\{\\s*\"(?<%s>\\w+)\"\\s*:\\s*\"?(?<%s>(null|[^\"]*))\"?\\s*}",
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
}
