package com.onlineStore.admin.settingTest;

import com.onlineStore.admin.setting.country.SettingRepository;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.setting.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
public class SettingRepositoryTests {

        @Autowired
        SettingRepository repo;

        @org.junit.jupiter.api.BeforeEach
        public void setUp() {
                com.onlineStoreCom.tenant.TenantContext.setTenantId(1L);
        }

        @Test
        public void testCreateGeneralSettings() {
                Setting siteName = new Setting("SITE_NAME", "Online Store", SettingCategory.GENERAL);
                Setting siteLogo = new Setting("SITE_LOGO", "Online Store.png", SettingCategory.GENERAL);
                Setting copyright = new Setting("COPYRIGHT", "Copyright (C) 2021 Online Store Ltd.",
                                SettingCategory.GENERAL);

                repo.saveAll(List.of(siteName, siteLogo, copyright));

                Iterable<Setting> iterable = repo.findAll();

                assertThat(iterable).size().isGreaterThan(0);
        }

        @Test
        public void testCreateGeneralSettings2() {
                Setting siteName = new Setting("PAYPAL_API_BASE_URL", "ORDER_CONFIRMATION", SettingCategory.PAYMENT);
                Setting siteLogo = new Setting("PAYPAL_API_CLIENT_ID", "ORDER_CONFIRMATION_CONTENT",
                                SettingCategory.PAYMENT);
                Setting siteLogo1 = new Setting("PAYPAL_API_CLIENT_SECRET", "ORDER_CONFIRMATION_CONTENT",
                                SettingCategory.PAYMENT);

                repo.saveAll(List.of(siteName, siteLogo, siteLogo1));

                Iterable<Setting> iterable = repo.findAll();

                assertThat(iterable).size().isGreaterThan(0);
        }

        @Test
        public void testCreateCurrencySettings() {
                Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
                Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
                Setting symbolPosition = new Setting("CURRENCY_SYMBOLE_POSITION", "before", SettingCategory.CURRENCY);
                Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
                Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
                Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

                repo.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType,
                                decimalDigits, thousandsPointType));

        }

        @Test
        public void testListSettingsByCategory() {
                List<Setting> settings = repo.findByCategory(SettingCategory.GENERAL);

                settings.forEach(System.out::println);
        }

        @Test
        public void testCreateMailServerlSettings() {
                Setting siteName = new Setting("MAIL_HOST", "Online Store", SettingCategory.MAIL_SERVER);
                Setting siteLogo = new Setting("MAIL_PORT", "Online Store.png", SettingCategory.MAIL_SERVER);
                Setting copyright = new Setting("MAIL_USERNAME", "Copyright (C) 2021 Online Store Ltd.",
                                SettingCategory.MAIL_SERVER);
                Setting copyright1 = new Setting("MAIL_PASSWORD", "Copyright (C) 2021 Online Store Ltd.",
                                SettingCategory.MAIL_SERVER);
                Setting copyright2 = new Setting("SMTP_AUTH", "true", SettingCategory.MAIL_SERVER);
                Setting copyright3 = new Setting("SMTP_SECURED", "true", SettingCategory.MAIL_SERVER);
                Setting copyright4 = new Setting("MAIL_FROM", "Copyright (C) 2021 Online Store Ltd.",
                                SettingCategory.MAIL_SERVER);
                Setting copyright5 = new Setting("MAIL_SENDER_NAME", "Copyright (C) 2021 Online Store Ltd.",
                                SettingCategory.MAIL_SERVER);
                Setting copyright6 = new Setting("CUSTOMER_VERIFY_SUBJECT", "Copyright (C) 2021 Online Store Ltd.",
                                SettingCategory.MAIL_TEMPLATES);
                Setting copyright7 = new Setting("CUSTOMER_VERIFY_CONTENT", "Copyright (C) 2021 Online Store Ltd.",
                                SettingCategory.MAIL_TEMPLATES);

                repo.saveAll(List.of(copyright1, copyright2, copyright3, copyright4, copyright5, copyright6,
                                copyright7));

                Iterable<Setting> iterable = repo.findAll();

                assertThat(iterable).size().isGreaterThan(0);
        }

}