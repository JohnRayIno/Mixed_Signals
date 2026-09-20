package opampapp;

/**
 * The six op-amp applications required by the project brief, each
 * carrying the metadata (menu label, input field descriptors, and
 * the result unit) needed to build its panel generically.
 */
public enum CircuitType {

    INVERTING(
            "Inverting",
            "Inverting Amplifier",
            new String[]{"Rin (kΩ)", "Rf (kΩ)", "Vin (V)"},
            "Vout",
            "V",
            "Vout = -(Rf / Rin) x Vin"
    ),
    NON_INVERTING(
            "Non-Inverting",
            "Non-Inverting Amplifier",
            new String[]{"R1 (kΩ)", "Rf (kΩ)", "Vin (V)"},
            "Vout",
            "V",
            "Vout = (1 + Rf / R1) x Vin"
    ),
    DIFFERENTIAL(
            "Differential",
            "Differential Amplifier",
            new String[]{"R1 (kΩ)", "Rf (kΩ)", "V1 (V)", "V2 (V)"},
            "Vout",
            "V",
            "Vout = (Rf / R1) x (V2 - V1)   [balanced: R1=R3, Rf=R2=R4]"
    ),
    INTEGRATOR(
            "Integrator",
            "Integrator Circuit",
            new String[]{"R (kΩ)", "C (µF)", "Vin (V)", "t (ms)"},
            "Vout",
            "V",
            "Vout = -(Vin x t) / (R x C)   [constant Vin over time t]"
    ),
    I_TO_V(
            "Current to Voltage",
            "Current-to-Voltage Converter",
            new String[]{"Iin (mA)", "Rf (kΩ)"},
            "Vout",
            "V",
            "Vout = -Iin x Rf"
    ),
    V_TO_I(
            "Voltage to Current",
            "Voltage-to-Current Converter",
            new String[]{"Vin (V)", "R1 (kΩ)"},
            "Iout",
            "mA",
            "Iout = Vin / R1"
    );

    public final String menuLabel;
    public final String title;
    public final String[] inputLabels;
    public final String resultName;
    public final String resultUnit;
    public final String formula;

    CircuitType(String menuLabel, String title, String[] inputLabels,
                String resultName, String resultUnit, String formula) {
        this.menuLabel = menuLabel;
        this.title = title;
        this.inputLabels = inputLabels;
        this.resultName = resultName;
        this.resultUnit = resultUnit;
        this.formula = formula;
    }

    /**
     * Computes the result for this circuit type given the raw input
     * values in the same order as {@link #inputLabels}.
     */
    public double compute(double[] v) {
        switch (this) {
            case INVERTING: {
                double rin = v[0], rf = v[1], vin = v[2];
                return -(rf / rin) * vin;
            }
            case NON_INVERTING: {
                double r1 = v[0], rf = v[1], vin = v[2];
                return (1.0 + (rf / r1)) * vin;
            }
            case DIFFERENTIAL: {
                double r1 = v[0], rf = v[1], v1 = v[2], v2 = v[3];
                return (rf / r1) * (v2 - v1);
            }
            case INTEGRATOR: {
                double r = v[0], c = v[1], vin = v[2], t = v[3];
                return -(vin * t) / (r * c);
            }
            case I_TO_V: {
                double iin = v[0], rf = v[1];
                return -(iin * rf);
            }
            case V_TO_I: {
                double vin = v[0], r1 = v[1];
                return vin / r1;
            }
            default:
                throw new IllegalStateException("Unhandled circuit type: " + this);
        }
    }
}
