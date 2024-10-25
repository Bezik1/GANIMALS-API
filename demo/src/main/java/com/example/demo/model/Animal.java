package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Size;

//
// Syntax of Genetic Code:
//
// * First single chart is responsible for gender of the animal (0, 1);
// * Next single chart is responsible for if the animal is carnivour or not (0, 1);
// * Control chart (x);
// * Next eight charts are responsible for the base color of the animal (00000000, ffffffff);
// * Next eight charts are responsible for the eye color of the animal (00000000, ffffffff);
// * Next eight charts are responsible for the special color of the animal (00000000, ffffffff);
// * Next chart is responsible for the enviroment in which th animal lives (0, 4): default, jungle, dessert, ice;
// * Next chart is responsible for the claws (0, 1);
// * Next chart is responsible for the spikes (0, 1);
//
// Additionaly there is a chance for the mutation of the gen, so not necessary
// the child will be similar to the parents.
//
//
//
// Example: 01xff00ff00ff00ff00ff0000ff401

class Color {
    String hex;

    int r;
    int g;
    int b;
    int a;

    public Color(String hexString) {
        if (hexString.length() != 8) throw new IllegalArgumentException("Hex string must be 8 characters long. But it is: " + hexString.length());

        int r = Integer.parseInt(hexString.substring(0, 2), 16);
        int g = Integer.parseInt(hexString.substring(2, 4), 16);
        int b = Integer.parseInt(hexString.substring(4, 6), 16);
        int a = Integer.parseInt(hexString.substring(6, 8), 16);

        this.hex = hexString;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public static Color combineColors(Color firstColor, Color secondColor) {
        String r = Integer.toHexString((int) ((firstColor.r + secondColor.r) / 2));
        String g = Integer.toHexString((int) ((firstColor.g + secondColor.g) / 2));
        String b = Integer.toHexString((int) ((firstColor.b + secondColor.b) / 2));
        String a = Integer.toHexString((int) ((firstColor.a + secondColor.a) / 2));

        return new Color(r + g + b + a);
    }
}

class Genom {
    boolean gender;
    boolean isCarnivour;

    Color baseColor;
    Color eyeColor;
    Color specialColor;

    int enviroment;
    boolean hasClaws;
    boolean hasSpikes;

    public Genom(boolean gender, boolean isCarnivour, Color baseColor,
                Color eyeColor, Color speciaColor, int enviroment,
                boolean hasClaws, boolean hasSpikes) {
        this.gender = gender;
        this.isCarnivour = isCarnivour;

        this.baseColor = baseColor;
        this.eyeColor = eyeColor;
        this.specialColor = speciaColor;

        this.enviroment = enviroment;

        this.hasClaws = hasClaws;
        this.hasSpikes = hasSpikes;
    }

    public String toHexString() {
        String genderString = (this.gender) ? "1" : "0";
        String isCarnivourString = (this.isCarnivour) ? "1" : "0";

        String hasClawsString = (this.hasClaws) ? "1" : "0";
        String hasSpikesString = (this.hasSpikes) ? "1" : "0";

        return genderString + isCarnivourString + "x" + this.baseColor.hex +
        this.eyeColor.hex + this.specialColor.hex + Integer.toString(this.enviroment) +
        hasClawsString + hasSpikesString;
    }
}

@Document(collection = "Animals")
public class Animal {
    @Id
    private String id;

    @Size(max=30)
    private String owner;

    @Size(min=3, max=20)
    private String name;

    @Size(min=31, max=31)
    private String genCode;

    public Animal() {}

    public Animal(String owner, String name, String genCode) {
        this.owner = owner;
        this.name = name;
        this.genCode = genCode;
    }

    private Genom analyzeParentString(String parentString) {
        boolean gender = parentString.charAt(0) == '1';
        boolean isCarnivour = parentString.charAt(1) == '1';

        Color baseColor = new Color(parentString.substring(3, 11));
        Color eyeColor = new Color(parentString.substring(11, 19));
        Color specialColor = new Color(parentString.substring(19, 27));

        int enviroment = Integer.parseInt(parentString.substring(27, 28));

        boolean hasClaws = parentString.charAt(28) == '1';
        boolean hasSpikes = parentString.charAt(29) == '1';

        return new Genom(gender, isCarnivour, baseColor, eyeColor, specialColor, enviroment, hasClaws, hasSpikes);
    }

