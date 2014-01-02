Play Tech Demo
==============

Play framework demo app using:

* Play Framework - Uses Play for Java, web services, flash and session scopes
* Social Integration using Rest FB - To interact with FB graph APIs
* Heroku - Deployement of app
* Jquery UI - For sliders
* Bootstrap - Basic out of box UI

App is currently hosted on Heroku app [here](http://play-tech-demo.herokuapp.com)

In case you need to clear your session use [this url](http://play-tech-demo.herokuapp.com/clearsession).

What it does
------------
Main goal of this project if to demonstrate use of various technologies to solve a problem which in this case would be "Acknowledging Facebook birthday wishes".

Following are different stages application goes through-

###Stage 1
Application is written to let users log in using their Facebook accounts, application then goes through their birthday wishes. the way it does is as follows - application reads post feed from 1 day prior to user birth date and moves forward till day next to birthday. Regular expression is used to detect whether message in a post is a birthday wish or not. 
### Stage 2
If application fails to detect some posts as birthday wishes, it asks User to identify those posts. After user selects and marks posts as birthday wishes, user can select how to thank back for those wishes. e.g. For now application is hard coded to ask user to distribute thanks in 3 categories:

* Thank by Like to a post
* Thank by replying "Thanks a lot"
* Thank by replying "Thank you"
### Stage 3
Now that application knows distribution if replies, all left to do is actually make calls to Facebook graph API. Internally, application uses bulk APIs to achieve this. After all posts are "ThankBack'ed", application gives up permission acquired from user to use his Facebook account.

Posts which were already acknowledged by either like'ing it or replying to it are skipped automatically.

Deployment
-----------
Heroku is used as a PaaS vendor to deploy this application. Heroku config variables are used to configure and use App ID and App Secret for Facebook application.