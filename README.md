# SigmaEC

An evolutionary computation framework by Eric 'Siggy' Scott.  Provides common
representations, operators, selection mechanisms, and objective functions.

Currently only genetic algorithms with real-valued genes and real-vector
phenotypes are implemented.

## Overview

  - A CircleOfLife defines the outer evolutionary loop.
  - Mators and Mutators operate on (pairs of) parents.
  - Generators transform entire populations according to operators (i.e.
creating a new generation).
  - Selectors apply selection according to an ObjectiveFunction.
  - Multiple inheritance of interfaces is used to define types of Individuals by
mixing and matching properties.  For instance, an Individual that has a linear
genetic representation and a real-vector phenotype should inherit from
LinearGenomeIndividual and DoubleVectorIndividual.
  - Some commonly used ObjectiveFunctions are implemented, including the
[De Jong test suite][1].  As a couple of wrapper classes are also provided which
[decorate][2] objectives with bounds (BoundedObjectiveFunction) or rotate/scale
them according to an linear transformation (AffineTranslatedDoubleObjective).

[1]: http://www2.denizyuret.com/pub/aitr1569/node19.html
[2]: http://en.wikipedia.org/wiki/Decorator_pattern