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

import com.example.risk.controller.DiagnosisController;
import com.example.risk.controller.MainController;



@WebMvcTest(DiagnosisController.class)
public class DiagnosisControllerTest {
    @Autowired
    private MockMvc mockmvc;
    @Test
    void アップロード画面表示_正常系1() throws Exception{
        mockmvc.perform(get("/diagnosis/top"))
            .andExpect(status().isOk())
            .andExpect(view().name("diagnosis/upload"));
    }
}
