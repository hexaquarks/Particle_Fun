## :large_orange_diamond: Particle Simulation
Time-driven particle simulation developped hybridly with *JavaFX* and *Swing*.

## 💻 Installation

### Requirements
- JavaFX SDK *OpenJFX*
- Java SE Development Kit *(JDK)*
- 
### VSCode
Clone the project
```
git clone https://github.com/hexaquarks/Particle_Geometry_Simulation.git && cd Particle_Geometry_Simulation 
```
Edit `vmArgs` in *./.vscode/launch.json* with appropriate *JavaFX* `path` variable.
```
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Launch AppFrame",
            "request": "launch",
            "vmArgs": "--module-path %PATH_TO_JAVAFX%/lib --add-modules javafx.controls,javafx.fxml,javafx.swing",
            "mainClass": "AppFrame",
            "projectName": "Particle_Geometry_Simulation_4f401646"
        }
    ]
}
```
Edit *./.vscode/launch.json* with appropriate *JavaFX* `path` variable.
```
{
    "java.project.sourcePaths": ["src"],
    "java.project.outputPath": "bin",
    "java.project.referencedLibraries": [
        "lib/**/*.jar",
        "%PATH_TO_JAVAFX%/lib/*.jar"
    ]
}
```

### IntelliJ
TODO

### Eclipse
TODO

___

# 💡 Features
### Current State Example
<p align="center">
  <img align="center" src="https://github.com/hexaquarks/Particle_Fun/blob/master/src/Promotion/spiralsGlimpse2.gif" width="500"/>
</p>

## Arrangement in shapes of a dynamic and variable number of particles
<div align="center" markdown="1">
<table>
    <thead>
        <tr>
            <th align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/Promotion/circleGif.gif" width="125" /></th>
            <th align="center"><code>circle</code></th>
            <th align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/Promotion/squareGif.gif" width="125" /></th>
            <th align="center"><code>square</code></th>
            <td align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/Promotion/looseSpiralGif.gif" width="125" /></td>
            <td align="center"><code>loose spiral</code></td>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/Promotion/diamondGif.gif" width="125" /></td>
            <td align="center"><code>diamond</code></td>
            <td align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/Promotion/spiralGif.gif" width="125" /></td>
            <td align="center"><code>spiral</code></td>
            <td align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/Promotion/sunflowerSpiralGif.gif" width="125" /></td>
            <td align="center"><code>sunflower spiral</code></td>
        </tr>
    </tbody>
</table>
</div>

## In-built natural forces modeling 
- Gravitational force 
- Electrostatic force
- Elastic collisions

## Shape division and rotation
<p align="center">
  <img align="center" src="https://github.com/hexaquarks/Particle_Fun/blob/master/src/Promotion/shapeDivisionGif.gif" width="500"/>
</p>

___ 

# <a name="goals"></a> Goals
The main goals of this project were to:
- Model physical phenomena computationally
- Use preexisting mathematical relationships to model mathematical abstractions, such as:
    1. Geometrical identities (*N-sided polygon*, ...)
    2. Matrix transformations (*Rotations*, *Stretching*, ...)
    3. Solution to equations involving the golden ratio

