package SigmaEC.represent.cgp;

import SigmaEC.represent.Decoder;
import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.util.Parameters;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Eric O. Scott
 */
public class CGPFormatterTest {
    private final static String PARAMS = "params";
    private final static String DECODER = "decoder";
    private final static String BASE = "base";
    
    private Parameters.Builder params;
    private CGPParameters cgpParams;
    private Decoder<IntVectorIndividual, CartesianIndividual> decoder;
    
    public CGPFormatterTest() {
    }
    
    @Before
    public void setup() {
        params = getParams();
        cgpParams = params.build().getInstanceFromParameter(PARAMS, CGPParameters.class);
        decoder = params.build().getInstanceFromParameter(DECODER, Decoder.class);
    }
    
    private static Parameters.Builder getParams() {
        return new Parameters.Builder(new Properties())
                .setParameter(PARAMS, "SigmaEC.represent.cgp.CGPParameters")
                .setParameter(Parameters.push(PARAMS, CGPParameters.P_NUM_INPUTS), "2")
                .setParameter(Parameters.push(PARAMS, CGPParameters.P_NUM_OUTPUTS), "2")
                .setParameter(Parameters.push(PARAMS, CGPParameters.P_NUM_LAYERS), "2")
                .setParameter(Parameters.push(PARAMS, CGPParameters.P_NUM_NODES_PER_LAYER), "2")
                .setParameter(Parameters.push(PARAMS, CGPParameters.P_MAX_ARITY), "2")
                .setParameter(Parameters.push(PARAMS, CGPParameters.P_LEVELS_BACK), "1")
                .setParameter(Parameters.push(PARAMS, CGPParameters.P_NUM_PRIMITIVES), "2")
                
                .setParameter(DECODER, "SigmaEC.represent.cgp.IntVectorToCartesianIndividualDecoder")
                .setParameter(Parameters.push(DECODER, IntVectorToCartesianIndividualDecoder.P_CGP_PARAMETERS), "%" + PARAMS)
                .setParameter(Parameters.push(DECODER, IntVectorToCartesianIndividualDecoder.P_PRIMITIVES), "SigmaEC.evaluate.objective.function.NAND,SigmaEC.evaluate.objective.function.XOR")
                
                .setParameter(BASE, "SigmaEC.represent.cgp.CGPFormatter")
                .setParameter(Parameters.push(BASE, CGPFormatter.P_OUTPUT_FORMAT), "TIKZ");
    }

    @Test
    public void testGenomeToString() {
        System.out.println("genomeToString");
        final IntVectorIndividual intInd = new IntVectorIndividual.Builder(new int[] { 0, 0, 1, 
                                                                                       0, 1, 0,
                                                                                       1, 2, 3,
                                                                                       0, 3, 3,
                                                                                       4, 5}).build();
        final CartesianIndividual ind = decoder.decode(intInd);
        final CGPFormatter instance = new CGPFormatter(params.build(), BASE);
        
        String expResult = "\\begin{tikzpicture}[circuit logic US, every circuit symbol/.style={thick}]\n" +
"	\\node[black,buffer gate,point down,draw=none] (node0) at (0,0) {\\rotatebox{90}{$I_{0}$}};\n" +
"	\\node[black,buffer gate,point down,draw=none] (node1) at (2,0) {\\rotatebox{90}{$I_{1}$}};\n" +
"	\\node[black,nand gate,inputs={nn}, point down] (node2) at (0,-2) {};\n" +
"		\\draw (node0.output) -- ++(down:5mm) -| (node2.input 1);\n" +
"		\\draw (node1.output) -- ++(down:5mm) -| (node2.input 2);\n" +
"	\\node[black,nand gate,inputs={nn}, point down] (node3) at (2,-2) {};\n" +
"		\\draw (node1.output) -- ++(down:5mm) -| (node3.input 1);\n" +
"		\\draw (node0.output) -- ++(down:5mm) -| (node3.input 2);\n" +
"	\\node[black,xor gate,inputs={nn}, point down] (node4) at (0,-4) {};\n" +
"		\\draw (node2.output) -- ++(down:5mm) -| (node4.input 1);\n" +
"		\\draw (node3.output) -- ++(down:5mm) -| (node4.input 2);\n" +
"	\\node[black,nand gate,inputs={nn}, point down] (node5) at (2,-4) {};\n" +
"		\\draw (node3.output) -- ++(down:5mm) -| (node5.input 1);\n" +
"		\\draw (node3.output) -- ++(down:5mm) -| (node5.input 2);\n" +
"	\\node[black,buffer gate, point down,draw=none] (out0) at (0,-6) {\\rotatebox{90}{$O_{0}$}};\n" +
"		\\draw (node4.output) -- ++(down:5mm) -| (out0);\n" +
"	\\node[black,buffer gate, point down,draw=none] (out1) at (2,-6) {\\rotatebox{90}{$O_{1}$}};\n" +
"		\\draw (node5.output) -- ++(down:5mm) -| (out1);\n" +
"\\end{tikzpicture}\n";
        String result = instance.genomeToString(ind);
        assertEquals(expResult, result);
    }
}