    private Genom combineGenoms(Genom firstParentGenom, Genom secondParentGenCode) {
        boolean gender = ((int)(Math.random()*2) < 1);
        boolean isCarnivour = ((int)(Math.random()*2) < 1);

        Color combinedBaseColor = Color.combineColors(firstParentGenom.baseColor, secondParentGenCode.baseColor);
        Color combinedEyeColor = Color.combineColors(firstParentGenom.eyeColor, secondParentGenCode.eyeColor);
        Color combinedSpecialColor = Color.combineColors(firstParentGenom.specialColor, secondParentGenCode.specialColor);

        int enviroment = (Math.random() >= 0.5) ? firstParentGenom.enviroment : secondParentGenCode.enviroment;

        boolean hasClaws = (Math.random() >= 0.5) ? firstParentGenom.hasClaws : secondParentGenCode.hasClaws;
        boolean hasSpikes = (Math.random() >= 0.5) ? firstParentGenom.hasSpikes : secondParentGenCode.hasSpikes;

        double mutationEpsilon = Math.random();
        double mutationThreshold = 0.05;

        if(mutationEpsilon<=mutationThreshold) {
            combinedBaseColor = Color.combineColors(combinedBaseColor, new Color("ffffffff"));
            combinedEyeColor = Color.combineColors(combinedEyeColor, new Color("ff0000ff"));
            combinedSpecialColor = Color.combineColors(combinedSpecialColor, new Color("ffffffff"));
        }

        Genom combinedGenom =  new Genom(gender, isCarnivour, combinedBaseColor, combinedEyeColor, combinedSpecialColor, enviroment, hasClaws, hasSpikes);
        return combinedGenom;
    }

    public Animal breed(String owner, String name, String secondParentGenCode) {
        Genom firstParentGenom = analyzeParentString(this.genCode);
        Genom secondParentGenom = analyzeParentString(secondParentGenCode);

        if(firstParentGenom.gender == secondParentGenom.gender) throw new IllegalArgumentException("Genders can't be the same!");

        Genom combinedGenom = combineGenoms(firstParentGenom, secondParentGenom);
        String combinedStringifiedGenom = combinedGenom.toHexString();

        return new Animal(owner, name, combinedStringifiedGenom);
    }

    public static String generateWildAnimalGeneticCode() {
        String gender = String.format("%X", (int) (Math.random() * 2));
        String carnivour = String.format("%X", (int) (Math.random() * 2));

        String baseColorR = String.format("%02X", (int) (Math.random() * 256));
        String baseColorG = String.format("%02X", (int) (Math.random() * 256));
        String baseColorB = String.format("%02X", (int) (Math.random() * 256));
        String baseColorA = String.format("%02X", (int) (255));

        String eyeColorR = String.format("%02X", (int) (Math.random() * 256));
        String eyeColorG = String.format("%02X", (int) (Math.random() * 256));
        String eyeColorB = String.format("%02X", (int) (Math.random() * 256));
        String eyeColorA = String.format("%02X", (int) (255));

        String specialColorR = String.format("%02X", (int) (Math.random() * 256));
        String specialColorG = String.format("%02X", (int) (Math.random() * 256));
        String specialColorB = String.format("%02X", (int) (Math.random() * 256));
        String specialColorA = String.format("%02X", (int) (255));

        String environment = String.format("%X", (int) (Math.random() * 4));

        String claws = String.format("%X", (int) (Math.random() * 2));
        String spikes = String.format("%X", (int) (Math.random() * 2));

        return gender + carnivour + "x" +
                baseColorR + baseColorG + baseColorB + baseColorA +
                eyeColorR + eyeColorG + eyeColorB + eyeColorA +
                specialColorR + specialColorG + specialColorB + specialColorA +
                environment + claws + spikes;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public String getGenome() {
        return this.genCode;
    }
}