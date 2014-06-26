# SigmaEC

A flexible evolutionary computation framework by Eric 'Siggy' Scott.

## Overview

  - Java properties files are used to specify all the operators used in
experiments and their parameters, including sophisticated set-ups like
meta-evolution.

  - A CircleOfLife defines the outer evolutionary loop.

  - Mators and Mutators operate on (pairs of) parents.

  - Generators transform entire populations according to operators (i.e.
creating a new generation).

  - Selectors apply selection according to an ObjectiveFunction.

  - Metrics measure characteristics of a population.

  - Some commonly used ObjectiveFunctions are implemented, including the
[De Jong test suite][1].  Some wrapper classes are also provided which
[decorate][2] objectives with bounds (BoundedObjectiveFunction) or rotate/scale
them according to an linear transformation (AffineTranslatedDoubleObjective).

[1]: http://www2.denizyuret.com/pub/aitr1569/node19.html
[2]: http://en.wikipedia.org/wiki/Decorator_pattern