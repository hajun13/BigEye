package com.example.simplewebserver;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StaticResourcesTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCssFileLoading() throws Exception {
        mockMvc.perform(get("/css/main.css"))
                .andExpect(status().isOk()) // 200 OK 상태를 기대
                .andExpect(content().contentType("text/css")); // 응답 타입이 CSS 파일인지 확인
    }

    @Test
    public void testJsFileLoading() throws Exception {
        mockMvc.perform(get("/js/main.js"))
                .andExpect(status().isOk()) // 200 OK 상태를 기대
                .andExpect(content().contentType("application/javascript")); // 응답 타입이 JavaScript 파일인지 확인
    }

    private ResultMatcher contentType(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'contentType'");
    }
}
