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
public class WebBoostResponseTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testStringReturn()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/user/go1")
                .contentType(MediaType.APPLICATION_JSON);
       String str = mockMvc.perform(requestBuilder)
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andReturn().getResponse().getContentAsString();
       assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":\"123\"}");
    }

    @Test
    public void testObjectReturn()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/user/go4")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":{\"id\":1,\"name\":\"fish\",\"age\":12}}");
    }

    @Test
    public void testObjectListReturn()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/user/go5")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":[{\"id\":1,\"name\":\"fish\",\"age\":12},{\"id\":2,\"name\":\"cat\",\"age\":78}]}");
    }

    @Test
    public void testWebBoostExceptionReturn()throws Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/user/go2")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":100,\"msg\":\"go2 error\",\"data\":null}");
    }

    @Test
    public void testRuntimeExceptionReturn()throws Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/user/go3")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":500,\"msg\":\"Internal Server Error\",\"data\":null}");
    }

    @Test
    public void testMyExceptionReturn()throws Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/user/go6")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":1001,\"msg\":\"go6_e\",\"data\":null}");
    }
}
