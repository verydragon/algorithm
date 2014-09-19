package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author harry
 *
 * Created on: Aug 26, 2014
 */

public class GeneticAlgorithm {
    
    private int generationCount;
    private int populationSize;
    private int chromosomeSize;
    private double mutationRate;
    private List<String> population;
    private Map<String, Double> evaluationSheet;
    
    public GeneticAlgorithm() {
        generationCount = 100;
        populationSize = 100;
        chromosomeSize = 25;
        mutationRate = 0.02; //2 in 100 will mutate
        population = new ArrayList<String>();
    }

    public static void main(String[] args){
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.demo();
    }
    
    public void demo(){
        
        System.out.println("Formula: \nf(x) = x*cos(10*pi*x) * sin(x) \n[0, 3)");
        System.out.println("Question: \nWhat is the value x to maximize f(x)?\n\n");
        
        String cBest = "";
        double xBest = 0;
        double fBest = 0;
        
        initialize();
        for(int i = 0; i < generationCount; i++){
            List<String> kids = crossover();
            mutate(kids);
            select(kids);
        }
        
        Map<String, Double> kidsRanked = sortEvaluation(evaluationSheet);
        int iKidCount = 0;
        for(String c : kidsRanked.keySet()){
            System.out.println(c +" : "+kidsRanked.get(c) + " x=["+xValueOfChromosome(c)+"]");
            iKidCount++;
            if(iKidCount == populationSize){
                xBest = xValueOfChromosome(c);
                fBest = kidsRanked.get(c);
                cBest = c;
            }
        }
        
        System.out.println("\n\n[Optimization Result]");
        System.out.println(cBest);
        System.out.println("   x: "+xBest);
        System.out.println("f(x): "+fBest);
        
    }
    
    private void initialize(){
        for(int i = 0; i < populationSize; i++){
            population.add(randomChromosome());
        }
    }
    
    private double evaluate(String chromosome){
        double xInDecimal = xValueOfChromosome(chromosome);
        double fValueOfX = xInDecimal*Math.cos(10*3.1415926*xInDecimal) * Math.sin(xInDecimal);
        return fValueOfX;
    }
    
    private List<String> crossover(){
        List<String> offsprings = new ArrayList<String>();
        for(int i = 0; i < populationSize-1; i=i+2){
            String c1 = population.get(i).substring(0, 6) + population.get(i+1).substring(6);
            String c2 = population.get(i+1).substring(0, 6) + population.get(i).substring(6);
            offsprings.add(c1);
            offsprings.add(c2);
        }
        for(int i = 0; i   < populationSize - offsprings.size(); i++){
            offsprings.add(randomChromosome());
        }
        return offsprings;
    }
    
    private void mutate(List<String> offsprings){
        for(int i = 0; i < populationSize; i++){
            if(Math.random() * 0.1 < mutationRate){
                StringBuilder mutated = new StringBuilder(offsprings.get(i));
                int mutatingIndex = (int) Math.ceil(Math.random()*(chromosomeSize-1));
                if(mutated.charAt(mutatingIndex) == '0'){
                    mutated.replace(mutatingIndex, mutatingIndex+1, "1");
                } else {
                    mutated.replace(mutatingIndex, mutatingIndex+1, "0");
                }
                offsprings.set(i, mutated.toString());
            }
        }
    }
    
    private void select(List<String> offsprings){
        evaluationSheet = new LinkedHashMap<String, Double>();
        Map<String, Double> tests = new LinkedHashMap<String, Double>();
        for(int i = 0; i < populationSize; i++){
            tests.put(population.get(i), evaluate(population.get(i)));
            tests.put(offsprings.get(i), evaluate(offsprings.get(i)));
        }
        tests = sortEvaluation(tests);
        List<String> family = new LinkedList<String>(tests.keySet());
        List<String> survivors = new ArrayList<String>();
        for(int i=(family.size()-1); i>=(family.size() - populationSize); i--){
            survivors.add(family.get(i));
            evaluationSheet.put(family.get(i), tests.get(family.get(i)));
        }
        population = survivors;
    }
    
    
    /*
     *  Utility functions 
     */
    
    private String randomChromosome(){
        StringBuilder chromosome = new StringBuilder();
        for(int j = 0; j < chromosomeSize; j++){
            if(Math.random() < 0.5){
                chromosome.append("0");
            } else {
                chromosome.append("1");
            }
        }
        return chromosome.toString();
    }
    
    private double xValueOfChromosome(String chromosome){
        return Integer.parseInt(chromosome, 2) * (3/(Math.pow(2, chromosomeSize) - 1));
    }
    
    private static Map<String, Double> sortEvaluation(Map<String, Double> unsortMap){
        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Double>>(){
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2){
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    
}
