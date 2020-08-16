import java.util.Scanner;

public class Main {

    //// The (?: notation must be used for non-capturing parenthesis. Each \ must be escaped in a Java String.
    private static final String NO_COMMENTS_GREEDY = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)";
    //private static final String NO_COMMENTS_NON_GREEDY = "/\\*(?:.|[\\n\\r])*?\\*/";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String codeWithComments = scanner.nextLine();
            //Prints out the contents of the string sourcecode with the comments removed.
            //Greedy Matching
            System.out.println(codeWithComments.replaceAll(NO_COMMENTS_GREEDY, ""));
            //Non-greedy Matching
            //System.out.println(codeWithComments.replaceAll(NO_COMMENTS_NON_GREEDY, ""));
        }

    }
}
