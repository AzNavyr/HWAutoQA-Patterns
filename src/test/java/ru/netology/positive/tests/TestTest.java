package ru.netology.positive.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class TestTest {
    Faker fake = new Faker();

    @BeforeEach
    void setAll(){
        Configuration.browser = "firefox";
        Configuration.browserSize = "874x769";
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").val(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='success-notification'] .notification__title").should(visible, Duration.ofSeconds(15));
        $("[class='notification__content']")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));

        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").val(secondMeetingDate);
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='replan-notification'] .button").should(visible, Duration.ofSeconds(15)).click();
        $("[class='notification__content']")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate), Duration.ofSeconds(15));
    }

    @Test
    void shouldCorrectlyFilledOutForm() throws InterruptedException {
        $("[data-test-id=city] .input__control").val("Симферополь");
        String date = LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        Thread.sleep(2000);
        $x("//input[@type= 'tel']").val(date);
        $("[data-test-id=name] .input__control").val("Шувалов Дмитрий");
        $("[data-test-id=\"phone\"] input").val("+79788885522");
        $("[data-test-id='agreement']").click();
        $$("[role=\"button\"]").find(exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " +date))
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldEnterHyphenatedName(){
        $("[data-test-id=city] .input__control").val("Казань");
        String date = LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $x("//input[@type= 'tel']").val(date);
        $("[data-test-id=name] .input__control").val("Шувалов Максим-Иван");
        $("[data-test-id=\"phone\"] input").val("+79788885522");
        $("[data-test-id='agreement']").click();
        $$("[role=\"button\"]").find(Condition.exactText("Забронировать")).click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " +date))
                .shouldBe(visible, Duration.ofSeconds(15));
    }


}