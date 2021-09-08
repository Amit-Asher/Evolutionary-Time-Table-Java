# Evolutionary-Time-Table-Java

This repository contains JavaFX Desktop Application of Evolution Engine that solves Time-Table Problems.

The software was written in Java using Multi-threading, Design Patterns and Serialization with JAXB.



## How to use

Run the software from Run/Run.bat and press 'Browse' to load a test file from 'TestFiles' directory.

Choose at least one from the stopping conditions- by Generation, Fitness or Time.

Then, insert how often would you like to record the best solution in 'update Frequency'.

In the end, press 'Run'.

---------------------------------------

![alt text](https://github.com/Amit-Asher/Evolutionary-Time-Table-Java/blob/master/RMImgs/Run.png)

---------------------------------------

After you press 'Run', a new seperate Thread is going to run the evolution and update the JAT with the current state.

You can find the status of the algorithm in the progress bars, 'Generation Count' label, and 'Best Fitness' label.

While the thread is running, you can pause and stop it in any time.

in addition, you can update the settings of the problem that was loaded from the file in real time, such as:

Mutation, Selection, Crossover and Elitism.

------------------------------------------

![alt text](https://github.com/Amit-Asher/Evolutionary-Time-Table-Java/blob/master/RMImgs/Run2.png)

------------------------------------------

Once the algorithm finished, the solution can be found in 'Show Solution' Tab.

The rules scores for the best solution is shown and changing the ComboBox will show the solution by Class/Teacher/Raw.

On the Top-Right of the window, you can go 'Back' or 'Forward'- see the previous best solution of a recorded generation.

----------------------------------------

![alt text](https://github.com/Amit-Asher/Evolutionary-Time-Table-Java/blob/master/RMImgs/Solution2.png)

![alt text](https://github.com/Amit-Asher/Evolutionary-Time-Table-Java/blob/master/RMImgs/Solution.png)

------------------------------------------

For visual Presentation, press 'Statistics' Tab.

The graph will show the best fitness that the algorithm achieved at each generation.

-----------------------------------------

![alt text](https://github.com/Amit-Asher/Evolutionary-Time-Table-Java/blob/master/RMImgs/Statistics.png)

--------------------------------------

In order to see the original constraints of the Time Table Problem such as:

Days, Hours, Teachers, Subjects, Subjects a Teacher can teach, Classes,

Hours and Subjects a class need to learn, Rules for Fitness evaluation and more...

press 'Show Problem' Tab.

----------------------------------------

![alt text](https://github.com/Amit-Asher/Evolutionary-Time-Table-Java/blob/master/RMImgs/Problem.png)

![alt text](https://github.com/Amit-Asher/Evolutionary-Time-Table-Java/blob/master/RMImgs/Problem2.png)

-------------------------------------

Some GUI features were implemented such as changing theme with CSS, and Java animations.

![alt text](https://github.com/Amit-Asher/Evolutionary-Time-Table-Java/blob/master/RMImgs/CSS_picture.png)

------------------------------------
