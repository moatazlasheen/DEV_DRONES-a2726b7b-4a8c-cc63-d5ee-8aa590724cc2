## Drones

[[_TOC_]]

---

:scroll: **START**


### Introduction

There is a major new technology that is destined to be a disruptive force in the field of transportation: **the drone**. Just as the mobile phone allowed developing countries to leapfrog older technologies for personal communication, the drone has the potential to leapfrog traditional transportation infrastructure.

Useful drone functions include delivery of small items that are (urgently) needed in locations with difficult access.

---

### Task description

We have a fleet of **10 drones**. A drone is capable of carrying devices, other than cameras, and capable of delivering small loads. For our use case **the load is medications**.

A **Drone** has:
- serial number (100 characters max);
- model (Lightweight, Middleweight, Cruiserweight, Heavyweight);
- weight limit (500gr max);
- battery capacity (percentage);
- state (IDLE, LOADING, LOADED, DELIVERING, DELIVERED, RETURNING).

Each **Medication** has: 
- name (allowed only letters, numbers, ‘-‘, ‘_’);
- weight;
- code (allowed only upper case letters, underscore and numbers);
- image (picture of the medication case).

Develop a service via REST API that allows clients to communicate with the drones (i.e. **dispatch controller**). The specific communicaiton with the drone is outside the scope of this task. 

The service should allow:
- registering a drone;
- loading a drone with medication items;
- checking loaded medication items for a given drone; 
- checking available drones for loading;
- check drone battery level for a given drone;

> Feel free to make assumptions for the design approach. 

---

### Requirements

While implementing your solution **please take care of the following requirements**: 

#### Functional requirements

- Prevent the drone from being loaded with more weight that it can carry;
- Prevent the drone from being in LOADING state if the battery level is **below 25%**;
- Introduce a periodic task to check drones battery levels and create history/audit event log for this;
- There is no need for UI.

---

#### Non-functional requirements

- The project must be buildable and runnable;
- The project must have Unit tests;
- The project must have a README file with build/run/test instructions (use a DB that can be run locally, e.g. in-memory, via container);
- Any data required by the application to run (e.g. reference tables, dummy data) must be preloaded in the database;
- Input/output data must be in JSON format;
- Use a framework of your choice, but popular, up-to-date, and long-term support versions are recommended.

---

Solution 
--------
- Language : Java 17
- Framework : Spring boot
- Build Tool : Maven
- build command : mvn clean package
- Testing frameworks : JUnit, Mockito 
- DB : H2 - in memory database
- SQL Scripts : No need, the service will create the tables and there is no need to fill them
- Database console : http://localhost:8080/h2-console
- Swagger link : http://localhost:8080/swagger-ui/index.html

sample requests, i used git bash to run these curl commands, generetad by postman

Register drone 
==============

curl --location 'http://localhost:8080/api/v1/drones' \
--header 'Content-Type: application/json' \
--data '{
"serial" : "A6",
"batteryCapacityPercentage" : 25,
"droneState" : "IDLE",
"weightLimitGrm": 500,
"model" : "LIGHT_WEIGHT"
}'

Register Medication
===================

curl --location 'http://localhost:8080/api/v1/medication' \
--header 'Content-Type: application/json' \
--data '{
"name" : "A1",
"weight" : 98,
"code" : "CODE_1",
"base64Image": "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"
}'

Get Available Drones for loading
===============================

curl --location --request GET 'http://localhost:8080/api/v1/drones/available'

Load drone with medications
===========================
curl --location 'http://localhost:8080/api/v1/drones/1/load-medications' \
--header 'Content-Type: application/json' \
--data '{
"medications" : [1]
}'

GET Drone info with load
========================
curl --location --request GET 'http://localhost:8080/api/v1/drones/1'

GET Drone Battery Level
======================
curl --location 'http://localhost:8080/api/v1/drones/1/battery-level'

:scroll: **END** 
