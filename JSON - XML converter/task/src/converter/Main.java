package converter;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            System.out.println(convert(input));
        }
    }

    private static String convert(String input) {
        if (input.startsWith("<")) {
            //XML -> JSON
            //Extract an element between tags and enclose it within double quotes.
            //Extract element content and enclose it within double quotes. If element is empty, assign null - JSON value.
        }
        if (input.startsWith("{")) {
            //JSON -> XML
            //Extract key between double quotes - XML element.
            //Extract element content from JSON value.
            //If value is not null, enclose JSON with <></>, else enclose within < />.
        }

        return input;
    }
}
