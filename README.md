Op-Amp Application Solver — ECPE304 Midterm Project
=====================================================

HOW TO COMPILE AND RUN (plain JDK, no IDE needed)
---------------------------------------------------
1. Open a terminal in this folder.
2. Compile:
     javac -d out src/opampapp/*.java
3. Run:
     java -cp out opampapp.Main

HOW TO OPEN IN AN IDE (NetBeans / IntelliJ / Eclipse)
-------------------------------------------------------
- Import "src" as the source root, package opampapp.
- Set opampapp.Main as the main class.

PROJECT STRUCTURE
------------------
src/opampapp/
  Main.java               - entry point
  MainFrame.java           - window, sidebar menu, CardLayout content switcher
  StyledButton.java        - gradient sidebar button (reference UI style)
  UITheme.java              - shared colors/fonts
  CircuitType.java          - the 6 applications, their input fields, and formulas
  CircuitDiagramPanel.java  - hand-drawn schematic for each application
  IntegratorWaveformPanel.java - Vin/Vout waveform plots (Integrator page only)
  OpAmpPanel.java           - diagram + inputs + Calculate button + result

APPLICATIONS INCLUDED
-----------------------
1. Inverting Amplifier            Vout = -(Rf/Rin) x Vin
2. Non-Inverting Amplifier        Vout = (1 + Rf/R1) x Vin
3. Differential Amplifier         Vout = ((R1 + Rf)/R1) x (R3/(R2 + R3)) x V2 - (Rf/R1) x V1
4. Integrator Circuit             Vout = -(Vin x t)/(R x C)
5. Current-to-Voltage Converter   Vout = Iin x Rf
6. Voltage-to-Current Converter   Iout = Vin/R1

NOTES
------
- Window is fixed-size (setResizable(false)) per the requirement.
- Each application has its own drawn circuit diagram (Graphics2D),
  so there are no external image files to lose or repath.
- Sidebar buttons highlight the active application.

TEST CASES AND EXPECTED RESULTS
---------------------------------

### 1. Inverting Amplifier

Formula: $V_{out} = -\left(\frac{R_f}{R_{in}}\right) \times V_{in}$

| Test Case | $R_{in}$ (kΩ) | $R_f$ (kΩ) | $V_{in}$ (V) | Expected $V_{out}$ |
|---|---:|---:|---:|---|
| Unity Gain | 10 | 10 | 2.5 | $V_{out} = -2.5$ V |
| Amplification | 1 | 10 | 0.5 | $V_{out} = -5$ V |
| Attenuation | 20 | 10 | 4.0 | $V_{out} = -2$ V |
| Negative Input | 10 | 47 | -1.2 | $V_{out} = 5.64$ V |
| Zero Input | 10 | 100 | 0 | $V_{out} = 0$ V |

### 2. Non-Inverting Amplifier

Formula: $V_{out} = \left(1 + \frac{R_f}{R_1}\right) \times V_{in}$

| Test Case | $R_1$ (kΩ) | $R_f$ (kΩ) | $V_{in}$ (V) | Expected $V_{out}$ |
|---|---:|---:|---:|---|
| Gain of 2 | 10 | 10 | 2.5 | $V_{out} = 5$ V |
| Gain of 11 | 1 | 10 | 0.5 | $V_{out} = 5.5$ V |
| Fractional Inputs | 2.2 | 10 | 0.33 | $V_{out} = 1.83$ V |
| Negative Input | 10 | 47 | -1.2 | $V_{out} = -6.84$ V |
| Zero Input | 10 | 100 | 0 | $V_{out} = 0$ V |

### 3. Differential Amplifier

General formula: $V_{out} = \left(\frac{R_1 + R_f}{R_1}\right)\left(\frac{R_3}{R_2 + R_3}\right)V_2 - \frac{R_f}{R_1}V_1$

| Test Case | $R_1$ (kΩ) | $R_f$ (kΩ) | $R_2$ (kΩ) | $R_3$ (kΩ) | $V_1$ (V) | $V_2$ (V) | Expected $V_{out}$ |
|---|---:|---:|---:|---:|---:|---:|---|
| Unity Difference | 10 | 10 | 10 | 10 | 1.0 | 3.5 | $V_{out} = 2.5$ V |
| Amplified Difference | 10 | 100 | 10 | 10 | 2.0 | 2.5 | $V_{out} = -6.25$ V |
| Negative Difference | 10 | 20 | 10 | 10 | 4.0 | 1.5 | $V_{out} = -5.75$ V |
| Common Mode Signal | 10 | 50 | 10 | 50 | 3.3 | 3.3 | $V_{out} = 0$ V |
| Zero Voltage Inputs | 10 | 100 | 10 | 10 | 0 | 0 | $V_{out} = 0$ V |

### 4. Ideal Integrator

Time-domain step response:
$V_{out}(t) = -\frac{1}{R \times C} \int V_{in}\,dt = -\frac{V_{in} \times t}{R \times C}$

| Test Case | $R$ (kΩ) | $C$ (μF) | $V_{in}$ (V) | Time $t$ (ms) | Expected $V_{out}$ |
|---|---:|---:|---:|---:|---|
| Standard Ramp | 10 | 1 | 1.0 | 10 | $V_{out} = -1$ V |
| Fast Ramp | 10 | 0.1 | 0.5 | 1 | $V_{out} = -0.5$ V |
| Negative Input Ramp | 10 | 1 | -2.0 | 5 | $V_{out} = 1$ V |
| High Capacitance | 100 | 10 | 5.0 | 100 | $V_{out} = -0.5$ V |
| Zero Input | 10 | 1 | 0 | 10 | $V_{out} = 0$ V |

### 5. Current-to-Voltage Converter (Transimpedance)

Formula: $V_{out} = I_{in} \times R_f$

| Test Case | $I_{in}$ (mA) | $R_f$ (kΩ) | Expected $V_{out}$ |
|---|---:|---:|---|
| Standard Signal | 1.0 | 10 | $V_{out} = 10$ V |
| Microamp Signal | 0.05 | 100 | $V_{out} = 5$ V |
| Negative Current Input | -0.2 | 22 | $V_{out} = -4.4$ V |
| Small Fractional Input | 0.001 | 4.7 | $V_{out} = 0.0047$ V (4.7 mV) |
| Zero Current Input | 0 | 10 | $V_{out} = 0$ V |

### 6. Voltage-to-Current Converter (Transconductance)

Formula: $I_{out} = \frac{V_{in}}{R_s}$

| Test Case | $V_{in}$ (V) | $R_s$ (kΩ) | Expected $I_{out}$ |
|---|---:|---:|---|
| Standard Signal | 5.0 | 1.0 | $I_{out} = 5$ mA |
| Milli-volt Input | 0.5 | 10.0 | $I_{out} = 0.05$ mA (50 μA) |
| Negative Voltage Input | -2.5 | 0.5 | $I_{out} = -5$ mA |
| High Resistance Sense | 12.0 | 100.0 | $I_{out} = 0.12$ mA (120 μA) |
| Zero Input | 0 | 10.0 | $I_{out} = 0$ mA |

### 7. Input Validation and Error Handling (current behavior)

These cases apply across all GUI tabs. `OpAmpPanel.calculate()` only checks that each field
parses as a number — it does not separately flag blank fields, negative values, or zero
resistor/capacitor values.

| Scenario / Test Case | Input Condition | Actual GUI Result |
|---|---|---|
| Invalid Text | Inputs such as `10k`, `abc`, or `--5` (`Double.parseDouble` fails) | Warning dialog: “Please enter a valid number for "<field label>".” Result label is cleared. |
| Blank Field | Any field left empty | Same as Invalid Text above — an empty string also fails `Double.parseDouble`. |
| Negative Component Values | $R < 0$ or $C < 0$ | **Not rejected.** The value is parsed normally and used as-is in `compute()`. |
| Division by Zero | Resistor $R = 0$ or capacitor $C = 0$ | **Not caught as an error.** Since all math uses `double`, dividing by 0 yields `Infinity` or `NaN` rather than throwing, so the result label shows `Infinity`/`NaN` instead of a dialog. |

> Note: the code does catch `ArithmeticException` around the `compute()` call, but none of the
> current formulas throw one (integer division isn't used), so that catch block is currently
> unreachable in practice.
