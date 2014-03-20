package SigmaEC.experiment;

import SigmaEC.represent.Phenotype;

/**
 * Parameters for specifying a vanilla genetic algorithm.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGAParameters {
    // Representation parameters
    public final int numDimensions;
    public final double initialBound;
    
    // Evolutionary parameters
    public final int populationSize;
    public final int numGenerations;
    public final double mutationRate;
    public final double mutationStd;
    public final int numRuns;
    public final int tournamentSize;
    
    private DoubleGAParameters(final Builder builder) {
        this.numDimensions = builder.numDimensions;
        this.initialBound = builder.initialBound;
        this.populationSize = builder.populationSize;
        this.numGenerations = builder.numGenerations;
        this.mutationRate = builder.mutationRate;
        this.mutationStd = builder.mutationStd;
        this.numRuns = builder.numRuns;
        this.tournamentSize = builder.tournamentSize;
    }
    
    // Copy constructor
    private DoubleGAParameters(final DoubleGAParameters ref) {
        this.numDimensions = ref.numDimensions;
        this.initialBound = ref.initialBound;
        this.populationSize = ref.populationSize;
        this.numGenerations = ref.numGenerations;
        this.mutationRate = ref.mutationRate;
        this.mutationStd = ref.mutationStd;
        this.numRuns = ref.numRuns;
        this.tournamentSize = ref.tournamentSize;
    }
    
    /** The Builder pattern allows us to concisely create an immutable
     * PleiotropicParameters and set some non-default parameter values at
     * the same time.  For example, if we want to use  myObjective for
     * 10 runs of 500 generations each and a tournament size of 4, we'd run
     * 
     * (new GAParameters.Builder(myObjective))
     *          .numGenerations(500)
     *          .numRuns(10)
     *          .tournamentSize(4)
     *          .build();
     * 
     * The remaining parameters will assume their default values.
     */
    public static class Builder<P extends Phenotype> {
        // Parameters with default values
        private int numDimensions = 3;
        private double initialBound = 1.0;
        private int populationSize = 50;
        private int numGenerations = 1000;
        private double mutationRate = 0.03;
        private double mutationStd = 0.5;
        private int numRuns = 30;
        private int tournamentSize = 2;
        
        public Builder() {
        }
        
        /** Create a modifiable copy of another PleiotropicParameters. */
        public Builder(final DoubleGAParameters ref) {
            this.numDimensions = ref.numDimensions;
            this.initialBound = ref.initialBound;
            this.populationSize = ref.populationSize;
            this.numGenerations = ref.numGenerations;
            this.mutationRate = ref.mutationRate;
            this.mutationStd = ref.mutationStd;
            this.numRuns = ref.numRuns;
            this.tournamentSize = ref.tournamentSize;
        }
        
        public DoubleGAParameters build() {
            return new DoubleGAParameters(this);
        }
        
        public Builder numDimensions(final int numDimensions) {
            assert(numDimensions > 0);
            this.numDimensions = numDimensions;
            return this;
        }
        
        public Builder initialBound(final double initialBound) {
            assert(!Double.isNaN(initialBound));
            assert(!Double.isInfinite(initialBound));
            this.initialBound = initialBound;
            return this;
        }
        
        public Builder populationSize(final int populationSize) {
            assert(populationSize > 1);
            this.populationSize = populationSize;
            return this;
        }
        
        public Builder numGenerations(final int numGenerations) {
            assert(numGenerations > 0);
            this.numGenerations = numGenerations;
            return this;
        }
        
        public Builder mutationRate(final double mutationRate) {
            assert(mutationRate >= 0.0);
            assert(mutationRate <= 1.0);
            this.mutationRate = mutationRate;
            return this;
        }
        
        public Builder mutationStd(final double mutationStd) {
            assert(mutationStd > 0.0);
            this.mutationStd = mutationStd;
            return this;
        }
        
        public Builder numRuns(final int numRuns) {
            assert(numRuns >= 0);
            this.numRuns = numRuns;
            return this;
        }
        
        public Builder tournamentSize(final int tournamentSize) {
            assert(tournamentSize > 1);
            this.tournamentSize = tournamentSize;
            return this;
        }
    }
}
