# stop-it
An extremely barebones and basic app for tracking fails while battling addiction.

Quick FAQ:
1. It looks like this:
   ![preview](stuff/pictures/preview.png)
2. The default port (8080) can be changed in config (`port=[your desired port`])
3. The default name ("Stop") can be changed in config (`app-name="[your app name]")
4. Example config is provided in [config.conf](config.conf)

### How to run

Either:
 1. add your own config to the [resources](src/main/resources) folder (name it `application.conf`)
 2. `sbt run`

or:
 1. `sbt assembly`
 2. `java -Dconfig="[config file path]" -jar ./target/scala-2.13/stopit.jar`

However, these methods of running should only be used for testing and "will i even like it" purposes. Because of this I did not fix any bugs/weirdness that come out when the app is run any of these ways. For example, on windows machines for some reason <C-c> won't stop the jvm when ran using the second method, so after testing it out just find the process in your Task Manager and end it there.

If you want to actually run this app (like on a home server or in the background on your pc), then I would recommend using docker:
 1. `sbt assembly`
 2. `docker build . -t stop-it`
 4. change the mounting folder in `docker-compose.yml`
 5. copy the docker image and `docker-compose.yml` to whatever machine you want to run it on
 6. docker-compose up

A fair warning: this app was basically me learning how a stack of `[react + http4s + cats effect + docker]` could work, so it might not get updated like ever, have bugs, and in general just not work at all.

### Tech things (most important of them)

backend:
 - lang: `scala`
 - build tool: `sbt`
 - server: `http4s`
 - "db": a very dirty and stupid implementation of an append-only list of json objects stored in pure text format. i don't plan on changing this
 
frontend:
 - `react` for elements
 - `foundation` for css
 - `webpack` for compiling jsx and adding helper libraries, like moment-js

to develop run alongside each other:
 1. in ./frontend: `npm run dev`
 2. in ./: `sbt run`

 I tried doing continuous compilation using new sources, but it just didn't work. With the way IOApps work, the threads it creates don't shut down quick enough. I might figure out another way to fix this, but for now simply cancelling `sbt run` and running it again is the only way I know of.
