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
            // <host>127.0.0.1</host>  -> {"host":"127.0.0.1"}
            // <success/> -> {"success": null }
            int iFrom = input.indexOf(">");
            int iTo = input.lastIndexOf("</");
            String tag;
            String content;
            if (iTo == -1) {
                tag = input.substring(input.indexOf("<") + 1, input.lastIndexOf("/>"));
                content = null;
                System.out.printf("{\"%s\": %s}", tag, content);
            } else {
                tag = input.substring(1, iFrom);
                content = input.substring(iFrom + 1, iTo);
                System.out.printf("{\"%s\":\"%s\"}", tag, content);
            }

        }
        if (input.startsWith("{")) {
            //JSON -> XML
            //Extract key between double quotes - XML element.
            //Extract element content from JSON value.
            //If value is not null, enclose JSON with <></>, else enclose within < />.

            //{"success": null } -> <storage/>
            //{"jdk" : "1.8.9"} -> <jdk>1.8.9</jdk>
            // {"pizza":"slice"}
            int t1 = input.indexOf("\"");
            int t2 = input.indexOf("\"", t1 + 1);
            String tag = input.substring(t1 + 1, t2);
            int c1 = input.indexOf("\"", t2 + 1);
            if (c1 == -1) {
                System.out.printf("<%s/>", tag);
            } else {
                int c2 = input.indexOf("\"", c1 + 1);
                String content = input.substring(c1 + 1, c2);
                System.out.printf("<%s>%s</%s>", tag, content, tag);
            }
        }
    }
}
