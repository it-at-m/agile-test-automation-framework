package ataf.core.helpers;

import ataf.core.assertions.CustomAssertions;
import ataf.core.assertions.strategy.impl.TestNGAssertionStrategy;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestPropertiesHelperTest {

    @BeforeClass
    public void setUp() {
        CustomAssertions.setStrategy(new TestNGAssertionStrategy());
    }

    @Test
    public void positiveBooleanTestVariantOne() {
        CustomAssertions.assertTrue(TestPropertiesHelper.getPropertyAsBoolean("booleanTest"));
    }

    @Test
    public void positiveBooleanTestVariantTwo() {
        CustomAssertions.assertTrue(TestPropertiesHelper.getPropertyAsBoolean("booleanTest", true));
    }

    @Test
    public void positiveBooleanTestVariantThree() {
        CustomAssertions.assertTrue(TestPropertiesHelper.getPropertyAsBoolean("booleanTest", true, true));
    }

    @Test
    public void negativeBooleanTestVariantOne() {
        CustomAssertions.assertFalse(TestPropertiesHelper.getPropertyAsBoolean(RandomStringUtils.secure().next(12)));
    }

    @Test
    public void negativeBooleanTestVariantTwo() {
        CustomAssertions.assertFalse(TestPropertiesHelper.getPropertyAsBoolean(RandomStringUtils.secure().next(12), true));
    }

    @Test
    public void negativeBooleanTestVariantThree() {
        CustomAssertions.assertTrue(TestPropertiesHelper.getPropertyAsBoolean(RandomStringUtils.secure().next(12), true, true));
    }

    @Test
    public void positiveByteTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsByte("byteTest"), (byte) 87);
    }

    @Test
    public void positiveByteTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsByte("byteTest", true), (byte) 87);
    }

    @Test
    public void positiveByteTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsByte("byteTest", true, (byte) 87), (byte) 87);
    }

    @Test
    public void negativeByteTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsByte(RandomStringUtils.secure().next(12)), (byte) -1);
    }

    @Test
    public void negativeByteTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsByte(RandomStringUtils.secure().next(12), true), (byte) -1);
    }

    @Test
    public void negativeByteTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsByte(RandomStringUtils.secure().next(12), true, (byte) 87), (byte) 87);
    }

    @Test
    public void positiveShortTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsShort("shortTest"), (short) 25000);
    }

    @Test
    public void positiveShortTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsShort("shortTest", true), (short) 25000);
    }

    @Test
    public void positiveShortTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsShort("shortTest", true, (short) 25000), (short) 25000);
    }

    @Test
    public void negativeShortTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsShort(RandomStringUtils.secure().next(12)), (short) -1);
    }

    @Test
    public void negativeShortTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsShort(RandomStringUtils.secure().next(12), true), (short) -1);
    }

    @Test
    public void negativeShortTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsShort(RandomStringUtils.secure().next(12), true, (short) 25000), (short) 25000);
    }

    @Test
    public void positiveIntegerTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsInteger("intTest"), -2000000000);
    }

    @Test
    public void positiveIntegerTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsInteger("intTest", true), -2000000000);
    }

    @Test
    public void positiveIntegerTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsInteger("intTest", true, -2000000000), -2000000000);
    }

    @Test
    public void negativeIntegerTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsInteger(RandomStringUtils.secure().next(12)), -1);
    }

    @Test
    public void negativeIntegerTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsInteger(RandomStringUtils.secure().next(12), true), -1);
    }

    @Test
    public void negativeIntegerTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsInteger(RandomStringUtils.secure().next(12), true, 2000000000), 2000000000);
    }

    @Test
    public void positiveLongTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsLong("longTest"), 9000000000000000000L);
    }

    @Test
    public void positiveLongTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsLong("longTest", true), 9000000000000000000L);
    }

    @Test
    public void positiveLongTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsLong("longTest", true, 9000000000000000000L),
                9000000000000000000L);
    }

    @Test
    public void negativeLongTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsLong(RandomStringUtils.secure().next(12)), -1L);
    }

    @Test
    public void negativeLongTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsLong(RandomStringUtils.secure().next(12), true), -1L);
    }

    @Test
    public void negativeLongTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsLong(RandomStringUtils.secure().next(12), true, 9000000000000000000L),
                9000000000000000000L);
    }

    @Test
    public void positiveFloatTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsFloat("floatTest"), (float) 4.8);
    }

    @Test
    public void positiveFloatTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsFloat("floatTest", true), (float) 4.8);
    }

    @Test
    public void positiveFloatTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsFloat("floatTest", true, (float) 4.8), (float) 4.8);
    }

    @Test
    public void negativeFloatTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsFloat(RandomStringUtils.secure().next(12)), (float) -1.0);
    }

    @Test
    public void negativeFloatTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsFloat(RandomStringUtils.secure().next(12), true), (float) -1.0);
    }

    @Test
    public void negativeFloatTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsFloat(RandomStringUtils.secure().next(12), true, (float) 4.8), (float) 4.8);
    }

    @Test
    public void positiveDoubleTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsDouble("doubleTest"), 24.75);
    }

    @Test
    public void positiveDoubleTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsDouble("doubleTest", true), 24.75);
    }

    @Test
    public void positiveDoubleTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsDouble("doubleTest", true, 24.75), 24.75);
    }

    @Test
    public void negativeDoubleTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsDouble(RandomStringUtils.secure().next(12)), -1.0);
    }

    @Test
    public void negativeDoubleTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsDouble(RandomStringUtils.secure().next(12), true), -1.0);
    }

    @Test
    public void negativeDoubleTestVariantThree() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsDouble(RandomStringUtils.secure().next(12), true, 24.75), 24.75);
    }

    @Test
    public void positiveStringTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsString("stringTest"), "The Test value of this property!!111");
    }

    @Test
    public void positiveStringTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsString("stringTest", true), "The Test value of this property!!111");
    }

    @Test
    public void positiveStringTestVariantThree() {
        CustomAssertions.assertEquals(
                TestPropertiesHelper.getPropertyAsString("stringTest", true, "The Test value of this property!!111"),
                "The Test value of this property!!111");
    }

    @Test
    public void negativeStringTestVariantOne() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsString(RandomStringUtils.secure().next(12)), "");
    }

    @Test
    public void negativeStringTestVariantTwo() {
        CustomAssertions.assertEquals(TestPropertiesHelper.getPropertyAsString(RandomStringUtils.secure().next(12), true), "");
    }

    @Test
    public void negativeStringTestVariantThree() {
        CustomAssertions.assertEquals(
                TestPropertiesHelper.getPropertyAsString(RandomStringUtils.secure().next(12), true, "The Test value of this property!!111"),
                "The Test value of this property!!111");
    }
}
