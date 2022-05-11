package com.example.app.users;

// import com.example.practice.PracticeApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class UsersControllerUnitTests {
        // Mockオブジェクト
        @Autowired
        private MockMvc mockMvc;
        private Object User;

        @Test
        // getリクエスト 問い合わせ画面
        void index処理でviewとしてindexが渡される() throws Exception {
                this.mockMvc.perform(get("/users/"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/index"));
        }

        @Test
        // getリクエスト 新規作成画面
        void newUser処理でviewとしてnewが渡される() throws Exception {
                this.mockMvc.perform(get("/users/new"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("users/new"));
        }

        @Test
        // Entityに納められたデータを引っ張ってこれているか
        void フォームに値が入力されている場合は確認画面へ() throws Exception {
                // ContactForm contactForm = new ContactForm();
                // contactForm.setName("test");
                // contactForm.setMail("test@email.com");
                // contactForm.setContent("テストです。");

                this.mockMvc
                                .perform((post("/users/confirm"))
                                                .param("name", "テスト")
                                                .param("email", "test@test")
                                                .param("inquiry", "テストです。"))
                                .andExpect(status().isOk())
                                .andExpect(model().hasNoErrors())
                                .andExpect(model().attribute("User", User))
                                .andExpect(view().name("users/confirm"));
        }

        @Test
        void 確認画面から完了画面へ() throws Exception {
                this.mockMvc
                                .perform((post("/users/"))
                                                .param("name", "テスト")
                                                .param("email", "test@test")
                                                .param("inquiry", "テストです。"))
                                .andExpect(status().isOk())
                                .andExpect(model().hasNoErrors())
                                .andExpect(model().attribute("User", User))
                                .andExpect(view().name("users/complete"));
        }
}
