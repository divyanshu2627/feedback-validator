package com.target.interview.feedbackvalidator;

import com.target.interview.feedbackvalidator.rest.service.FeedbackValidatorService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackValidatorApplicationTests {

    @Autowired
    FeedbackValidatorService feedbackValidatorService;

    @Test
    public void findBadWords() {
        String inputWithBadWords = "he is rapist";
        String validInput = "hello you there?";
        ArrayList<String> firstResponse = feedbackValidatorService.findBadWords(inputWithBadWords);
        ArrayList<String> secondResponse = feedbackValidatorService.findBadWords(validInput);
        Assert.assertEquals(firstResponse.get(0), "rapist");
        Assert.assertTrue(secondResponse.isEmpty());

    }

}
