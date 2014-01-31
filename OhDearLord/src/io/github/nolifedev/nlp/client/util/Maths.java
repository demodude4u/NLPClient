package io.github.nolifedev.nlp.client.util;


import java.awt.Color;

public final class Maths {
	private Maths() {
	}

	public static Color interp(Color color1, Color color2, float f) {
		return new Color(Maths.interp(color1.getRed(), color2.getRed(), f), //
				Maths.interp(color1.getGreen(), color2.getGreen(), f), //
				Maths.interp(color1.getBlue(), color2.getBlue(), f), //
				Maths.interp(color1.getAlpha(), color2.getAlpha(), f));
	}

	public static float interp(float num1, float num2, float f) {
		return (num1 + (num2 - num1) * f);
	}

	public static int interp(int num1, int num2, float f) {
		return (int) (num1 + (num2 - num1) * f);
	}
}
