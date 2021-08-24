package com.fishedee.web_boost.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebBoostParamRequestTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testRequestParamRequiredFail()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(str.contains("Required request parameter 'salesOrderId' for method"));
    }

    @Test
    public void testRequestParamHave()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get")
                .param("salesOrderId","123")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":\"result_123\"}");
    }

    @Test
    public void testRequestParamNotValid()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get2")
                .param("salesOrderId","9")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(str.contains("get2.salesOrderId:must be greater than or equal to 10"));
    }

    @Test
    public void testRequestParamValid()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get2")
                .param("salesOrderId","456")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":\"result2_456\"}");
    }
}
