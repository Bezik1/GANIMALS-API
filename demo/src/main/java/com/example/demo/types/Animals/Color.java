package com.example.demo.types.Animals;

public class Color {
    public String hex;
    public int r, g, b, a;

    public Color(String hexString) {
        if (hexString.length() != 8) throw new IllegalArgumentException("Hex string must be 8 characters long. But it is: " + hexString.length());

        this.r = Integer.parseInt(hexString.substring(0, 2), 16);
        this.g = Integer.parseInt(hexString.substring(2, 4), 16);
        this.b = Integer.parseInt(hexString.substring(4, 6), 16);
        this.a = Integer.parseInt(hexString.substring(6, 8), 16);
        this.hex = hexString;
    }

    public static Color combineColors(Color firstColor, Color secondColor, String allels) {
        int firstColorRatio = 0;
        int secondColorRatio = 0;

        switch(allels) {
            case "10":
                firstColorRatio = 4/3;
                secondColorRatio = 2/3;
                break;
            case "01":
                firstColorRatio = 2/3;
                secondColorRatio = 4/3;
                break;
            case "11":
                firstColorRatio = 1;
                secondColorRatio = 0;
                break;
            case "00":
                firstColorRatio = 0;
                secondColorRatio = 1;
                break;
        }

        String r = String.format("%02x", ((Math.min(255, firstColor.r*firstColorRatio) + Math.min(255, secondColor.r*secondColorRatio)) / 2));
        String g = String.format("%02x", ((Math.min(255, firstColor.g*firstColorRatio) + Math.min(255, secondColor.g*secondColorRatio)) / 2));
        String b = String.format("%02x", ((Math.min(255, firstColor.b*firstColorRatio) + Math.min(255, secondColor.b*secondColorRatio)) / 2));
        String a = String.format("%02x", (255));

        return new Color(r + g + b + a);
    }

    public static String formatRandomColorChannel() {
        return String.format("%02X", (int) (Math.random() * 256));
    }
}