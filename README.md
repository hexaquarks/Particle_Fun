## :large_orange_diamond: Particle Geometrical Simulation
Time-driven particle simulation developped hybridly with *JavaFX* and *Swing*.

1. [Installation](#installation)
2. [Features](#features)
3. [Goals](#goals)

## 💻 <a name="installation"></a> Installation

### Requirements
- JavaFX SDK *OpenJFX*
- Java SE Development Kit *(JDK)*

### Run 
```
mvn clean package javafx:run
```
___

# 💡 <a name="features"></a> Features
### Current State Example
<p align="center">
  <img align="center" src="https://github.com/hexaquarks/Particle_Fun/blob/master/src/main/java/promotion/spiralsGlimpse2.gif" width="500"/>
</p>

## Arrangement in shapes of a dynamic and variable number of particles
<div align="center" markdown="1">
<table>
    <thead>
        <tr>
            <th align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/main/java/promotion/circleGif.gif" width="125" /></th>
            <th align="center"><code>circle</code></th>
            <th align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/main/java/promotion/squareGif.gif" width="125" /></th>
            <th align="center"><code>square</code></th>
            <td align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/main/java/promotion/looseSpiralGif.gif" width="125" /></td>
            <td align="center"><code>loose spiral</code></td>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/main/java/promotion/diamondGif.gif" width="125" /></td>
            <td align="center"><code>diamond</code></td>
            <td align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/main/java/promotion/spiralGif.gif" width="125" /></td>
            <td align="center"><code>spiral</code></td>
            <td align="center"><img src="https://github.com/hexaquarks/Particle_Geometry_Simulation/blob/master/src/main/java/promotion/sunflowerSpiralGif.gif" width="125" /></td>
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
  <img align="center" src="https://github.com/hexaquarks/Particle_Fun/blob/master/src/main/java/promotion/shapeDivisionGif.gif" width="500"/>
</p>

___ 

# <a name="goals"></a> Goals
The main goals of this project were to:
- Model physical phenomena computationally
- Use preexisting mathematical relationships to model mathematical abstractions, such as:
    1. Geometrical identities (*N-sided polygon*, ...)
    2. Matrix transformations (*Rotations*, *Stretching*, ...)
    3. Solution to equations involving the golden ratio

