package com.example.demo.types.Animals;

public class Genom {
    public boolean gender;
    public String carnivourAlleles;
    public boolean isCarnivour;

    public String baseColorAlleles;
    public Color baseColor;

    public String eyeColorAlleles;
    public Color eyeColor;

    public String specialColorAlleles;
    public Color specialColor;

    public String environmentAlleles;
    public int environment;

    public String clawsAlleles;
    public boolean hasClaws;

    public String spikesAlleles;
    public boolean hasSpikes;

    public Genom(boolean gender, String carnivourAlleles, boolean isCarnivour, String baseColorAlleles, Color baseColor,
                String eyeColorAlleles, Color eyeColor, String specialColorAlleles, Color specialColor,
                String environmentAlleles, int environment, String clawsAlleles, boolean hasClaws,
                String spikesAlleles, boolean hasSpikes) {
        this.gender = gender;
        this.carnivourAlleles = carnivourAlleles;
        this.isCarnivour = isCarnivour;

        this.baseColorAlleles = baseColorAlleles;
        this.baseColor = baseColor;

        this.eyeColorAlleles = eyeColorAlleles;
        this.eyeColor = eyeColor;

        this.specialColorAlleles = specialColorAlleles;
        this.specialColor = specialColor;

        this.environmentAlleles = environmentAlleles;
        this.environment = environment;

        this.clawsAlleles = clawsAlleles;
        this.hasClaws = hasClaws;

        this.spikesAlleles = spikesAlleles;
        this.hasSpikes = hasSpikes;
    }

    public static String getRandomAlleles() {
        return (Math.random() >= 0.5 ? "1" : "0") + (Math.random() >= 0.5 ? "1" : "0");
    }

    public String toHexString() {
        String genderString = (this.gender) ? "1" : "0";
        String isCarnivourString = this.carnivourAlleles + (this.isCarnivour ? "1" : "0");

        String baseColorString = this.baseColorAlleles + this.baseColor.hex;
        String eyeColorString = this.eyeColorAlleles + this.eyeColor.hex;
        String specialColorString = this.specialColorAlleles + this.specialColor.hex;

        String environmentString = this.environmentAlleles + Integer.toString(this.environment);
        String clawsString = this.clawsAlleles + (this.hasClaws ? "1" : "0");
        String spikesString = this.spikesAlleles + (this.hasSpikes ? "1" : "0");

        return genderString + isCarnivourString + "x" +
                baseColorString + eyeColorString + specialColorString +
                environmentString + clawsString + spikesString;
    }
}