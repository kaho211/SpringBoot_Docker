package com.example.app;

import java.util.List;

import javax.transaction.Transactional;

import com.example.app.entity.User;
import com.example.app.service.UserService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

// csvファイルの読み込みを可能にする
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
// テストコードの実行の前後に行われる処理を定義したもののうち、必要なものを読み込ませる
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class, // DIコンテナからインジェクションすることが可能
        TransactionalTestExecutionListener.class, // DB操作後は、DBを元の状態に戻しておく処理
        DbUnitTestExecutionListener.class // テストの前後でのデータベースの状態を後述のアノテーションで設定
})
@SpringBootTest
@DatabaseSetup(value = "/testData/") // 初期状態のDBを定義
@Transactional // メソッドの実行のたびにロールバックする
@Import(TestConfiguration.class)

public class DBSelectTest {
    @Autowired
    private UserService userService;

    @Test
    public void contextLoads() throws Exception {
        List<User> userList = userService.findAll();

        // 正常にテーブルからレコードを取得できたか
        assertThat(userList.size(), is(2));
    }

}
