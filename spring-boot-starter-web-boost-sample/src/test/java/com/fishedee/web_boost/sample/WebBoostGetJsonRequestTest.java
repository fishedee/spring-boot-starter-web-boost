package com.fishedee.web_boost.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fishedee.web_boost.sample.api.SalesOrder;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class WebBoostGetJsonRequestTest {
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
    public void testGetJsonPrimitiveEmpty()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get3")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":\"result3_0\"}");
    }

    @Test
    public void testGetJsonPrimitive()throws  Exception{
        Map map = new HashMap<>();
        map.put("salesOrderId","123");
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get3")
                .param("data",objectMapper.writeValueAsString(map))
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":\"result3_123\"}");
    }

    @Test
    public void testGetJsonBoxEmpty()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get4")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":\"result4_null\"}");
    }

    @Test
    public void testGetJsonBox()throws  Exception{
        Map map = new HashMap<>();
        map.put("salesOrderId","456");
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get4")
                .param("data",objectMapper.writeValueAsString(map))
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":\"result4_456\"}");
    }

    @Test
    public void testGetJsonBoxNotNullEmpty()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get5")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(str.contains("get5.salesOrderId:must not be null"));
    }

    @Test
    public void testGetJsonBoxNotNull()throws  Exception{
        Map map = new HashMap<>();
        map.put("salesOrderId","789");
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get5")
                .param("data",objectMapper.writeValueAsString(map))
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":\"result5_789\"}");
    }

    @Test
    public void testGetJsonObjectNotNullEmpty()throws  Exception{
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get6")
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(str.contains("itemList:must not be empty"));
    }

    @Test
    public void testGetJsonObjectNotNull()throws  Exception{
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setName("fish");
        salesOrder.setAge(901);
        salesOrder.setItemList(Arrays.asList(
                new SalesOrder.Item("item1",10),
                new SalesOrder.Item("item2",20),
                new SalesOrder.Item("item3",30)
        ));
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get6")
                .param("data",objectMapper.writeValueAsString(salesOrder))
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(str,"{\"code\":0,\"msg\":\"\",\"data\":{\"name\":\"fish\",\"age\":901,\"itemList\":[{\"name\":\"item1\",\"count\":10},{\"name\":\"item2\",\"count\":20},{\"name\":\"item3\",\"count\":30}]}}");
    }

    @Test
    public void testGetJsonObjectItemNameEmpty()throws  Exception{
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setName("fish");
        salesOrder.setAge(901);
        salesOrder.setItemList(Arrays.asList(
                new SalesOrder.Item("",10)
        ));
        MockHttpServletRequestBuilder requestBuilder  = MockMvcRequestBuilders.get("/salesOrder/get6")
                .param("data",objectMapper.writeValueAsString(salesOrder))
                .contentType(MediaType.APPLICATION_JSON);
        String str = mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(str.contains("itemList[0].name:must not be blank"));
    }
}
