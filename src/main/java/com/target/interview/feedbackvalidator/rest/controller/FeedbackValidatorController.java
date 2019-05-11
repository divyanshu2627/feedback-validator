package com.target.interview.feedbackvalidator.rest.controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class FeedbackValidatorController {

    private Map<String, String[]> words = new HashMap<>();
    private int largestWordLength = 0;

    private static final String feedBackContainsBadWords = "Your feedback/comment contains bad words, please contact customer support if you have any questions";
    private static final String feedBackSubmitted = "Your feedback/comment submitted successfully";

    private static Logger log = Logger.getLogger(FeedbackValidatorController.class.getName());

    @PostConstruct
    public void initialize() {
        loadBadWords();
    }

    public void loadBadWords() {
        //for reference I have taken bad words from google spreadsheet and put it in csv file
        // https://docs.google.com/spreadsheets/d/1hIEi2YG3ydav1E06Bzf2mQbGZ12kh2fe4ISgLg_UBuM/export?format=csv
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/bad_words.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String currentLine;
            int lineCount = 0;
            while ((currentLine = br.readLine()) != null) {
                lineCount++;
                if (lineCount == 1) {
                    continue;
                }
                String[] content = currentLine.split(",");
                String[] ignoreInCombinationWithWords = new String[]{};
                if (content.length == 0 || StringUtils.isEmpty(content[0])) {
                    continue;
                }
                String word = content[0];
                if (content.length > 1) {
                    ignoreInCombinationWithWords = content[1].split("_");
                }
                if (word.length() > largestWordLength) {
                    largestWordLength = word.length();
                }
                words.put(word.replaceAll(" ", "").toLowerCase(), ignoreInCombinationWithWords);

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    public ArrayList<String> findBadWords(String input) {
        if (StringUtils.isEmpty(input)) {
            return new ArrayList<>();
        }
        input = input.replaceAll("1", "i");
        input = input.replaceAll("!", "i");
        input = input.replaceAll("3", "e");
        input = input.replaceAll("4", "a");
        input = input.replaceAll("@", "a");
        input = input.replaceAll("5", "s");
        input = input.replaceAll("7", "t");
        input = input.replaceAll("0", "o");
        input = input.replaceAll("9", "g");

        ArrayList<String> badWords = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");

        for (int start = 0; start < input.length(); start++) {
            for (int offset = 1; offset < (input.length() + 1 - start) && offset < largestWordLength; offset++) {
                String wordToCheck = input.substring(start, start + offset);
                if (words.containsKey(wordToCheck)) {
                    String[] ignoreCheck = words.get(wordToCheck);
                    boolean ignore = false;
                    for (int s = 0; s < ignoreCheck.length; s++) {
                        if (input.contains(ignoreCheck[s])) {
                            ignore = true;
                            break;
                        }
                    }
                    if (!ignore) {
                        badWords.add(wordToCheck);
                    }
                }
            }
        }
        return badWords;

    }


    @RequestMapping(value = "/feedback/{feedback}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getCache(@PathVariable("feedback") String feedback) {
        ArrayList<String> badWords = findBadWords(feedback);
        if (badWords.size() > 0) {
            return feedBackContainsBadWords;
        } else {
            return feedBackSubmitted;
        }
    }
}
