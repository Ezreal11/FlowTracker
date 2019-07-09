# FlowTracker

FlowTracker is a pulgin for IntelliJ IDEA, which can capture the interaction activities of developers with using IDEA and recognize developers' flow states based on the captured interaction activities.



## Structure

This repository consists of two project: one is **flow-tracker** and another is **subjective-flow-questionnaire**.

The **flow-tracker** project products a plugin named **FlowTracker** for *IntelliJ IDEA*. This plugin is developed follow the [***IntelliJ Platform SDK***](http://www.jetbrains.org/intellij/sdk/docs/welcome.html), which can only capture interaction activities dispatched from the **IntelliJ Event Queue** and cannot capture anything out of IDE.

The **subjective-flow-questionniare** project products a questionnaire to sample developers' subjective flow experience. This questionnaire is packed into a independent *jar* package, which is used as a dependency library for the **flow-tracker** project. The reason for this is that, there will be Chinese scrambling problem when the questionnaire is directly implemented in the **flow-tracker** project, but the Chinese can be displayed normally after the questionnaire is packed into a *jar* package.



## IDE Support

**FlowTracker** is compatible with *IntelliJ IDEA* **`2018.1`**, **`2018.2`**, **`2018.3`** and **`2019.1`**;

It is also compatible with *Android Studio* with version >= **`3.2`**.

## Installation

To install the **FlowTracker** plugin in *IntelliJ IDEA*, you can follow the following steps:

1. Check the version of your IDE against the versions listed above;
2. Dowload the binary of **FlowTracker** plugin from `flow-tracker/plugin/flow-tracker-1.0.0.zip`; 
3. [Optional] You can build the plugin from source code following the steps introduced below;
4. Open your IDE, following ***File***, ***Settings...***, ***Plugins***, ***Install Plugin from Disk...***, select the *zip* file of plugin, ***OK***;
5. Restart your IDE, **FlowTracker** is working now and have a good time!


## Building Plugin from Source Code

If you want to build **FlowTracker** from the source code, please follow the following steps:

1. Create a plugin development project follow the [office tutorial](http://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html);
2. Checkout the source code from this repository;
3. Import `lib/`, `src/` and `build.gradle` from the **flow-tracker** project into your project;
4. Use *gradle* to build **FlowTracker** plugin.

