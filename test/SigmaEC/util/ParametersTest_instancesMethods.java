package SigmaEC.util;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.objective.real.RastriginObjective;
import SigmaEC.evaluate.objective.real.SphereObjective;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author eric
 */
@RunWith(Parameterized.class)
public class ParametersTest_instancesMethods {
    
    private final static String PARAMETER_NAME = Parameters.push("test", "objective");
    private final static String DEFAULT_PARAMETER_NAME = Parameters.push("default", "objective");
    
    private final static SphereObjective INSTANCE = new SphereObjective(new Parameters.Builder(new Properties())
            .setParameter("obj.numDimensions", "10")
            .build(), "obj");
    private final static SphereObjective DEFAULT_INSTANCE = new SphereObjective(new Parameters.Builder(new Properties())
            .setParameter("obj.numDimensions", "15")
            .build(), "obj");
    private final static ObjectiveFunction DEFAULT_VALUE = new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj");
    
    @Parameterized.Parameter(0)
    public Parameters.Builder parameter;
    
    @Parameterized.Parameter(1)
    public Parameters.Builder defaultParameter;
    
    @Parameterized.Parameter(2)
    public Object defaultValue;
    
    
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        final Parameters.Builder params = new Parameters.Builder(new Properties())
                .setParameter(PARAMETER_NAME, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(PARAMETER_NAME, "numDimensions"), "10");
        
        final Parameters.Builder paramsInstance = new Parameters.Builder(new Properties())
                .registerInstance(PARAMETER_NAME, INSTANCE);
        
        final Parameters.Builder defaultParams = new Parameters.Builder(new Properties())
                .setParameter(DEFAULT_PARAMETER_NAME, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(DEFAULT_PARAMETER_NAME, "numDimensions"), "15");
        
        final Parameters.Builder defaultParamsInstance = new Parameters.Builder(new Properties())
                .registerInstance(DEFAULT_PARAMETER_NAME, DEFAULT_INSTANCE);
        
        final Object[][] domainVariables = new Object[][] {
            {
                null,
                params,
                params.registerInstance(PARAMETER_NAME, INSTANCE),
                paramsInstance
            },
            {
                null,
                defaultParams,
                defaultParams.registerInstance(DEFAULT_PARAMETER_NAME, DEFAULT_INSTANCE),
                defaultParamsInstance
            },
            {
                null,
                DEFAULT_VALUE
            }
        };
        
        return TestMisc.allCombinations(domainVariables);
    }
    
    public ParametersTest_instancesMethods() { }
    
    @Before
    public void setUp() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testGetInstanceFromClassName() {
        System.out.println(String.format("%s, %s, %s", parameter, defaultParameter, defaultValue));
        if (parameter == null || defaultParameter != null || defaultValue != null)
            return;
        final Parameters sut = parameter.build();
        final Object result = sut.getInstanceFromParameter(PARAMETER_NAME, SphereObjective.class);
        assertEquals(result, INSTANCE);
    }
}
