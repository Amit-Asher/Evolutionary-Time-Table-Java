package EvolutionModel.Operations.Selection;


import EvolutionModel.Components.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ElitismMachine {

    public static <T extends Solution> List<T> GetElite(Map<T, Double> solution2Map, int elitismCount) {
        if(elitismCount == 0) return new ArrayList<>();

        int dictSize = solution2Map.size();
        Set<Map.Entry<T, Double>> entries = solution2Map.entrySet();
        List<T> elite = entries.stream().
                sorted((o1, o2) -> Double.compare(o2.getValue(),o1.getValue())).
                limit(elitismCount).
                map(Map.Entry::getKey).
                collect(Collectors.toList());
        return elite;
    }
}