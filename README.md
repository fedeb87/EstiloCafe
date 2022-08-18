# EstiloCafe

EstiloCafe is an application that improves sales and profits for a Cofee shop. Also this application can be customize for any commerce.
This app are using many tools and libraries designed to build robust and testable software all together. Also it follow the mvvm architecture, implement RXJava to do asynchronous work, and are fully tested.

## Tech Stack

This project uses [feature modularization architecture](https://proandroiddev.com/intro-to-app-modularization-42411e4c421e) and uses MVVM as software design patter.

## Before you start
This project requires the following

 1. Android Studio 4.2 (stable channel) or higher.
 2. Android SDK 21 or above.
 3. Android SDK build tools 21.0.0 or above.

## Screenshots
The screenshot below shows how the app looks like when it is done.

<img width="300" alt="SC1" src="https://i.imgur.com/QbWnzV1.png">   <img width="300" alt="SC1" src="https://i.imgur.com/nI4tJrz.png">   <img width="300" alt="SC1" src="https://i.imgur.com/YY7GNj0.png">   <img width="300" alt="SC1" src="https://i.imgur.com/rZQ1sy4.png">   <img width="300" alt="SC1" src="https://i.imgur.com/h9Vkqtr.png">

### Libraries

 - Application entirely written in Java.
 - Asynchronous processing using [RXJava](https://reactivex.io/).
 - [JavaxEmail](https://javaee.github.io/javamail/) to send commerce notifications.
 - [Room](https://developer.android.com/training/data-storage/room) for local data storage.
 - [Dagger](https://dagger.dev/) for dependecy injection.
 - Uses [JUnit4](https://developer.android.com/training/testing/junit-rules), [Espresso](https://developer.android.com/training/testing/espresso), [Robolectric](http://robolectric.org/) among other libraries for unit & instrumented tests.

## Notes
This application consumes Firebase as a backend. Particulary Firestore, Auth and Firebase Storage. Hence for run EstiloCafe you need a Firebase account and import the google-services.json into the project. To do this follow [this](https://firebase.google.com/docs/android/setup?hl=es) instructions

To make sending email service work, you need to setting an email account in EmailUtils class.

On the other hand, you have total freedom to extend the current functionalities and help me to solve any bug that you find :)

## 📃 License

```
Copyright 2022 Federico Beron

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```