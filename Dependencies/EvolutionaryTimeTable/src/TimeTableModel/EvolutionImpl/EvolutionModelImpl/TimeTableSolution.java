package TimeTableModel.EvolutionImpl.EvolutionModelImpl;

import EvolutionModel.Components.Solution;

import java.util.ArrayList;
import java.util.List;

public class TimeTableSolution implements Solution {
    List<Gen> gens = new ArrayList<>();

    public TimeTableSolution(List<Gen> gens){
        this.gens = gens;
    }

    public List<Gen> getGens(){
        return this.gens;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }

        if (obj == this){
            return true;
        }

        if (!(obj instanceof Gen)) {
            return false;
        }

        final TimeTableSolution other = (TimeTableSolution) obj;
        List<Gen> otherGen = other.gens;
        if( this.gens.size() != otherGen.size())
            return false;
        for(Gen gen : this.gens){
            if(!otherGen.contains(gen)){
                return false;
            }
        }
        return true;
    }

}
