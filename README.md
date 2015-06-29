# SigmaEC

SigmaEC is a flexible evolutionary computation experiment framework written in
Java.

Its distinguishing feature is that it uses a simple experiment definition
language based on Java property files to perform complete [dependency injection][2].

The user uses configuration files to wire together components into potentially
complex algorithms and experiments.  The configuration language defines not only
the free parameters, but also the high-level structure of the entire algorithm.

## Inversion of Control

The idea is to provide complete [control inversion][1], keeping implementation
details separate from the high-level design of the algorithm and experimental
apparatus.  If you don't need to change low-level functionality, you don't
need to write any Java.

Low-level components can easily be added to provide custom functions such as
stopping conditions, mutation operators, and high-level evolutionary controllers,
or even entirely different kinds of algorithms such as data mining techniques
or simulations.

Other evolutionary computation frameworks such as Sean Luke's [ECJ][3] achieve
a great deal of extensibility and inversion of control, but SigmaEC takes it
to an extreme.  Components have access only to the dependencies they are 'wired
to' in the configuration files.  There is no concept of globally accessible
state in SigmaEC algorithms; therefore components cannot create unexpected side
effects in other parts of the program.  This makes unit testing easier and
reduces the potential for confusion on the part of the user.

## Features

  - Bitstring and real-valued genetic algorithms.
  - Ant Colony Optimization
  - Classic selection mechanisms.
  - A large variety of classic test objectives.
  - Decorators for altering and combining real-valued objective functions with offsets, boundaries, etc.
  - A system for sampling objective functions from an objective function generator, such as TSP.
  - A high-level mechanism for evaluating an EA's performance on a suite of functions.
  - Functionality for printing visualizations of real-valued objective functions to a PDF for verification.

[1]: http://en.wikipedia.org/wiki/Inversion_of_control
[2]: http://en.wikipedia.org/wiki/Dependency_injection
[3]: http://cs.gmu.edu/~eclab/projects/ecj