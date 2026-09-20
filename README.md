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
  OpAmpPanel.java           - diagram + inputs + Calculate button + result

APPLICATIONS INCLUDED
-----------------------
1. Inverting Amplifier            Vout = -(Rf/Rin) x Vin
2. Non-Inverting Amplifier        Vout = (1 + Rf/R1) x Vin
3. Differential Amplifier         Vout = (Rf/R1) x (V2 - V1)
4. Integrator Circuit             Vout = -(Vin x t)/(R x C)
5. Current-to-Voltage Converter   Vout = -Iin x Rf
6. Voltage-to-Current Converter   Iout = Vin/R1

NOTES
------
- Window is fixed-size (setResizable(false)) per the requirement.
- Each application has its own hand-drawn circuit diagram (Graphics2D),
  so there are no external image files to lose or repath.
- Sidebar buttons highlight the active application, matching the
  reference calculator app's light-blue card style.
