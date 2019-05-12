package com.target.interview.feedbackvalidator.rest.controller;

import com.target.interview.feedbackvalidator.rest.constant.FeedbackResponse;
import com.target.interview.feedbackvalidator.rest.service.FeedbackValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class FeedbackValidatorController {

    @Autowired
    FeedbackValidatorService feedbackValidatorService;

    @RequestMapping(value = "/feedback", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> validateFeedback(@RequestParam("input") String input) {
        if (StringUtils.isEmpty(input)) {
            return new ResponseEntity<>(FeedbackResponse.feedBackEmpty, HttpStatus.BAD_REQUEST);
        }
        ArrayList<String> badWords = feedbackValidatorService.findBadWords(input);
        if (badWords.size() > 0) {
            return new ResponseEntity<>(FeedbackResponse.feedBackContainsBadWords, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(FeedbackResponse.feedBackSubmitted, HttpStatus.OK);
        }
    }
}
