package net.lunglet.primme;

import com.sun.jna.Structure;

/**
 * Correction parameters.
 * <p>
 * <CODE>
 * typedef struct correction_params {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;int precondition;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;int robustShifts;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;int maxInnerIterations;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;struct JD_projectors projectors;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;primme_convergencetest convTest;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;double relTolBase;<br>
 } correction_params;
 </CODE>
 */
public final class CorrectionParams extends Structure {
    /** Flag to set if preconditioning is to be performed. */
    int precondition;

    int robustShifts;

    int maxInnerIterations;

    JDProjectors projectors;

    /** How to stop the inner QMR method. */
    int convTest;

    double relTolBase;
}
