package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.demo.types.Animals.Color;
import com.example.demo.types.Animals.Genom;

import jakarta.validation.constraints.Size;

/**
 * Ganimal is an virtual representation of animal with properites like
 * base color, eye color, special color, etc. The data about its characteristics
 * is stored in hexadecimal gen code. Ganimals from the backend point of view are
 * just the collection of objects paired with some user.
 */
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

    public Animal() {
        this.genCode = generateWildAnimalGeneticCode();
    }

    public Animal(String owner, String name, String genCode) {
        this.owner = owner;
        this.name = name;
        this.genCode = genCode;
    }

    /**
     * Analyze animal genCode as string and return an
     * object with useful access to genome properties
     * @param parentStirng
     * @return Genom
     */
    public static Genom analyzeParentString(String parentString) {
        boolean gender = parentString.charAt(0) == '1';

        String carnivourAlleles = parentString.substring(1, 3);
        boolean isCarnivour = parentString.charAt(4) == '1';

        String baseColorAlleles = parentString.substring(5, 7);
        Color baseColor = new Color(parentString.substring(7, 15));

        String eyeColorAlleles = parentString.substring(15, 17);
        Color eyeColor = new Color(parentString.substring(17, 25));

        String specialColorAlleles = parentString.substring(25, 27);
        Color specialColor = new Color(parentString.substring(27, 35));

        String environmentAlleles = parentString.substring(35, 37);
        int environment = Integer.parseInt(parentString.substring(37, 38));

        String clawsAlleles = parentString.substring(38, 40);
        boolean hasClaws = parentString.charAt(40) == '1';

        String spikesAlleles = parentString.substring(41, 43);
        boolean hasSpikes = parentString.charAt(43) == '1';

        return new Genom(gender, carnivourAlleles, isCarnivour, baseColorAlleles, baseColor,
                            eyeColorAlleles, eyeColor, specialColorAlleles, specialColor,
                            environmentAlleles, environment, clawsAlleles, hasClaws, spikesAlleles, hasSpikes);
    }


    /**
     * Combine genoms of two ganimals with respect to allels,
     * in such a way that most of the properties are from the
     * parent with dominant allel
     *
     * @param firstParentGenom
     * @param secondParentGenom
     * @return Genom
     */
    private Genom combineGenoms(Genom firstParentGenom, Genom secondParentGenom) {
        boolean gender = Math.random() >= 0.5;

        String carnivourAlleles = getCombinedAlleles(firstParentGenom.carnivourAlleles, secondParentGenom.carnivourAlleles);
        boolean isCarnivour = Math.random() >= 0.5;

        String baseColorAlleles = getCombinedAlleles(firstParentGenom.baseColorAlleles, secondParentGenom.baseColorAlleles);
        Color combinedBaseColor = Color.combineColors(firstParentGenom.baseColor, secondParentGenom.baseColor, baseColorAlleles);

        String eyeColorAlleles = getCombinedAlleles(firstParentGenom.eyeColorAlleles, secondParentGenom.eyeColorAlleles);
        Color combinedEyeColor = Color.combineColors(firstParentGenom.eyeColor, secondParentGenom.eyeColor, eyeColorAlleles);

        String specialColorAlleles = getCombinedAlleles(firstParentGenom.specialColorAlleles, secondParentGenom.specialColorAlleles);
        Color combinedSpecialColor = Color.combineColors(firstParentGenom.specialColor, secondParentGenom.specialColor, specialColorAlleles);

        String environmentAlleles = getCombinedAlleles(firstParentGenom.environmentAlleles, secondParentGenom.environmentAlleles);
        int environment = Math.random() >= 0.5 ? firstParentGenom.environment : secondParentGenom.environment;

        String clawsAlleles = getCombinedAlleles(firstParentGenom.clawsAlleles, secondParentGenom.clawsAlleles);
        boolean hasClaws = Math.random() >= 0.5;

        String spikesAlleles = getCombinedAlleles(firstParentGenom.spikesAlleles, secondParentGenom.spikesAlleles);
        boolean hasSpikes = Math.random() >= 0.5;

        return new Genom(gender, carnivourAlleles, isCarnivour, baseColorAlleles, combinedBaseColor,
                        eyeColorAlleles, combinedEyeColor, specialColorAlleles, combinedSpecialColor,
                        environmentAlleles, environment, clawsAlleles, hasClaws, spikesAlleles, hasSpikes);
    }

    /**
     * Returns all possible combinations of two pairs of allels.
     * 
     * @param alleles1
     * @param alleles2
     * @return List<String>
     */
    private List<String> getPossibleAllelsCombintations(String alleles1, String alleles2) {
        List<String> possibleAllelsCombinations = new ArrayList<String>();

        for(int i=0; i<alleles1.length(); i++) {
            for(int j=0; j<alleles2.length(); j++) {
                String combination = "" + alleles1.charAt(i) + alleles2.charAt(j);
                possibleAllelsCombinations.add(combination);
            }
        }
        return possibleAllelsCombinations;
    }


    /**
     * Use the function getPossibleAllelsCombintations and returns
     * one of the combination with random probability
     * 
     * @param alleles1
     * @param alleles2
     * @return
     */
    private String getCombinedAlleles(String alleles1, String alleles2) {
        List<String> possibleAllelsCombinations = getPossibleAllelsCombintations(alleles1, alleles2);
        int index = (int) (Math.random() * (float) (possibleAllelsCombinations.size()));

        return possibleAllelsCombinations.get(index);
    }

    /**
     * Create a new ganimal based on the gen code of current ganimal and
     * some other, of whose the genCode is passed down to the function
     * 
     * @param owner
     * @param name
     * @param secondParentGenCode
     * @return
     */
    public Animal breed(String owner, String name, String secondParentGenCode) {
        Genom firstParentGenom = analyzeParentString(this.genCode);
        Genom secondParentGenom = analyzeParentString(secondParentGenCode);

        if (firstParentGenom.gender == secondParentGenom.gender) {
            throw new IllegalArgumentException("Both parents are the same gender!");
        }

        Genom combinedGenom = combineGenoms(firstParentGenom, secondParentGenom);
        String combinedStringifiedGenom = combinedGenom.toHexString();

        return new Animal(owner, name, combinedStringifiedGenom);
    }

    /**
     * Generate Wild Ganimal Genome in such a way that:
     *  first bit is gender 0/1
     *  next bit is carnivour allels
     *  next bit is if the ganimal is carnivour
     *  next bit is baseColor allels
     *  next eight bits are base color representation
     *  next bit is eye color allels
     *  next eight bits are eye color representation
     *  next bit is special color allels
     *  next eight bit are special color representation
     *  next bit is environmental allel
     *  next bit represents enviornemt
     *  next bit is claws allels
     *  next bit is if ganimal has claws
     *  next bit is spikes allels
     *  last bit is if ganimal has spikes
     *
     * @return String
     */
    public static String generateWildAnimalGeneticCode() {
        String gender = String.format("%X", (int) (Math.random() * 2));

        String carnivourAlleles = Genom.getRandomAlleles();
        String carnivour = String.format("%X", (int) (Math.random() * 2));

        String baseColorAlleles = Genom.getRandomAlleles();
        String baseColorR = Color.formatRandomColorChannel();
        String baseColorG = Color.formatRandomColorChannel();
        String baseColorB = Color.formatRandomColorChannel();
        String baseColorA = String.format("%02X", 255);

        String eyeColorAlleles = Genom.getRandomAlleles();
        String eyeColorR = Color.formatRandomColorChannel();
        String eyeColorG = Color.formatRandomColorChannel();
        String eyeColorB = Color.formatRandomColorChannel();
        String eyeColorA = String.format("%02X", 255);

        String specialColorAlleles = Genom.getRandomAlleles();
        String specialColorR = Color.formatRandomColorChannel();
        String specialColorG = Color.formatRandomColorChannel();
        String specialColorB = Color.formatRandomColorChannel();
        String specialColorA = String.format("%02X", 255);

        String environmentAlleles = Genom.getRandomAlleles();
        String environment = String.format("%X", (int) (Math.random() * 4));

        String clawsAlleles = Genom.getRandomAlleles();
        String claws = String.format("%X", (int) (Math.random() * 2));

        String spikesAlleles = Genom.getRandomAlleles();
        String spikes = String.format("%X", (int) (Math.random() * 2));

        return gender + carnivourAlleles + carnivour + "x" +
                baseColorAlleles + baseColorR + baseColorG + baseColorB + baseColorA +
                eyeColorAlleles + eyeColorR + eyeColorG + eyeColorB + eyeColorA +
                specialColorAlleles + specialColorR + specialColorG + specialColorB + specialColorA +
                environmentAlleles + environment +
                clawsAlleles + claws +
                spikesAlleles + spikes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getOwner() { return this.owner; }
    public String getName() { return this.name; }
    public String getGenome() { return this.genCode; }
}
