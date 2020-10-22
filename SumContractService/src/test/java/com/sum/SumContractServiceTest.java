package com.sum;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import java.util.Map.Entry;

import org.apache.commons.collections4.map.HashedMap;
import org.assertj.core.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;

import com.sum.service.SumContractService;
import com.sum.util.SumContractMemoryCache;

@WebMvcTest
class SumContractServiceTest {
	
	
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private SumContractService sumContractService;
    
    @MockBean
    private HttpSession session;

    @Test
    public void testGetQuestion() throws Exception {
    	Mockito.when(sumContractService.getQuestion(session)).thenReturn(sumContractService.CLIENT_MESSAGE);
    	MvcResult requestResult = mockMvc.perform(get("/api/getMessage"))
                .andExpect(status().isOk()).andExpect(status().isOk()).andReturn();
        String result = requestResult.getResponse().getContentAsString();
        assertEquals(result, sumContractService.CLIENT_MESSAGE);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testQuestionAndSum_Success_Message() throws Exception {
    	SumContractService sum = new SumContractService();
    	doReturn("CLIENT_SESSION_ID").when(session).getId();
    	String question = sum.getQuestion(session);
    	Mockito.when(sumContractService.getQuestion(session)).thenReturn(question);
    	MvcResult requestResult = mockMvc.perform(get("/api/getQuestion")).andExpect(status().isOk()).andExpect(status().isOk()).andReturn();
		List<Integer> questionList = (List<Integer>) sum.getCache().get(sumContractService.CLIENT_QUESTION +"-"+ session.getId());
		Integer actualSumTotal = sum.getSumOfNumbers(questionList);
		Mockito.when(sumContractService.getSumOfNumbers(questionList)).thenReturn(actualSumTotal);
		ResponseEntity<String> response = sum.executeSum(questionList, actualSumTotal, session);
		assertEquals(requestResult.getResponse().getStatus(), 200);
    	assertEquals(response.getBody(), sumContractService.SUCCESS_MESSAGE);
    	assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testQuestionAndSum_Failure_Message() throws Exception {
    	SumContractService sum = new SumContractService();
    	doReturn("CLIENT_SESSION_ID").when(session).getId();
    	String question = sum.getQuestion(session);
    	Mockito.when(sumContractService.getQuestion(session)).thenReturn(question);
    	MvcResult requestResult = mockMvc.perform(get("/api/getQuestion")).andExpect(status().isOk()).andReturn();
		List<Integer> questionList = (List<Integer>) sum.getCache().get(sumContractService.CLIENT_QUESTION +"-"+ session.getId());
		Integer actualSumTotal = sum.getSumOfNumbers(questionList);
		Mockito.when(sumContractService.getSumOfNumbers(questionList)).thenReturn(actualSumTotal);
		ResponseEntity<String> response = sum.executeSum(questionList, 25, session);
		assertEquals(requestResult.getResponse().getStatus(), 200);
    	assertEquals(response.getBody(), SumContractService.FAILURE_MESSAGE);
    	assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
    
    
    @Test
    @SuppressWarnings("unchecked")
    public void testQuestionAndSum__Safeguard_Against_Cheating() throws Exception {
    	SumContractService sum = new SumContractService();
    	doReturn("CLIENT_SESSION_ID").when(session).getId();
    	String question = sum.getQuestion(session);
    	Mockito.when(sumContractService.getQuestion(session)).thenReturn(question);
    	MvcResult requestResult = mockMvc.perform(get("/api/getQuestion")).andExpect(status().isOk()).andReturn();
		List<Integer> questionList = (List<Integer>) sum.getCache().get(sumContractService.CLIENT_QUESTION +"-"+ session.getId());
		List<Integer> listDummy = java.util.Arrays.asList(new Integer[] { 10, 20, 30, 40 });
		Integer actualSumTotalForCheat = sum.getSumOfNumbers(listDummy);
		Integer actualSumTotalOriginal = sum.getSumOfNumbers(questionList);
		Mockito.when(sumContractService.getSumOfNumbers(listDummy)).thenReturn(actualSumTotalForCheat);
		ResponseEntity<String> response = sum.executeSum(listDummy, actualSumTotalForCheat, session);
		assertNotEquals(actualSumTotalForCheat, actualSumTotalOriginal);
    	assertEquals(response.getBody(), SumContractService.FAILURE_MESSAGE);
    	assertEquals(requestResult.getResponse().getStatus(), 200);
    	assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

}
