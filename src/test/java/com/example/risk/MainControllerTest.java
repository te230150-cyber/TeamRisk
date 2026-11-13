package com.example.risk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.risk.controller.MainController;

@WebMvcTest(MainController.class)
public class MainControllerTest {
    @Autowired
    private MockMvc mockmvc;
    @Test
    void top画面表示_正常系1() throws Exception{
        mockmvc.perform(get(""))
            .andExpect(status().isOk())
            .andExpect(view().name("top"));
    }
    @Test
    void top画面表示_正常系2() throws Exception{
        mockmvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("top"));
    }
    @Test
    void top画面表示_正常系3() throws Exception{
        mockmvc.perform(get("/top"))
            .andExpect(status().isOk())
            .andExpect(view().name("top"));
    }

    @Test
    void 画像診断top表示_リダイレクト() throws Exception{
        mockmvc.perform(get("/diagnosis"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/diagnosis/top"));
    }
    @Test
    void クイズtop表示_リダイレクト() throws Exception{
        mockmvc.perform(get("/quiz"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/quiz/top"));
    }
}
