# Apollo User Guide (as of V2.0)

## Introduction and Aim

Welcome to Apollo, a timetable organiser made for the average NUS students by NUS students!
Apollo is a desktop app for managing your tasks, events, deadlines and modules optimized for use via a Command Line
Interface (CLI) .
If you can type fast, Apollo can get your timetable management done faster than traditional GUI apps.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Features](#features)
    + [Task Commands](#task-commands)
    + [Module Commands](#module-commands)
    + [Common Commands](#common-commands)
    + [Loading and saving of data](#loading-and-saving-of-data)

3. [Command summary](#command-summary)
4. [FAQ](#faq)

## Quick Start

1. Ensure that you have Java `11` or above installed on your Personal Computer.
2. Download the latest `tp.jar` from [here]().
3. Copy the file to the folder you want to use as the home folder for your Apollo. This folder must be *empty*.
4. Open a command terminal, cd into the directory you put the jar file in and run the command `java -jar tp.jar`.
5. On opening, Apollo should greet you with this:

```
____________________________________________________________
Hello from
 ____    ____    _____  __      __       _____
|  _  | |  _ \  | ___ | | |     | |     | ___ |
| |_| | | |_| | | | | | | |     | |     | | | |
| | | | |  __/  | |_| | | |___  | |___  | |_| |
|_| |_| |_|     \_____/ |_____| |_____| \_____/
Enter "help" to see a list of commands.
____________________________________________________________

```

## Command Summary

|         Action          |              Format              |
|:-----------------------:|:--------------------------------:|
|       List Tasks        |              `list`              |
|          Todo           |           `todo TASK`            |
|        Deadline         |     `deadline TASK /by DATE`     |
|          Event          | `event TASK /from DATE /to DATE` |
|          Mark           |            `mark IDX`            |
|         Unmark          |           `unmark IDX`           |
|       Delete Task       |           `delete IDX`           |
| Find Tasks with Keyword |          `find KEYWORD`          |
|   Find Tasks on Date    |           `date DATE`            |
|      List Modules       |            `listmod`             |
|       Add Module        |       `addmod MODULE_CODE`       |
|      Delete Module      |           `delmod IDX`           |
| Show Module Information |            `showmod`             |
|          Help           |              `help`              |
 |     Weekly Schedule     |              `week`              |
|           Bye           |              `bye`               |

> Notes about the command format:
> + Words in `UPPER_CASE` are the parameters to be supplied by the user.
> > e.g. in 'todo TASK', `TASK` is a parameter that can be used as `todo read book`.
> + `DATE`s should be input in the format `yyyy-MM-ddThh:mm`.
> > e.g. `deadline read book /by 2023-10-30T23:59` sets a deadline for Oct 20 2023, 11:59PM
> + Tasks that have occurred prior to the current date cannot be added. 
> + `IDX` can be obtained by using `list` for tasks or `listmod` for modules.  
> + By default, all newly added tasks are not completed.

## Features

## *Task Commands*

### `list` - Listing all saved tasks

Shows a numbered list of all tasks (Todos, Events, Deadlines) in Apollo. `list` automatically sorts the tasks by type, 
then date within each type.

Format: `list`

```
>> list
You have a total of 7 tasks in your tasklist.
Here are the tasks in your list:
1.[D][ ] submit tutorial (by: Apr 01 2023, 11:59PM)
2.[D][ ] submit tutorial (by: May 01 2023, 11:59PM)
3.[D][ ] submit tutorial (by: May 03 2023, 11:59PM)
4.[E][ ] lecture (from: Apr 03 2023, 09:00AM to: May 03 2023, 11:00AM)
5.[E][ ] lecture (from: Apr 04 2023, 09:00AM to: May 03 2023, 11:00AM)
6.[E][ ] lecture (from: May 03 2023, 09:00AM to: May 03 2023, 11:00AM)
7.[T][ ] eat lunch
There are 7 unmarked tasks in your tasklist.
```

### `todo` - Adding a ToDo

Adds a normal task to Apollo.

Format: `todo TASK`

```
>> todo Feed the fish
Got it. I've added this todo:
   [T][ ] Feed the fish
```

### `deadline` - Adding a Deadline

Adds a task with a due date to Apollo. 
If deadline clashes with any event or lesson type you will be alerted through a warning message. 
However, you will still be able to add it into the tasklist. 

Format: `deadline TASK /by DATE`

> Note: `DATE` must be entered in the format `yyyy-MM-ddThh:mm`.

```
>> deadline submit tutorial /by 2023-03-30T23:59
Got it. I've added this deadline:
  [D][ ] submit tutorial (by: Mar 30 2023, 11:59PM)
```

### `event` - Adding an Event

Adds a task with a start and end date to Apollo.
If there is an event in the tasklist that is clashing with any event added previously a warning message will be printed. 
However, you will still be able to add it. 

Format: `event TASK /from DATE /to DATE`

> Note: `DATE` must be entered in the format `yyyy-MM-ddThh:mm`.

```
>> event holiday /from 2023-03-25T00:00 /to 2023-03-30T23:59
Got it. I've added this event:
  [E][ ] holiday (from: Mar 25 2023, 12:00AM to: Mar 30 2023, 11:59PM)
```

### `mark` - Marking done

Marks the specified task as completed.

Format: `mark IDX`

> Note: `IDX` can be obtained by using `list` to find the task's index.

```
>> mark 4
Nice!, I've marked this task as done:
[T][X] Feed the fish
```

### `unmark` - Marking not done

Marks the specified task as not completed.  

Format: `unmark IDX`

> Note: `IDX` can be obtained by using `list` to find the task's index.

```
>> unmark 4
OK, I've marked this task as not done yet:
  [T][ ] Feed the fish
```

### `delete` - Deleting a task

Deletes the specified task from Apollo.

Format: `delete IDX`

> Note: `IDX` can be obtained by using `list` to find the task's index.

```
>> delete 4
Noted, I've removed this task:
  [T][ ] Feed the fish
Now you have 3 tasks in the list
```

### `find` - Finding a task

Shows all tasks in Apollo that contain the specified keyword.

Format: `find KEYWORD`

```
>> find tutorial
Here are the matching tasks in your list:
1.[D][ ] submit tutorial (by: Mar 30 2023, 11:59PM)
```

### `date` - Find tasks on date

Shows all tasks in Apollo that occur on the specified date.

Format: `date DATE`

> Note: `DATE` should be entered in the format `yyyy-MM-dd`.

```
>> date 2023-03-30
Here are the tasks happening on Mar 30 2023:
1.[E][ ] holiday (from: Mar 25 2023, 12:00AM to: Mar 30 2023, 11:59PM)
2.[D][ ] submit tutorial (by: Mar 30 2023, 11:59PM)
```

## *Module Commands*

### `listmod` - Listing all modules

Shows a list of all modules in Apollo. It will also show the total number of modular credits you have in this semester.
The list will be automatically sorted in alphabetical order according to EduRec standards.
Format: `listmod`

```
>> listmod
You are taking 3 module(s) this semester:
1.CG1111A: Engineering Principles and Practice I (4 MCs)
2.CS2113: Software Engineering & Object-Oriented Programming (4 MCs)
3.DTK1234: Design Thinking (4 MCs)
Total modular credits you have in this semester: 12
```

### `addmod` - Adding a module

Adds a module to Apollo.

Format: `addmod MODULE_CODE`
> Note: `MODULE_CODE` can be either uppercase or lowercase. 
```
>> addmod cs2113
Got it. I've added this module:
  CS2113: Software Engineering & Object-Oriented Programming
Total modular credits you have in this semester: 12
Enter "addmod CS2113 -[FLAG] [LESSON NUMBER]" to add lessons for this module.
Here are the lesson types for this module:
Lecture (-lec)
Tutorial (-tut)
```
#### `addmod` flags
There are many lesson options and types, the below is a list of all the flags and their respective lesson types.
```
-lec        LECTURE
-plec       PACKAGED LECTURE
-st         SECTIONAL TEACHING
-dlec       DESIGN LECTURE
-tut        TUTORIAL
-ptut       PACKAGED TUTORIAL
-rcit       RECITATION
-lab        LABORATORY
-ws         WORKSHOP
-smc        SEMINAR STYLE MODULE CLASS
-mp         MINI PROJECT
-tt2        TUTORIAL TYPE 2
```

To add a lesson, use the following format:
`addmod MODULE_CODE -FLAG LESSON_NUMBER`

Example:
```
addmod CS1010 -st 1
```
This will add the first section teaching lesson of CS1010 to your module list.
If this lesson clashes with any of your other lessons a warning message will be displayed but you will still be able to add it.

### `delmod` - Deleting a module

Removes a module from Apollo. Can be done using either IDX or MODULE_CODE. 

#### `delmod` IDX
> Note: `IDX` can be obtained by using `listmod` to find the module's index.   
> We assume that each user will take no more than the number of mods available in the current semester. 

Format: `delmod IDX`

```
>> delmod 1
Got it, removed CS2113 from your Module list.
```

#### `delmod` MODULE_CODE
Format: `delmod MODULE_CODE`

```
>> delmod dtk1234
Got it, removed DTK1234 from your Module list.
Total modular credits you have in this semester: 8
```

#### `delmod` flags
The lesson types and their corresponding guide are the same as `addmod` flags.

To delete a lesson, use the following format:
`delmod MODULE_CODE -FLAG LESSON NUMBER`

```
>> delmod CS1010 -st 1
Deleting lessons for module: CS1010
Lessons deleted: SECTIONAL TEACHING - 1
```

### `showmod` - Show information of a module

Shows the information of a module.
Format: `showmod MODULE_CODE`

```
>> showmod cs1231
Here are the lesson types for this module:
Sectional Teaching (-st)
Tutorial (-tut)
Number of MC: 4
```
#### `showmod` flags
The lesson types and their corresponding guide are the same as `addmod` flags.
To show the information on a lesson, use the following format:
`showmod MODULE_CODE -FLAG`

```
>> showmod CS1010 -st
Here are all available lessons of type: SECTIONAL_TEACHING for CS1010:
Class Number: 1
   Monday 1200 - 1400
```

The ordering of lessons in the list are sorted as follows:

1. Lesson Type
2. Lesson Number (lexicographically)
3. Lesson Day and time

## *Common Commands*

### `help` - Viewing help

Shows a menu of commands available in Apollo and their usage, as well as their required format/parameters.

Format: `help`

### `week` - Viewing weekly schedule

Shows a list of all lessons and tasks occurring during the current week (Mon to Sun).   
Format: `week`

### `bye` - Exiting the program

Format: `exit`

```
>> bye 
Bye. Hope to see you again soon!
```


## *Loading and saving of data*

Apollo automatically loads up your todo and module lists on start-up.

After any command that changes the data, Apollo will save the changes into your hard disk automatically.
No need to save manually!

The save file for your tasks is located at save.txt within the *home folder* for Apollo.
The save file for your modules is located at moduleData.txt within the *home folder* for Apollo.
If either file is corrupted, Apollo will show you a warning before the welcome message.





