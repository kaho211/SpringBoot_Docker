package com.example.app.users;

import com.example.app.CsvDataSetLoader;
import com.example.app.entity.User;
import com.example.app.service.UserService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.web.reactive.function.BodyInserters.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import javax.transaction.Transactional;

// csvファイルの読み込みを可能にする
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
// テストコードの実行の前後に行われる処理を定義したもののうち、必要なものを読み込ませる
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class, // DIコンテナからインジェクションすることが可能
        TransactionalTestExecutionListener.class, // DB操作後は、DBを元の状態に戻しておく処理
        DbUnitTestExecutionListener.class // テストの前後でのデータベースの状態を後述のアノテーションで設定
})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "50000")
@AutoConfigureMockMvc
@Transactional // メソッドの実行のたびにロールバックする
@Import(TestConfiguration.class)
@DatabaseSetup(value = "/combineTestData/") // 初期状態のDBを定義

public class CombineTests {

    @Autowired
    private UserService userService;
    // webクライアントの設定
    @Autowired
    private WebTestClient webClient;

    @Test
    void 結合テスト() {
        // 既存のDBからレコード（csvファイルで登録された2件のデータ）を取得
        List<User> userListBefore = userService.findAll();
        assertThat(userListBefore.size(), is(2));

        // 新規作成画面への遷移
        this.webClient.get().uri("/users/new").exchange().expectStatus().isOk();

        // データの入力
        String name = "combinetest3";
        String email = "combinetest3@email.com";
        String inquiry = "結合テスト3です。";

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setInquiry(inquiry);

        // 確認画面への遷移、及び入力データの受け渡し
        this.webClient.post()
                .uri("/users/confirm")
                .body(fromFormData("name", name).with("email", email).with("inquiry", inquiry))
                .exchange()
                .expectStatus()
                .isOk();

        assertThat(user.getName(), is(name));
        assertThat(user.getEmail(), is(email));
        assertThat(user.getInquiry(), is(inquiry));

        // 完了画面への遷移、及び入力データをDBに保存
        this.webClient.post()
                .uri("/users")
                .body(fromFormData("name", name).with("email", email).with("inquiry", inquiry))
                .exchange()
                .expectStatus()
                .isOk();

        // 保存後のDBからレコード（合計３件のデータが登録されているか）の取得
        List<User> userListAfter = userService.findAll();
        assertThat(userListAfter.size(), is(3));
    }
}
