package com.sum.controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sum.service.SumContractService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class SumContractController {
	
	
	@Autowired
	private SumContractService sumContractService;
	

	@ApiOperation(value = "Get the Informational Message for the User")
	@RequestMapping(method = RequestMethod.GET, value= "/getMessage")
	public ResponseEntity<String> getQuestion(HttpSession session) {
		return ResponseEntity.ok(SumContractService.CLIENT_MESSAGE);
	}

	@ApiOperation(value = "Get the List of Questions for the User")
	@RequestMapping(method = RequestMethod.GET, value= "/getQuestion")
	public ResponseEntity<String> getNumbers(HttpSession session) {
		return ResponseEntity.ok(sumContractService.getQuestion(session));
	}

	@ApiOperation(value = "Execute the Sum of the Numbers for the User")
	@RequestMapping(method = RequestMethod.GET, value = "/execute")
	public ResponseEntity execute(@RequestParam("values") List<Integer> values,
			@RequestParam(name = "sumTotal") int sumTotal, HttpSession session) {
		return sumContractService.executeSum(values, sumTotal, session);

	}
}
