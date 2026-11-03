package com.isanorte.constructora_api.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.transaction.annotation.Transactional;

class DemoProductSeederTransactionTest {
    @Test
    void checkedIoExceptionIsConfiguredForRollback() throws Exception {
        Transactional transactional = DemoProductSeeder.class
                .getMethod("run", ApplicationArguments.class)
                .getAnnotation(Transactional.class);

        assertThat(transactional).isNotNull();
        assertThat(transactional.rollbackFor()).containsExactly(java.io.IOException.class);
    }
}
