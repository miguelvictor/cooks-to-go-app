package team.jcandfriends.cookstogo.inflector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TwoFormInflector {
    private final List<TwoFormInflector.Rule> rules = new ArrayList<>();

    protected String getPlural(String word) {
        for (TwoFormInflector.Rule rule : this.rules) {
            String result = rule.getPlural(word);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected void uncountable(String[] list) {
        this.rules.add(new TwoFormInflector.CategoryRule(list, "", ""));
    }

    protected void irregular(String singular, String plural) {
        if (singular.charAt(0) == plural.charAt(0)) {
            this.rules.add(new TwoFormInflector.RegExpRule(Pattern.compile("(?i)(" + singular.charAt(0) + ")" + singular.substring(1)
                    + "$"), "$1" + plural.substring(1)));
        } else {
            this.rules.add(new TwoFormInflector.RegExpRule(Pattern.compile(Character.toUpperCase(singular.charAt(0)) + "(?i)"
                    + singular.substring(1) + "$"), Character.toUpperCase(plural.charAt(0))
                    + plural.substring(1)));
            this.rules.add(new TwoFormInflector.RegExpRule(Pattern.compile(Character.toLowerCase(singular.charAt(0)) + "(?i)"
                    + singular.substring(1) + "$"), Character.toLowerCase(plural.charAt(0))
                    + plural.substring(1)));
        }
    }

    protected void irregular(String[][] list) {
        for (String[] pair : list) {
            this.irregular(pair[0], pair[1]);
        }
    }

    protected void rule(String singular, String plural) {
        this.rules.add(new TwoFormInflector.RegExpRule(Pattern.compile(singular, Pattern.CASE_INSENSITIVE), plural));
    }

    protected void rule(String[][] list) {
        for (String[] pair : list) {
            this.rules.add(new TwoFormInflector.RegExpRule(Pattern.compile(pair[0], Pattern.CASE_INSENSITIVE), pair[1]));
        }
    }

    protected void categoryRule(String[] list, String singular, String plural) {
        this.rules.add(new TwoFormInflector.CategoryRule(list, singular, plural));
    }

    private interface Rule {
        String getPlural(String singular);
    }

    private static class RegExpRule implements TwoFormInflector.Rule {
        private final Pattern singular;
        private final String plural;

        private RegExpRule(Pattern singular, String plural) {
            this.singular = singular;
            this.plural = plural;
        }

        @Override
        public String getPlural(String word) {
            StringBuffer buffer = new StringBuffer();
            Matcher matcher = this.singular.matcher(word);
            if (matcher.find()) {
                matcher.appendReplacement(buffer, this.plural);
                matcher.appendTail(buffer);
                return buffer.toString();
            }
            return null;
        }
    }

    private static class CategoryRule implements TwoFormInflector.Rule {
        private final String[] list;
        private final String singular;
        private final String plural;

        public CategoryRule(String[] list, String singular, String plural) {
            this.list = list;
            this.singular = singular;
            this.plural = plural;
        }

        @Override
        public String getPlural(String word) {
            String lowerWord = word.toLowerCase();
            for (String suffix : this.list) {
                if (lowerWord.endsWith(suffix)) {
                    if (!lowerWord.endsWith(this.singular)) {
                        throw new RuntimeException("Internal error");
                    }
                    return word.substring(0, word.length() - this.singular.length()) + this.plural;
                }
            }
            return null;
        }
    }
}
