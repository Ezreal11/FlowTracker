# FlowTracker

FlowTracker is a pulgin for IntelliJ IDEA, which can capture the interaction activities of developers with using IDEA and recognize developers' flow states based on the captured interaction activities.



## Structure

This repository consists of two project: one is **interaction-activity-tracker** and another is **subjective-flow-questionnaire**, both **interaction-activity-tracker** and **subjective-flow-questionnaire** projects make up **FlowTracker** plugin.

The **interaction-activity-tracker** project products a plugin named of *IntelliJ IDEA*. This plugin is developed follow the [***IntelliJ Platform SDK***](http://www.jetbrains.org/intellij/sdk/docs/welcome.html), which can only capture interaction activities dispatched by the **IntelliJ Event Queue** and cannot capture anything out of IDE.

The **subjective-flow-questionniare** project products a questionnaire to sample developers' subjective flow experience. This questionnaire is packed into a independent *jar* package, which is used as a dependency library for the **interaction-activity-tracker** project. The reason for this is that, there will be Chinese scrambling problem when the questionnaire is directly implemented in the **interaction-activity-tracker** project, but the Chinese can be displayed normally after the questionnaire is packed into a *jar* package.



![structure](images/structure.png)



## Installation

To install the **FlowTracker** plugin in *IntelliJ IDEA*, you can follow the following steps:

1. Check the version of your *IntelliJ IDEA*: **FlowTracker** is compatible with *IntelliJ IDEA* **`2018.1`**, **`2018.2`**, **`2018.3`** and **`2019.1`**;
2. Get **FlowTracker**: you can get the built plugin from *interaction-activity-tracker/plugin/interaction-activity-tracker-1.0.0.zip* or build the plugin from source code by yourself;
3. Open *IntelliJ IDEA*, following ***File***, ***Settings...***, ***Plugins***, ***Install Plugin from Disk...***, select the *zip* file of plugin, ***OK***;
4. Restart your *IntelliJ IDEA*, **FlowTracker** is working now and have a good time!

*<span style="color:red">Support for Android Studio</span>*: **FlowTracker** also can be installed in *Android Studio*, and works in *Android Studio* whose version does not less than **`3.2`**.



## Building Plugin from Source Code

If you want to build **FlowTracker** from the source code, please follow the following steps:

1. Create a plugin development project follow the [office tutorial](http://www.jetbrains.org/intellij/sdk/docs/tutorials/build_system/prerequisites.html);
2. Checkout the source code from this repository;
3. Import `lib/`, `src/` and `build.gradle` from the **interaction-activity-tracker** project into your project;
4. Use *gradle* to build **FlowTracker** plugin.

