import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class test {

    private static final String lessonSeparator = " ";

    public String[] splitLesson(String lesson) {

        //String[] temp = lesson.replace(" \n", "").replace("  \n", "").replace("\n ", "").replace("\n", "").replace(lessonSeparator, "\t").split("\t");
        //String[] temp = lesson.replaceAll("[\\t\\n\\r]+", " ").split(lessonSeparator + "+");
        //return temp;

        if (lesson == null || lesson.trim().isEmpty()) {
            return new String[]{"", "", ""};
        }

        // Normalize tabs/newlines to lessonSeparator and trim
        String normalized = lesson.replaceAll("[\\t\\n\\r]+", lessonSeparator).trim();

        // Split using lessonSeparator (escaped for regex)
        String[] tokens = normalized.split(Pattern.quote(lessonSeparator) + "+");
        int n = tokens.length;
        String code = "";
        String subject = "";
        String room = "";
        int start = 0;

        // Improved: Detect code (first token that contains at least one digit)
        if (n > 0) {
            // Handle exceptional case for "當值" or "Duty"
            if (tokens[0].equals("當值") || tokens[0].equalsIgnoreCase("Duty")) {
                code = tokens[0];
                if (n > 1) {
                    subject = tokens[1];
                }
                // No subject (everything is already parsed)
                return new String[] { code, subject, "" };
            }
            // Handle two-token code, e.g., (雙) 3B
            else if (n > 1 && tokens[0].matches("^\\(.*\\)$") && tokens[1].matches("^\\d+[A-Z]$")) {
                code = tokens[0] + " " + tokens[1];
                start = 1;
            }
            // Standard digit-in-code logic, e.g., "3B", "1A"
            else if (tokens[0].matches(".*\\d+.*")) {
                code = tokens[0];
                start = 1;
            } else {
                start = 0;
            }
        }


        // Build subject tokens (excluding code if present)
        start = code.isEmpty() ? 0 : 1;
        List<String> subjectTokens = new ArrayList<>(Arrays.asList(tokens).subList(start, n));

        // Detect room at the end of subjectTokens
        if (!subjectTokens.isEmpty()) {
            String lastToken = subjectTokens.get(subjectTokens.size() - 1);
            // Room patterns: RM207, Room207, Room 207, etc.
            if (lastToken.matches("\\d+( ?#\\w+)?") || lastToken.matches("(?i)RM\\d+|Room")) {
                room = lastToken;
                subjectTokens.remove(subjectTokens.size() - 1);
            } else if (subjectTokens.size() >= 2 &&
                    subjectTokens.get(subjectTokens.size() - 2).equalsIgnoreCase("Room") &&
                    subjectTokens.get(subjectTokens.size() - 1).matches("\\d+")) {
                room = subjectTokens.get(subjectTokens.size() - 2) + " " + subjectTokens.get(subjectTokens.size() - 1);
                subjectTokens.remove(subjectTokens.size() - 1);
                subjectTokens.remove(subjectTokens.size() - 1);
            }
        }

        // Build subject string
        subject = String.join(" ", subjectTokens).trim();

        return new String[]{code, subject, room};
    }

    public static void main(String[] args) {
        test splitter = new test();
        String lesson1 = "CS101 Introduction to Java RM101";
        String[] result1 = splitter.splitLesson(lesson1);
        System.out.println("Lesson: " + lesson1);
        System.out.println("Code: " + result1[0]);
        System.out.println("Subject: " + result1[1]);
        System.out.println("Room: " + result1[2]);
        System.out.println(java.util.Arrays.toString(result1));

        System.out.println("---");

        String lesson2 = "PHY202 Advanced Physics Lab Room 203";
        String[] result2 = splitter.splitLesson(lesson2);
        System.out.println("Lesson: " + lesson2);
        System.out.println("Code: " + result2[0]);
        System.out.println("Subject: " + result2[1]);
        System.out.println("Room: " + result2[2]);
        System.out.println(java.util.Arrays.toString(result2));

        String lesson3 = "當值 6/F";
        String[] result3 = splitter.splitLesson(lesson3);
        System.out.println("Lesson: " + lesson3);
        System.out.println("Code: " + result3[0]);
        System.out.println("Subject: " + result3[1]);
        System.out.println("Room: " + result3[2]);
        System.out.println(java.util.Arrays.toString(result3));

        
        String lesson4 = "(雙)3B 中文(麗支援) 402#Special";
        String[] result4 = splitter.splitLesson(lesson4);
        System.out.println("Lesson: " + lesson4);
        System.out.println("Code: " + result4[0]);
        System.out.println("Subject: " + result4[1]);
        System.out.println("Room: " + result4[2]);
        System.out.println(java.util.Arrays.toString(result4));
    }
}