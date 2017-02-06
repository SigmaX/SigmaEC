package SigmaEC.measure;

import SigmaEC.ContractObject;
import SigmaEC.evaluate.VectorFitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.select.VectorFitnessComparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A helper class that stores best-so-far information for a population of
 * individuals whose fitness in defined by VectorFitness objects.  The best-so-far
 * individual is recorded for each subpopulation with respect to each dimension
 * of VectorFitness.
 * 
 * @author Eric O. Scott
 */
class VectorBestSoFarTable<T extends Individual<VectorFitness>> extends ContractObject {
    private final FitnessComparator<T, VectorFitness> scalarFitnessComparator;
    private final VectorFitnessComparator<T, VectorFitness> vectorFitnessComparator;
    private final int numFitnessDimensions;
    private final List<T> scalarBestSoFar = new ArrayList<>(); // One element per subpopulation
    private final List<List<T>> bestSoFar = new ArrayList<>(); // One element per subpopulation
    private int lastBSFUpdate = -1;

    public VectorBestSoFarTable(final FitnessComparator<T, VectorFitness> scalarFitnessComparator, final VectorFitnessComparator<T, VectorFitness> vectorFitnessComparator, final int numFitnessdimensions) {
        assert(vectorFitnessComparator != null);
        assert(numFitnessdimensions >= 0);
        this.scalarFitnessComparator = scalarFitnessComparator;
        this.vectorFitnessComparator = vectorFitnessComparator;
        this.numFitnessDimensions = numFitnessdimensions;
    }
    
    public int getNumFitnessDimensions() {
        return numFitnessDimensions;
    }
    
    public T getScalarBestSoFar(final int subPopulation) {
        assert(subPopulation >= 0);
        assert(subPopulation < scalarBestSoFar.size());
        return scalarBestSoFar.get(subPopulation);
    }
    
    public T getBestSoFar(final int subPopulation, final int dimension) {
        assert(subPopulation >= 0);
        assert(subPopulation < scalarBestSoFar.size());
        assert(dimension >= 0);
        assert(dimension < numFitnessDimensions);
        return bestSoFar.get(subPopulation).get(dimension);
    }

    public void reset() {
        scalarBestSoFar.clear();
        bestSoFar.clear();
        lastBSFUpdate = -1;
    }

    /** Initialize the best-so-far records with the best individuals of the current population. */
    public void init(final int step, final Population<T, VectorFitness> population) {
        assert(step == 0);
        assert(population != null);
        assert(scalarBestSoFar.isEmpty());
        assert(bestSoFar.isEmpty());
        lastBSFUpdate = 0;
        for (int i = 0; i < population.numSuppopulations(); i++) {
            scalarBestSoFar.add(population.getBest(i, scalarFitnessComparator));
            bestSoFar.add(getBestsOfSubpopulation(population, i));
        }
        assert(repOK());
    }

    /** Update the best-so-far records against the best individuals of the current population. */
    public void update(final int step, final Population<T, VectorFitness> population) {
        assert(population != null);
        if (step - lastBSFUpdate > 1)
            throw new IllegalStateException(String.format("%s: the ping() method was called after an interval of %d steps.  It must be called every step in order to maintain a valid record of the best-so-far individual.", this.getClass().getSimpleName(), step - lastBSFUpdate));
        if (step == lastBSFUpdate)
            return;
        lastBSFUpdate = step;

        for (int i = 0; i < population.numSuppopulations(); i++) {
            final T scalarBest = population.getBest(i, scalarFitnessComparator);
            if (scalarFitnessComparator.betterThan(scalarBest, scalarBestSoFar.get(i)))
                scalarBestSoFar.set(i, scalarBest);
            final List<T> bests = getBestsOfSubpopulation(population, i);
            assert(bests.size() == numFitnessDimensions);
            for (int j = 0; j < bests.size(); j++)
                if (new VectorFitnessComparator(vectorFitnessComparator, j).betterThan(bests.get(j), bestSoFar.get(i).get(j)))
                    bestSoFar.get(i).set(j, bests.get(j));
        }
        assert(repOK());
    }

    private List<T> getBestsOfSubpopulation(final Population<T, VectorFitness> population, final int subPop) {
        assert(population != null);
        assert(subPop >= 0);
        assert(subPop < population.numSuppopulations());
        final List<T> bests = new ArrayList<>(numFitnessDimensions);
        for (int funID = 0; funID < numFitnessDimensions; funID++) {
            final VectorFitnessComparator funComparator = new VectorFitnessComparator(vectorFitnessComparator, funID);
            bests.add((T)population.getBest(subPop, funComparator));
        }
        return bests;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK() {
        return scalarFitnessComparator != null
                && vectorFitnessComparator != null
                && numFitnessDimensions > 0
                && scalarBestSoFar != null
                && bestSoFar != null
                && scalarBestSoFar.size() == bestSoFar.size()
                && lastBSFUpdate >= -1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof VectorBestSoFarTable))
            return false;
        final VectorBestSoFarTable ref = (VectorBestSoFarTable)o;
        return numFitnessDimensions == ref.numFitnessDimensions
                && lastBSFUpdate == ref.lastBSFUpdate
                && scalarFitnessComparator.equals(ref.scalarFitnessComparator)
                && vectorFitnessComparator.equals(ref.vectorFitnessComparator)
                && bestSoFar.equals(ref.bestSoFar)
                && scalarBestSoFar.equals(ref.scalarBestSoFar);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.scalarFitnessComparator);
        hash = 43 * hash + Objects.hashCode(this.vectorFitnessComparator);
        hash = 43 * hash + this.numFitnessDimensions;
        hash = 43 * hash + Objects.hashCode(this.scalarBestSoFar);
        hash = 43 * hash + Objects.hashCode(this.bestSoFar);
        hash = 43 * hash + this.lastBSFUpdate;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: scalarFitnessComparator=%s, vectorFitnessComparator=%s, numFitnessDimensions=%d, scalarBestSoFar=%s, bestSoFar=%s, lastBSFUpdate=%d]",
                this.getClass().getSimpleName(),
                scalarFitnessComparator,
                vectorFitnessComparator,
                numFitnessDimensions,
                scalarBestSoFar,
                bestSoFar,
                lastBSFUpdate);
    }
    // </editor-fold>
}
