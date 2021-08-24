package com.fishedee.web_boost.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fishedee.web_boost.sample.api.SalesOrder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
public class WebBoostMixJsonRequestTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetMixParamAndJson()throws  Exception{
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setName("fish");
        salesOrder.setAge(901);
        salesOrder.setItemList(Arrays.asList(
                new SalesOrder.Item("item1",10),
                new SalesOrder.Item("item2",20),
                new SalesOrder.Item("item3",30)
        ));
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/mix/get")
                .param("salesOrderId","123")
                .param("data",objectMapper.writeValueAsString(salesOrder))
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":{\"salesOrderId\":123,\"salesOrder\":{\"name\":\"fish\",\"age\":901,\"itemList\":[{\"name\":\"item1\",\"count\":10},{\"name\":\"item2\",\"count\":20},{\"name\":\"item3\",\"count\":30}]}}}");
    }

    @Test
    public void testGetMixMultiJson()throws  Exception{
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setName("fish");
        salesOrder.setAge(901);
        salesOrder.setItemList(Arrays.asList(
                new SalesOrder.Item("item1",10),
                new SalesOrder.Item("item2",20),
                new SalesOrder.Item("item3",30)
        ));
        String temp = objectMapper.writeValueAsString(salesOrder);

        temp = "{\"salesOrderId\":123,"+temp.substring(1,temp.length()-1)+"}";
        log.info("temp {}",temp);
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/mix/get2")
                .param("data",temp)
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":{\"salesOrderId\":123,\"salesOrder\":{\"name\":\"fish\",\"age\":901,\"itemList\":[{\"name\":\"item1\",\"count\":10},{\"name\":\"item2\",\"count\":20},{\"name\":\"item3\",\"count\":30}]}}}");
    }

    @Test
    public void testPostMixMultiJson()throws  Exception{
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setName("fish");
        salesOrder.setAge(901);
        salesOrder.setItemList(Arrays.asList(
                new SalesOrder.Item("item1",10),
                new SalesOrder.Item("item2",20),
                new SalesOrder.Item("item3",30)
        ));
        String temp = objectMapper.writeValueAsString(salesOrder);

        temp = "{\"salesOrderId\":123,"+temp.substring(1,temp.length()-1)+"}";
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.post("/mix/post")
                .content(temp)
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":{\"salesOrderId\":123,\"salesOrder\":{\"name\":\"fish\",\"age\":901,\"itemList\":[{\"name\":\"item1\",\"count\":10},{\"name\":\"item2\",\"count\":20},{\"name\":\"item3\",\"count\":30}]}}}");
    }
}
