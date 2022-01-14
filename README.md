# stop-it
An extremely barebones and basic app for tracking fails while battling addiction

Example config is provided in [config.conf](config.conf)

To run:
 1. sbt assembly
 2. java -Dconfig="[where your config is]" -jar target/scala-2.13/stopit.jar

If you want to run it in a docker container, then what I do is:
 1. sbt assembly
 2. docker build . -t stop-it
 3. copy over the docker image and docker-compose.yml to whatever machine you want to run it on
 4. docker-compose up

A fair warning: this app was basically me learning how a stack of [react + http4s + cats effect + docker] could work, so it might not get updated like ever.
