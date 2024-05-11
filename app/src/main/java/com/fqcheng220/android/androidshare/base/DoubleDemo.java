package com.fqcheng220.android.androidshare.base;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description:
 * {@link Double}和double、{@link BigDecimal}运算的坑
 * 搭配{@link DecimalFormat}格式化显示
 * @date 2024/5/11 10:22
 */
public class DoubleDemo {
   public static void main(String[] args) {
      DoubleDemo doubleDemo = new DoubleDemo();
      doubleDemo.testBase();
      doubleDemo.testDecimalFormat();
      doubleDemo.testBigDecimal();
   }

   private void testDecimalFormat() {
      System.out.println("testDecimalFormat------");
      DecimalFormat decimalFormat1 = new DecimalFormat("0.00%");
      decimalFormat1.setRoundingMode(RoundingMode.HALF_UP);
      double d1 = 1.050725;
      System.out.println(decimalFormat1.format(d1));

      DecimalFormat decimalFormat2 = new DecimalFormat("0.00#");
      decimalFormat2.setRoundingMode(RoundingMode.HALF_UP);
      double d2 = 1.050725;
      System.out.println(decimalFormat2.format(d2 * 100));

      DecimalFormat decimalFormat3 = new java.text.DecimalFormat("0.00000");
      decimalFormat3.setRoundingMode(RoundingMode.HALF_UP);
      System.out.println(decimalFormat3.format(1.050725));

      DecimalFormat decimalFormat4 = new java.text.DecimalFormat("0.00000");
      decimalFormat4.setRoundingMode(RoundingMode.HALF_UP);
      System.out.println(decimalFormat4.format(Double.parseDouble("1.050725")));

      DecimalFormat decimalFormat5 = new java.text.DecimalFormat("0.00000");//BigDecimal错误四舍五入
      decimalFormat5.setRoundingMode(RoundingMode.HALF_UP);
      System.out.println(decimalFormat5.format(new BigDecimal(1.050725)));

      DecimalFormat decimalFormat6 = new java.text.DecimalFormat("0.00000");//BigDecimal正确四舍五入
      decimalFormat6.setRoundingMode(RoundingMode.HALF_UP);
      System.out.println(decimalFormat6.format(new BigDecimal("1.050725")));
   }

   private void testBase() {
      System.out.println("testBase------");
      System.out.println(0.05 + 0.01);
      System.out.println(Double.parseDouble("0.05") + Double.parseDouble("0.01"));
      System.out.println(1.0 - 0.42);
      System.out.println(Double.parseDouble("1.0") - Double.parseDouble("0.42"));
      System.out.println(4.015 * 100);
      System.out.println(Double.parseDouble("4.015") * Double.parseDouble("100"));
      System.out.println(123.3 / 100);
      System.out.println(Double.parseDouble("123.3") / Double.parseDouble("100"));
   }

   private void testBigDecimal() {
      System.out.println("testBigDecimal------");
      System.out.println(new BigDecimal("123.3"));
      System.out.println(new BigDecimal(123.3));
      System.out.println(new BigDecimal("0.05").add(new BigDecimal("0.01")));
      System.out.println(new BigDecimal(0.05).add(new BigDecimal(0.01)));
      System.out.println(new BigDecimal("1.0").subtract(new BigDecimal("0.42")));
      System.out.println(new BigDecimal(1.0).subtract(new BigDecimal(0.42)));
      System.out.println(new BigDecimal("4.015").multiply(new BigDecimal("100")));
      System.out.println(new BigDecimal(4.015).multiply(new BigDecimal(100)));
      System.out.println(new BigDecimal("123.3").divide(new BigDecimal("100")));
      System.out.println(new BigDecimal(123.3).divide(new BigDecimal(100)));
   }
}
