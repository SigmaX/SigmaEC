package SigmaEC.experiment;

import SigmaEC.represent.Phenotype;

/**
 * Parameters for specifying a vanilla genetic algorithm.
 * 
 * @author Eric 'Siggy' Scott
 */
public class GAParameters {
    // Representation parameters
    public final int numBits;
    
    // Evolutionary parameters
    public final int populationSize;
    public final int numGenerations;
    public final double bitMutationRate;
    public final int numRuns;
    public final int tournamentSize;
    
    public final boolean isDynamic;
    
    private GAParameters(final Builder builder) {
        this.numBits = builder.numBits;
        this.populationSize = builder.populationSize;
        this.numGenerations = builder.numGenerations;
        this.bitMutationRate = builder.bitMutationRate;
        this.numRuns = builder.numRuns;
        this.tournamentSize = builder.tournamentSize;
        this.isDynamic = builder.isDynamic;
    }
    
    // Copy constructor
    private GAParameters(final GAParameters ref) {
        this.numBits = ref.numBits;
        this.populationSize = ref.populationSize;
        this.numGenerations = ref.numGenerations;
        this.bitMutationRate = ref.bitMutationRate;
        this.numRuns = ref.numRuns;
        this.tournamentSize = ref.tournamentSize;
        this.isDynamic = ref.isDynamic;
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
        private int numBits = 3;
        private int populationSize = 50;
        private int numGenerations = 1000;
        private double bitMutationRate = 0.03;
        private int numRuns = 30;
        private int tournamentSize = 2;
        private boolean isDynamic = false;
        
        public Builder() {
        }
        
        /** Create a modifiable copy of another PleiotropicParameters. */
        public Builder(final GAParameters ref) {
            this.numBits = ref.numBits;
            this.populationSize = ref.populationSize;
            this.numGenerations = ref.numGenerations;
            this.bitMutationRate = ref.bitMutationRate;
            this.numRuns = ref.numRuns;
            this.tournamentSize = ref.tournamentSize;
            this.isDynamic = ref.isDynamic;
        }
        
        public GAParameters build() {
            return new GAParameters(this);
        }
        
        public Builder numBits(final int numBits) {
            assert(numBits > 0);
            this.numBits = numBits;
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
        
        public Builder bitMutationRate(final double bitMutationRate) {
            assert(bitMutationRate >= 0.0);
            assert(bitMutationRate <= 1.0);
            this.bitMutationRate = bitMutationRate;
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
        
        public Builder isDynamic(final boolean isDynamic) {
            this.isDynamic = isDynamic;
            return this;
        }
    }
}
