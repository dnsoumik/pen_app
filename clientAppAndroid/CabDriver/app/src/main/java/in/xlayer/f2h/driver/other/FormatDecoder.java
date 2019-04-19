package in.xlayer.f2h.driver.other;

import java.text.DecimalFormat;

public class FormatDecoder {

    private static DecimalFormat df2 = new DecimalFormat(".##");
    private static DecimalFormat df3 = new DecimalFormat(".###");

    public static double twoDecimalPoint(double value) {
        return Double.parseDouble(df2.format(value));
    }

    public static double threeDecimalPoint(double value) {
        return Double.parseDouble(df3.format(value));
    }

}
