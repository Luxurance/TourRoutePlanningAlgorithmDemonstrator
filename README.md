# Tour Route Planning (TRP) Algorithm Demonstrator

---

### Abstract
The essence of starting a journey is to formulate or obtain a copy of the touring scheme. The tour route planning (TRP) problem asks for a pool of reasonable and preferable travel plans including the selection of places, duration of stay and so on, given a number of subjective and objective constraints. This project proposes an algorithmic method to simulate and optimise the TRP process, taking into consideration of quarantine conditions among various regions. Additionally, its performance on artificial graphs with different properties involving vertices count as well as sparsity will be evaluated. Specifically, a specific variant of the Genetic Algorithm (GA) composing multiple objectives in conjunction with heuristic searches and constraints is adopted to address the TRP problem. In order to preserve the correctness and extensibility of this project, an open-source algorithmic framework written in Java, called `JMetal`, is used to implement the required algorithm. Experiments are conducted to tune and evaluate this approach, after which the usability is explored by testing on a transformed public dataset. 

(More on Report)

This repository contains the core classes implementing the proposed algorithm, presenting in the `trp` directory, as well as a simple web demonstrator building upon a lightweight web framework named `Spark Java`, making use of a derived public dataset mentioned in the project report. The simulation of the shortest path problem (SPP) as a warm-up task of this project is also provided here, which can be found in the directory `spp` and played around with `src/test/java/spp/SPPMain.java`.

### Manual
1) Open the Maven project with `IntelliJ`
2) Wait until the Maven finishes resolving dependencies
3) Find the `WebMain.java` (as the structure below) file, and run it directly
3) Access website via `localhost:4567/demonstrator`

```

/TourRoutePlanningAlgorithmDemonstrator/
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── spp
│   │   │   ├── trp
│   │   │   ├── utils
│   │   │   └── webapp
│   │   │       ├── WebMain.java
│   │   │       └── ...
│   │   └── resources
│   └── test
│       └── java
│           ├── spp
│           ├── trp
│           ├── utils
│           └── webapp
├── target
├── .gitignore
├── LICENSE
├── README.md
└── pom.xml
    
```

GitHub Link: https://github.com/Luxurance/TourRoutePlanningAlgorithmDemonstrator

