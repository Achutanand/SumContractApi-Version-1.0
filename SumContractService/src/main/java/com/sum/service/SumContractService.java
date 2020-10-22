package com.sum.service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sum.util.SumContractMemoryCache;

@Service
public class SumContractService {

	private static final Logger log = LoggerFactory.getLogger(SumContractService.class);

	public static final String CLIENT_MESSAGE = "Hey Service, can you provide me a question with numbers to add?";
	public static final String CLIENT_QUESTION = "CLIENT_QUESTION";
	public static final String CLIENT_INFO_QUESTION = "Here you go, solve the question: “Please sum the numbers ";
	public static final String SUCCESS_MESSAGE = "Thats Great";
	public static final String FAILURE_MESSAGE = "That’s wrong. Please try again.";
	public static final String SESSION_FAILURE = "Session Is not Available Currently, Could You Please Try After SomeTime !!!";

	SumContractMemoryCache<String, Object> cache = new SumContractMemoryCache<String, Object>(500, 500, 10);

	public SumContractMemoryCache<String, Object> getCache() {
		return this.cache;
	}

	public String getQuestion(HttpSession session) {
		if (isAvailable(session)) {
			List<Object> question = getRandomNumbers();
			cache.put(CLIENT_QUESTION + "-" + session.getId(), question);
			log.info("Generated Numbers are : " + Arrays.deepToString(question.toArray()));
			return CLIENT_INFO_QUESTION + Arrays.deepToString(question.toArray());
		}
		return SESSION_FAILURE;
	}

	public ResponseEntity<String> executeSum(List<Integer> values, int sumTotal, HttpSession session) {
		if (isAvailable(session)) {
			Integer sum = getSumOfNumbers(values);
			log.info("The sum of number is : " + sum);
			List<Integer> listFromSession = (List<Integer>) cache.get(CLIENT_QUESTION + "-" + session.getId());
			if (sum == sumTotal && values.equals(listFromSession)) {
				return ResponseEntity.ok(SUCCESS_MESSAGE);
			} else {
				return new ResponseEntity(FAILURE_MESSAGE, new HttpHeaders(), HttpStatus.BAD_REQUEST);

			}
		}
		return new ResponseEntity(SESSION_FAILURE, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	public static boolean isAvailable(Object value) {
		return (value != null);
	}

	public Integer getSumOfNumbers(List<Integer> values) {
		return values.stream().reduce(0, (a, b) -> a + b);
	}

	public List<Object> getRandomNumbers() {
		return new Random().ints(0, 350).limit(10).boxed().collect(Collectors.toList());
	}

}
