package SigmaEC.app.lsystems;

import SigmaEC.ContractObject;
import SigmaEC.util.Parameters;
import java.awt.geom.Path2D;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class TurtleGraphics extends ContractObject {
    public final static String P_DELTA_ANGLE = "deltaAngle";
    public final static String P_STEP_SIZE = "stepSize";
    
    public static final String V_FORWARD_DRAW = "F";
    public static final String V_FORWARD_NODRAW = "f";
    public static final String V_LEFT = "+";
    public static final String V_RIGHT = "-";
    
    private final double deltaAngle;
    private final double stepSize;
    private final Path2D path;
    
    public Path2D getPath() {
        return path;
    }
    
    public TurtleGraphics(final Parameters parameters, final String base, final String inputString) {
        assert(inputString != null);
        
        deltaAngle = parameters.getDoubleParameter(Parameters.push(base, P_DELTA_ANGLE));
        if (Double.isInfinite(deltaAngle) || Double.isNaN(deltaAngle))
            throw new IllegalArgumentException(String.format("%s: angle is NaN or infinite.  Must be finite.", this.getClass().getSimpleName()));
        stepSize = parameters.getDoubleParameter(Parameters.push(base, P_STEP_SIZE));
        if (Double.isInfinite(stepSize) || Double.isNaN(stepSize))
            throw new IllegalArgumentException(String.format("%s: stepSize is NaN or infinite.  Must be finite.", this.getClass().getSimpleName()));
        path = stringToPath(inputString);
        
        assert(repOK());
    }
    
    private Path2D stringToPath(final String inputString) {
        assert(inputString != null);
        
        double x = 150, y = 150, theta = 0;
        final Path2D path = new Path2D.Double();
        path.moveTo(x, y);
        for (final char c : inputString.toCharArray()) {
            if (c == V_FORWARD_DRAW.charAt(0)) {
                x += stepSize * Math.cos(theta);
                y += stepSize * Math.sin(theta);
            }
            else if (c == V_FORWARD_NODRAW.charAt(0))
                throw new UnsupportedOperationException();
            else if (c == V_LEFT.charAt(0))
                theta += deltaAngle;
            else if (c == V_RIGHT.charAt(0))
                theta -= deltaAngle;
            else
                throw new IllegalArgumentException(String.format("%s: Unrecognized character, '%c'.", this.getClass().getSimpleName(), c));
            path.lineTo(x, y);
        }
        return path;
    }

    @Override
    public final boolean repOK() {
        return !Double.isInfinite(deltaAngle)
                && !Double.isNaN(deltaAngle)
                && stepSize > 0
                && !Double.isInfinite(stepSize)
                && path != null
                && P_DELTA_ANGLE != null
                && !P_DELTA_ANGLE.isEmpty()
                && P_STEP_SIZE != null
                && !P_STEP_SIZE.isEmpty()
                && V_FORWARD_DRAW != null
                && !V_FORWARD_DRAW.isEmpty()
                && V_FORWARD_NODRAW != null
                && !V_FORWARD_NODRAW.isEmpty()
                && V_LEFT != null
                && !V_LEFT.isEmpty()
                && V_RIGHT != null
                && !V_RIGHT.isEmpty();
    }

    @Override
    public boolean equals(final Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
