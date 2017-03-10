package SigmaEC.evaluate;

import SigmaEC.meta.Operator;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.meta.Fitness;
import SigmaEC.operate.constraint.Constraint;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric O. Scott
 */
public class EvaluationOperator<T extends Individual<F>, P, F extends Fitness> extends Operator<T> {
    public final static String P_DECODER = "decoder";
    public final static String P_OBJECTIVE = "objective";
    public final static String P_REEVALUATE = "reevaluate";
    public final static String P_NUM_THREADS = "numThreads";
    public final static String P_CONSTRAINT = "constraint";
    
    private final Option<Decoder<T, P>> decoder;
    private final ObjectiveFunction<P, F> objective;
    private final boolean reevaluate;
    private final int numThreads;
    private final Option<Constraint<T>> constraint;
    
    public EvaluationOperator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        decoder = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        reevaluate = parameters.getOptionalBooleanParameter(Parameters.push(base, P_REEVALUATE), false);
        numThreads = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_THREADS), Runtime.getRuntime().availableProcessors());
        constraint = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_CONSTRAINT), Constraint.class);
        assert(repOK());
    }
    
    /** Evaluate the fitness of all the individuals in a population.
     * 
     * @param run Ignored.
     * @param generation Ignored.
     * @param parentPopulation The population to be evaluated.
     * @return A population of new individuals with their fitness value (re)set
     *   and their parents attribute cleared.
     */
    @Override
    public List<T> operate(final int run, final int generation, final List<T> parentPopulation) {
        assert(run >= 0);
        assert(generation >= 0);
        assert(parentPopulation != null);
        assert(!Misc.containsNulls(parentPopulation));
        if (numThreads == 1)
            return evaluateSequentially(parentPopulation);
        
        final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        final Collection<Callable<T>> tasks = new ArrayList<Callable<T>>(parentPopulation.size()) {{
            for (final T ind : parentPopulation) {
                if (constraint.isDefined() && constraint.get().isViolated(ind))
                    throw new IllegalStateException(String.format("%s: unexpected constraint violation detected.", this.getClass().getSimpleName()));
                add(new EvalThread(ind));
            }
        }};
        try {
            final List<Future<T>> results = executor.invokeAll(tasks);
            final List<T> childPopulation = new ArrayList<>(parentPopulation.size());
            for (final Future<T> f : results)
                childPopulation.add(f.get());
            return childPopulation;
        } catch (final InterruptedException | ExecutionException ex) {
            Logger.getLogger(EvaluationOperator.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        finally {
            executor.shutdown();
        }
    }
    
    private List<T> evaluateSequentially(final List<T> parentPopulation) {
        assert(parentPopulation != null);
        assert(!Misc.containsNulls(parentPopulation));
        return new ArrayList<T>(parentPopulation.size()) {{
                for (final T ind : parentPopulation) {
                    if (constraint.isDefined() && constraint.get().isViolated(ind))
                        throw new IllegalStateException(String.format("%s: unexpected constraint violation detected.", this.getClass().getSimpleName()));
                    if (!reevaluate && ind.isEvaluated())
                        // Keep existing fitness value
                        add((T) ind.clearParents());
                    else {
                        // Evaluate or re-evaluate fitness
                        add(evaluate(ind));
                    }
                }
        }};
    }
    
    private class EvalThread implements Callable<T> {
        private final T ind;
        
        EvalThread(final T ind) {
            assert(ind != null);
            this.ind = ind;
            assert(repOK());
        }
        
        @Override
        public T call() throws Exception {
            assert(repOK());
            return evaluate(ind);
        }
        
        public final boolean repOK() {
            return ind != null;
        }
    }
    
    public T evaluate(final T ind) {
        final P phenotype = decoder.isDefined() ? decoder.get().decode(ind) : (P) ind;
        final F fitness = objective.fitness(phenotype);
        return (T) ind.clearParents().setFitness(fitness);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_DECODER != null
                && !P_DECODER.isEmpty()
                && P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && decoder != null
                && objective != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EvaluationOperator))
            return false;
        final EvaluationOperator ref = (EvaluationOperator) o;
        return decoder.equals(ref.decoder)
                && objective.equals(ref.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.decoder);
        hash = 67 * hash + Objects.hashCode(this.objective);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_DECODER, decoder,
                P_OBJECTIVE, objective);
    }
    // </editor-fold>
}
