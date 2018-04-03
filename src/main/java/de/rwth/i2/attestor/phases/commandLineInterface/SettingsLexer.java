package de.rwth.i2.attestor.phases.commandLineInterface;

import java.util.ArrayList;
import java.util.List;

public class SettingsLexer {

    private static final char STRING_DELIMITER = '"';
    private static final char DELIMITER = ' ';
    private static final char LINE_DELIMITER = '\n';
    private static final char COMMENT_DELIMITER = '#';


    private List<String> lexemes = new ArrayList<>();
    private List<String> settings;

    public SettingsLexer(List<String> settings) {

        this.settings = settings;

        for(String line : settings) {
            lexLine(line);
        }
    }

    public List<String> getLexemes() {

        return lexemes;
    }

    private void lexLine(String line) {

        boolean stringMode = false;

        // get rid of invisible unicode characters
        line.replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "?").trim();

        StringBuilder lexemeBuilder = new StringBuilder();

        if(line.length() > 0 && line.charAt(0) == COMMENT_DELIMITER) {
            return;
        }

        for(int i=0; i < line.length(); i++) {
            char next = line.charAt(i);

            switch(next) {
                case STRING_DELIMITER:
                    if(stringMode) {
                        lexemes.add(lexemeBuilder.toString());
                        lexemeBuilder = new StringBuilder();
                        stringMode = false;
                    } else {
                        stringMode = true;
                    }
                    break;
                case DELIMITER:
                case LINE_DELIMITER:
                    if(stringMode) {
                       lexemeBuilder.append(next);
                    } else {
                        String l = lexemeBuilder.toString();
                        if(!l.isEmpty()) {
                            lexemes.add(lexemeBuilder.toString());
                            lexemeBuilder = new StringBuilder();
                        }
                    }
                    break;
                default:
                    lexemeBuilder.append(next);
                    break;
            }
        }

        String l = lexemeBuilder.toString();
        if(!l.isEmpty()) {
            lexemes.add(lexemeBuilder.toString());
        }

    }
}
