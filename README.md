# Cheapbook - A cheap Jupyter Notebook alternative

This project was developed during the Scala course at HEIG-VD by Guillaume Hochet, Florent Piller and Kamil Amrani

## What does it do
Currently nothing but this readme is worth the time you lost isnt it? :)

### Purpose
The objective is to implement a simple and basic Scala sandbox environment, like the one Jupyter provides. It would primarly consist of a web based interface allowing developers to configure the environment (provide dependencies for example) and then offer a simple code editor to script that environment and receive generated output. 

The developer could choose between various "saved" environment (ones that could be dynamically built from database), load them and start scripting.

```
|----------|    |---------------------------|    |--------------------------|    |--------------------|
|Create env| -> |Add dependencies (maven...)| -> |Env saved to DB and loaded| -> |Dev starts scripting|
|----------|    |---------------------------|    |--------------------------|    |--------------------|
```
The project would be useless without the possibility to feed the environment with dependencies or external data. The system will thus ship with tools to load Maven dependencies (or from similar repos) and/or external data like Json files, CSV tables...

## Implementation details
- Backend will be using the Play! Framework and expose a Rest API (the most suitable for the given usecase, GraphQL would be overkill), completely client agnostic
- Frontend will be made using Vue.JS
- We will probably use MySQL as database engine to allow the use of the Slick ORM
- In order to run environments and retrieve output we will probably simple run external process. We thought about using an interpreter like twitter-eval or scala-compiler but it would be very troublesome to load dependencies.

## Environment sandboxing
The system will work using some docker containers. Creating an environment will dynamically create a docker image which will pull correct dependencies and such before being started.
1. User creates a new environment, an entry is created in database
2. User adds some dependencies, model updated
3. User launches an environment, a docker image is created and run
4. User writes some code and executes is, code is sent to docker container, executed, output is retrieved and presented back to user
5. User adds or remove dependencies, stops docker container, add dependencies, go back to point 3
