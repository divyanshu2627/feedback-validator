package com.target.interview.feedbackvalidator.rest.service;

import com.target.interview.feedbackvalidator.rest.controller.FeedbackValidatorController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class FeedbackValidatorService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackValidatorService.class);

    private Map<String, String[]> words = new HashMap<>();
    private int largestWordLength = 0;

    @PostConstruct
    public void initialize() {
        loadBadWords();
    }

    private void loadBadWords() {
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
                String[] ignoreInCombinationWithWords = null;
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
        } catch (IOException io) {
            logger.error("exception while reading file" + io.getMessage());
        } catch (Exception e) {
            logger.error("generic exception while loading file" + e.getMessage());
        }
    }

    public ArrayList<String> findBadWords(String input) {
        if (StringUtils.isEmpty(input)) {
            return new ArrayList<>();
        }
        input = input.replaceAll("1", "i");
        input = input.replaceAll("!", "i");
        input = input.replaceAll("@", "a");
        input = input.replaceAll("5", "s");
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
                    for (int s = 0; ignoreCheck != null && s < ignoreCheck.length; s++) {
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

}
