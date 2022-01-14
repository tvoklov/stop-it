# stop-it
An extremely barebones and basic app for tracking fails while battling addiction.

Example config is provided in [config.conf](config.conf).

To just run this, either:
 1. add your own config to the [resources](src/main/resources) folder (name it `application.conf`)
 2. `sbt run`

or (the lesser-of-two-evils way):
 1. `sbt assembly`
 2. `java -Dconfig="[config file path]" -jar ./target/scala-2.13/stopit.jar`

This method of running should only be used for testing and "will i even like it" purposes. Because of this I did not fix any bugs/weirdness that come out when the app is run this way. For example, on windows machines for some reason <C-c> won't stop the jvm, so after testing it out just find the process in your Task Manager and end it there.

If you want to actually run this app (like on a home server or in the background on your pc), then I would recommend using docker:
 1. `sbt assembly`
 2. `docker build . -t stop-it`
 4. change the mounting folder in `docker-compose.yml`
 5. copy the docker image and `docker-compose.yml` to whatever machine you want to run it on
 6. docker-compose up

A fair warning: this app was basically me learning how a stack of `[react + http4s + cats effect + docker]` could work, so it might not get updated like ever, have bugs, and in general just not work at all.
