package ataf.core.helpers;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import ataf.core.utils.CryptoUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;

public class TestDataHelperTest {
    @BeforeClass
    public void testSetup() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
        TestDataHelper.initializeTestDataMap();
        CryptoUtils.setSecret(RandomStringUtils.secure().next(64).toCharArray());
    }

    @Test
    public void testTransformTestDataParameterTest() {
        final String CUSTOMER_SURNAME = "Vukmir";
        final String CUSTOMER_NAME = "Milosch";
        final String TICKET_IDENTIFIER = "133305072024";

        TestDataHelper.setTestData("customer_surname", CUSTOMER_SURNAME);
        TestDataHelper.setTestData("customer_name", CUSTOMER_NAME);
        TestDataHelper.setSuiteTestData("ticket_identifier", TICKET_IDENTIFIER);

        CustomAssertions.assertEquals(TestDataHelper.getTestData("customer_surname"), CUSTOMER_SURNAME);
        CustomAssertions.assertEquals(TestDataHelper.getTestData("customer_name"), CUSTOMER_NAME);
        CustomAssertions.assertEquals(TestDataHelper.getSuiteTestData("ticket_identifier"), TICKET_IDENTIFIER);

        final String TEST_DATA_PARAMETER_TEST_EXPECTED = "Hallo mein name ist " + CUSTOMER_NAME + " " + CUSTOMER_SURNAME + ". Meine Ticket nummer lautet: "
                + TICKET_IDENTIFIER;
        final String TEST_DATA_PARAMETER_TEST_ACTUAL = "Hallo mein name ist <TestData.customer_name> <TestData.customer_surname>. Meine Ticket nummer lautet: <SuiteTestData.ticket_identifier>";
        CustomAssertions.assertEquals(TestDataHelper.transformTestData(TEST_DATA_PARAMETER_TEST_ACTUAL), TEST_DATA_PARAMETER_TEST_EXPECTED);
    }

    @Test
    public void testTransformTestDataParameterWithSpecialCharacters() {
        final String NAME = "Götz";
        final String CITY = "München_123";

        TestDataHelper.setTestData("name", NAME);
        TestDataHelper.setTestData("city", CITY);

        final String expected = "Mein Name ist " + NAME + " und ich wohne in " + CITY + ".";
        final String actual = "Mein Name ist <TestData.name> und ich wohne in <TestData.city>.";

        CustomAssertions.assertEquals(TestDataHelper.transformTestData(actual), expected);
    }

    @Test
    public void testTransformTestDataParameterWithMissingKey() {
        final String actual = "Mein Name ist <TestData.unknown_key>.";
        CustomAssertions.assertThrows(IllegalArgumentException.class, () -> TestDataHelper.transformTestData(actual)); // Should throw an IllegalArgumentException
    }

    @Test
    public void testTransformTestDataParameterWithSuiteTestData() {
        final String ORDER_ID = "A12345";
        final String USER_ID = "U98765";

        TestDataHelper.setTestData("user_id", USER_ID);
        TestDataHelper.setSuiteTestData("order_id", ORDER_ID);

        final String expected = "Bestellung " + ORDER_ID + " gehört Benutzer " + USER_ID + ".";
        final String actual = "Bestellung <SuiteTestData.order_id> gehört Benutzer <TestData.user_id>.";

        CustomAssertions.assertEquals(TestDataHelper.transformTestData(actual), expected);
    }

    @Test
    public void testTransformTestDataParameterWithoutPlaceholders() {
        final String actual = "Dies ist ein normaler Satz ohne Platzhalter.";
        CustomAssertions.assertEquals(TestDataHelper.transformTestData(actual), actual);
    }

    @Test
    public void testTransformTestDataParameterAtStartAndEnd() {
        final String FIRSTNAME = "Anna";
        final String LASTNAME = "Müller";

        TestDataHelper.setTestData("firstname", FIRSTNAME);
        TestDataHelper.setTestData("lastname", LASTNAME);

        final String expected = FIRSTNAME + " arbeitet mit " + LASTNAME;
        final String actual = "<TestData.firstname> arbeitet mit <TestData.lastname>";

        CustomAssertions.assertEquals(TestDataHelper.transformTestData(actual), expected);
    }

    @Test
    public void testTransformTestDataParameterWithSimilarKeys() {
        final String KEY_1 = "value1";
        final String KEY_2 = "value2";

        TestDataHelper.setTestData("key", KEY_1);
        TestDataHelper.setTestData("key_extended", KEY_2);

        final String expected = "key: " + KEY_1 + ", extended key: " + KEY_2;
        final String actual = "key: <TestData.key>, extended key: <TestData.key_extended>";

        CustomAssertions.assertEquals(TestDataHelper.transformTestData(actual), expected);
    }

    @Test
    public void todayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayDayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_tag>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd")));
    }

    @Test
    public void todayWeekTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_woche>"),
                String.valueOf(LocalDateTime.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
    }

    @Test
    public void todayMonthTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_monat>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM")));
    }

    @Test
    public void todayYearTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_jahr>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy")));
    }

    @Test
    public void todayInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_invertiert>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }

    @Test
    public void todayPlusSevenDaysTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute+7_tage>"),
                LocalDateTime.now().plusDays(7L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayPlusTenWeeksTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute+10_WOCHEN>"),
                LocalDateTime.now().plusWeeks(10L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayPlusThirteenMonthsTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute+13_monate>"),
                LocalDateTime.now().plusMonths(13L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayPlusEightYearsTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute+8_jahre>"),
                LocalDateTime.now().plusYears(8L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayMinusThreeDaysTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute-3_tage>"),
                LocalDateTime.now().minusDays(3L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayMinusFiveWeeksTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute-5_WOCHEN>"),
                LocalDateTime.now().minusWeeks(5L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayMinusSevenMonthsTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute-7_monate>"),
                LocalDateTime.now().minusMonths(7L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayMinusFourYearsTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute-4_jahre>"),
                LocalDateTime.now().minusYears(4L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayPlusSevenDaysDayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_tag+7_tage>"),
                LocalDateTime.now().plusDays(7L).format(DateTimeFormatter.ofPattern("dd")));
    }

    @Test
    public void todayPlusTenWeeksDayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_tag+10_WOCHEN>"),
                LocalDateTime.now().plusWeeks(10L).format(DateTimeFormatter.ofPattern("dd")));
    }

    @Test
    public void todayPlusThirteenMonthsDayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_tag+13_monate>"),
                LocalDateTime.now().plusMonths(13L).format(DateTimeFormatter.ofPattern("dd")));
    }

    @Test
    public void todayPlusEightYearsDayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_tag+8_jahre>"),
                LocalDateTime.now().plusYears(8L).format(DateTimeFormatter.ofPattern("dd")));
    }

    @Test
    public void todayMinusThreeDaysDayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_tag-3_tage>"),
                LocalDateTime.now().minusDays(3L).format(DateTimeFormatter.ofPattern("dd")));
    }

    @Test
    public void todayMinusFiveWeeksDayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_tag-5_WOCHEN>"),
                LocalDateTime.now().minusWeeks(5L).format(DateTimeFormatter.ofPattern("dd")));
    }

    @Test
    public void todayMinusSevenMonthsDayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_tag-7_monate>"),
                LocalDateTime.now().minusMonths(7L).format(DateTimeFormatter.ofPattern("dd")));
    }

    @Test
    public void todayMinusFourYearsDayTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_tag-4_jahre>"),
                LocalDateTime.now().minusYears(4L).format(DateTimeFormatter.ofPattern("dd")));
    }

    @Test
    public void todayPlusSevenDaysWeekTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_woche+7_tage>"),
                String.valueOf(LocalDateTime.now().plusDays(7L).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
    }

    @Test
    public void todayPlusTenWeeksWeekTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_woche+10_WOCHEN>"),
                String.valueOf(LocalDateTime.now().plusWeeks(10L).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
    }

    @Test
    public void todayPlusThirteenMonthsWeekTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_woche+13_monate>"),
                String.valueOf(LocalDateTime.now().plusMonths(13L).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
    }

    @Test
    public void todayPlusEightYearsWeekTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_woche+8_jahre>"),
                String.valueOf(LocalDateTime.now().plusYears(8L).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
    }

    @Test
    public void todayMinusThreeDaysWeekTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_woche-3_tage>"),
                String.valueOf(LocalDateTime.now().minusDays(3L).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
    }

    @Test
    public void todayMinusFiveWeeksWeekTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_woche-5_WOCHEN>"),
                String.valueOf(LocalDateTime.now().minusWeeks(5L).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
    }

    @Test
    public void todayMinusSevenMonthsWeekTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_woche-7_monate>"),
                String.valueOf(LocalDateTime.now().minusMonths(7L).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
    }

    @Test
    public void todayMinusFourYearsWeekTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_woche-4_jahre>"),
                String.valueOf(LocalDateTime.now().minusYears(4L).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
    }

    @Test
    public void todayPlusSevenDaysMonthTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_monat+7_tage>"),
                LocalDateTime.now().plusDays(7L).format(DateTimeFormatter.ofPattern("MM")));
    }

    @Test
    public void todayPlusTenWeeksMonthTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_monat+10_WOCHEN>"),
                LocalDateTime.now().plusWeeks(10L).format(DateTimeFormatter.ofPattern("MM")));
    }

    @Test
    public void todayPlusThirteenMonthsMonthTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_monat+13_monate>"),
                LocalDateTime.now().plusMonths(13L).format(DateTimeFormatter.ofPattern("MM")));
    }

    @Test
    public void todayPlusEightYearsMonthTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_monat+8_jahre>"),
                LocalDateTime.now().plusYears(8L).format(DateTimeFormatter.ofPattern("MM")));
    }

    @Test
    public void todayMinusThreeDaysMonthTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_monat-3_tage>"),
                LocalDateTime.now().minusDays(3L).format(DateTimeFormatter.ofPattern("MM")));
    }

    @Test
    public void todayMinusFiveWeeksMonthTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_monat-5_WOCHEN>"),
                LocalDateTime.now().minusWeeks(5L).format(DateTimeFormatter.ofPattern("MM")));
    }

    @Test
    public void todayMinusSevenMonthsMonthTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_monat-7_monate>"),
                LocalDateTime.now().minusMonths(7L).format(DateTimeFormatter.ofPattern("MM")));
    }

    @Test
    public void todayMinusFourYearsMonthTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_monat-4_jahre>"),
                LocalDateTime.now().minusYears(4L).format(DateTimeFormatter.ofPattern("MM")));
    }

    @Test
    public void todayPlusSevenDaysYearTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_jahr+7_tage>"),
                LocalDateTime.now().plusDays(7L).format(DateTimeFormatter.ofPattern("yyyy")));
    }

    @Test
    public void todayPlusTenWeeksYearTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_jahr+10_WOCHEN>"),
                LocalDateTime.now().plusWeeks(10L).format(DateTimeFormatter.ofPattern("yyyy")));
    }

    @Test
    public void todayPlusThirteenMonthsYearTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_jahr+13_monate>"),
                LocalDateTime.now().plusMonths(13L).format(DateTimeFormatter.ofPattern("yyyy")));
    }

    @Test
    public void todayPlusEightYearsYearTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_jahr+8_jahre>"),
                LocalDateTime.now().plusYears(8L).format(DateTimeFormatter.ofPattern("yyyy")));
    }

    @Test
    public void todayMinusThreeDaysYearTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_jahr-3_tage>"),
                LocalDateTime.now().minusDays(3L).format(DateTimeFormatter.ofPattern("yyyy")));
    }

    @Test
    public void todayMinusFiveWeeksYearTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_jahr-5_WOCHEN>"),
                LocalDateTime.now().minusWeeks(5L).format(DateTimeFormatter.ofPattern("yyyy")));
    }

    @Test
    public void todayMinusSevenMonthsYearTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_jahr-7_monate>"),
                LocalDateTime.now().minusMonths(7L).format(DateTimeFormatter.ofPattern("yyyy")));
    }

    @Test
    public void todayMinusFourYearsYearTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_jahr-4_jahre>"),
                LocalDateTime.now().minusYears(4L).format(DateTimeFormatter.ofPattern("yyyy")));
    }

    @Test
    public void todayPlusSevenDaysInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_invertiert+7_tage>"),
                LocalDateTime.now().plusDays(7L).format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }

    @Test
    public void todayPlusTenWeeksInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_invertiert+10_WOCHEN>"),
                LocalDateTime.now().plusWeeks(10L).format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }

    @Test
    public void todayPlusThirteenMonthsInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_invertiert+13_monate>"),
                LocalDateTime.now().plusMonths(13L).format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }

    @Test
    public void todayPlusEightYearsInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_invertiert+8_jahre>"),
                LocalDateTime.now().plusYears(8L).format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }

    @Test
    public void todayMinusThreeDaysInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_invertiert-3_tage>"),
                LocalDateTime.now().minusDays(3L).format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }

    @Test
    public void todayMinusFiveWeeksInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_invertiert-5_WOCHEN>"),
                LocalDateTime.now().minusWeeks(5L).format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }

    @Test
    public void todayMinusSevenMonthsInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_invertiert-7_monate>"),
                LocalDateTime.now().minusMonths(7L).format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }

    @Test
    public void todayMinusFourYearsInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_invertiert-4_jahre>"),
                LocalDateTime.now().minusYears(4L).format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }

    @Test
    public void todayTimeTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_uhrzeit>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @Test
    public void todayHourTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_stunde>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")));
    }

    @Test
    public void todayMinuteTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_minute>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("mm")));
    }

    @Test
    public void todaySecondTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_sekunde>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("ss")));
    }

    @Test
    public void todayTimeInvertedTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_uhrzeit_invertiert>"),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH.mm.ss")));
    }

    @Test
    public void todayPlusTwoHoursTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute+2_stunden>"),
                LocalDateTime.now().plusHours(2L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayMinusFifteenMinutesTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute-15_minuten>"),
                LocalDateTime.now().minusMinutes(15L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Test
    public void todayTimePlusThreeHoursTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_uhrzeit+3_stunden>"),
                LocalDateTime.now().plusHours(3L).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @Test
    public void todayHourMinusOneHourTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_stunde-1_stunde>"),
                LocalDateTime.now().minusHours(1L).format(DateTimeFormatter.ofPattern("HH")));
    }

    @Test
    public void todayMinutePlusTenMinutesTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_minute+10_minuten>"),
                LocalDateTime.now().plusMinutes(10L).format(DateTimeFormatter.ofPattern("mm")));
    }

    @Test
    public void todaySecondMinusThirtySecondsTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_sekunde-30_sekunden>"),
                LocalDateTime.now().minusSeconds(30L).format(DateTimeFormatter.ofPattern("ss")));
    }

    @Test
    public void todayTimeInvertedPlusTwoHoursTest() {
        CustomAssertions.assertEquals(TestDataHelper.transformTestData("<heute_uhrzeit_invertiert+2_stunden>"),
                LocalDateTime.now().plusHours(2L).format(DateTimeFormatter.ofPattern("HH.mm.ss")));
    }

    @AfterClass
    public void testTearDown() {
        TestDataHelper.flushMapTestData();
        TestDataHelper.flushMapSuiteTestData();
        CryptoUtils.clearSecret();
    }
}
