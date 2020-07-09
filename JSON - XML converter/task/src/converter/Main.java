package converter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
/*        String s = "<B><G>Test</G></B><C>Test1</C>";

        String pattern = "\\<(.+)\\>([^\\<\\>]+)\\<\\/\\1\\>";

        int count = 0;

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        while(m.find())
        {
            System.out.printf("%s -> %s: %s%n", m.group(0), m.group(1), m.group(2));
            count++;
        }

        System.out.println("Count:" + count);*/

        try (Scanner scanner = new Scanner(System.in)) {
            String input = scanner.nextLine();
            convert(input);
        }
    }

    private static void convert(String input) {
        if (input.startsWith("<")) {
            //XML -> JSON
            //Extract an element between tags and enclose it within double quotes.
            //Extract element content and enclose it within double quotes. If element is empty, assign null - JSON
            // value.
            int iFrom = input.indexOf(">");
            int iTo = input.lastIndexOf("<");
            String tag = input.substring(1, iFrom);
            String content = input.substring(iFrom + 1, iTo);
            System.out.printf("{\"%s\":\"%s\"}", tag, content);
        }
        if (input.startsWith("{")) {
            //JSON -> XML
            //Extract key between double quotes - XML element.
            //Extract element content from JSON value.
            //If value is not null, enclose JSON with <></>, else enclose within < />.

            //cut the useless brackets
            input = input.trim().substring(input.indexOf("\"") + 1, input.length() - 1); //{"jdk" : "1.8.9"} -> jdk":"1.8.9

            String tag = input.substring(0, input.indexOf("\""));
            String content = input.substring(input.indexOf(":") + 3);
            System.out.printf("<%s>%s</%s>", tag, content, tag);
        }
    }
}
